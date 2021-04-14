package com.bootstrap.network

import android.content.Context
import android.content.Intent
import com.cardbazaar.domain.usecases.LogoutUseCase
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.bootstrap.AppActivity
import com.bootstrap.BuildConfig
import com.bootstrap.extensions.put
import com.bootstrap.manager.SharedPreferencesManager
import com.bootstrap.model.Message
import kotlinx.coroutines.CoroutineExceptionHandler
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.create
import timber.log.Timber

val networkModule = module {
    single {
        CoroutineExceptionHandler { context, throwable -> Timber.e(throwable, context.toString()) }
    }
    single {
        GsonBuilder().create()
    }
    single {
        val sharedPreferencesManager: SharedPreferencesManager by inject()
        val okHttpBuilder = OkHttpClient.Builder().apply {
            addInterceptor { chain ->
                val request = chain.request()
                val ongoing = request.newBuilder()
                    .addHeader("Authorization", "Bearer " + sharedPreferencesManager.token)
                val response: Response = chain.proceed(ongoing.build())
                if (response.code == 401) get<LogoutUseCase>()()
                val mediaType = response.body?.contentType()
                if (mediaType?.subtype == "json") {
                    val content = response.body?.string() ?: ""
                    val message = try {
                        val message = get<Gson>().fromJson(content, Message::class.java)
                        message.type = when (response.code) {
                            in 200 until 300 -> Message.Type.SUCCESS
                            in 400 until 500 -> when (response.code) {
                                else -> Message.Type.WARNING
                            }
                            else -> Message.Type.ERROR
                        }
                        message
                    } catch (e: Exception) {
                        Timber.e("Error when extract Message")
                        null
                    }
                    if (message?.message?.isNotEmpty() == true) {
                        val context: Context = get()
                        val intent = Intent(context, AppActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
                        intent.put(message)
                        context.startActivity(intent)
                    }
                    response.newBuilder().body(content.toResponseBody(mediaType)).build()
                } else response.newBuilder().build()
            }
        }
        if (BuildConfig.DEBUG) {
            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            okHttpBuilder.addInterceptor(loggingInterceptor)
        }
        okHttpBuilder.build()
    }

    single {
        val retrofitBuilder = Retrofit.Builder()
        retrofitBuilder
            .baseUrl(BuildConfig.SERVER_URL)
            .client(get())
            .addConverterFactory(GsonConverterFactory.create(get()))
            .addConverterFactory(ScalarsConverterFactory.create())
            .addCallAdapterFactory(ResultAdapter.Factory())
            .addCallAdapterFactory(FlowAdapterFactory())
            .addCallAdapterFactory(ResultFlowAdapter.Factory())
        retrofitBuilder.build()
    }
    single { get<Retrofit>().create<Api>() }
}