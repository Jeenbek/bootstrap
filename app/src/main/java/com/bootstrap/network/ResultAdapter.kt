package com.bootstrap.network

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import okhttp3.Request
import okio.Buffer
import okio.BufferedSource
import okio.Timeout
import retrofit2.*
import timber.log.Timber
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.nio.charset.Charset


class ResultAdapter(
    private val type: Type
) : CallAdapter<Type, Call<Result<Type>>> {
    override fun responseType() = type
    override fun adapt(call: Call<Type>): Call<Result<Type>> = ResultCall(call)

    class Factory : CallAdapter.Factory() {
        override fun get(
            returnType: Type,
            annotations: Array<Annotation>,
            retrofit: Retrofit
        ) = when (getRawType(returnType)) {
            Call::class.java -> {
                val callType = getParameterUpperBound(0, returnType as ParameterizedType)
                when (getRawType(callType)) {
                    Result::class.java -> {
                        val resultType = getParameterUpperBound(0, callType as ParameterizedType)
                        ResultAdapter(resultType)
                    }
                    else -> null
                }
            }
            else -> null
        }
    }

    private abstract class CallDelegate<TIn, TOut>(
        protected val proxy: Call<TIn>
    ) : Call<TOut> {
        override fun execute(): Response<TOut> = throw NotImplementedError()
        final override fun enqueue(callback: Callback<TOut>) = enqueueImpl(callback)
        final override fun clone(): Call<TOut> = cloneImpl()
        override fun cancel() = proxy.cancel()
        override fun request(): Request = proxy.request()
        override fun isExecuted() = proxy.isExecuted
        override fun isCanceled() = proxy.isCanceled
        abstract fun enqueueImpl(callback: Callback<TOut>)
        override fun timeout(): Timeout = proxy.timeout()
        abstract fun cloneImpl(): Call<TOut>
    }

    private class ResultCall<T>(proxy: Call<T>) : CallDelegate<T, Result<T>>(proxy) {
        override fun cloneImpl() = ResultCall(proxy.clone())
        override fun enqueueImpl(callback: Callback<Result<T>>) =
            proxy.enqueue(object : Callback<T> {
                override fun onResponse(call: Call<T>, response: Response<T>) {
                    val result =
                        if (response.isSuccessful) Result.Success(response.body()!!)
                        else {
                            val exception = extractErrorMessage(response)
                            Result.Error(exception ?: Exception("code: ${response.code()}"))
                        }
                    if (result is Result.Error) Timber.e(result.e)
                    callback.onResponse(this@ResultCall, Response.success(result))
                }

                override fun onFailure(call: Call<T>, t: Throwable) {
                    Timber.e(t)
                    val result = Result.Error(Exception(t))
                    callback.onResponse(this@ResultCall, Response.success(result))
                }
            })
    }
}

private fun <T> extractErrorMessage(response: Response<T>) : ErrorBody? {
    try {
        val errorBody = ErrorBody()
        val body = response.errorBody()
        if (body != null) {
            val source: BufferedSource = body.source()
            source.request(Long.MAX_VALUE)

            val buffer: Buffer = source.buffer()
            val charset: Charset =
                body.contentType()?.charset(Charset.forName("UTF-8"))!!
            val json: String = buffer.clone().readString(charset)
            val obj = JsonParser.parseString(json)
            if (obj is JsonObject && obj.has("status")) {
                val errorCode = obj["status"].asInt
                errorBody.code = errorCode
            }
            if (obj is JsonObject && obj.has("message")) {
                val errorMessage = obj["message"].asString
                errorBody.message = errorMessage
            }
            return errorBody
        }
    } catch (e: java.lang.Exception) {
        Timber.e("Error message parse exception -> $e")
    }
    return null
}

data class ErrorBody(
    var code: Int? = null,
    override var message: String? = null
) : java.lang.Exception()