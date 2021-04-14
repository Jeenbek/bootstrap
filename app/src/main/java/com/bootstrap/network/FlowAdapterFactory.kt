package com.bootstrap.network

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.suspendCancellableCoroutine
import retrofit2.*
import timber.log.Timber
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class FlowAdapterFactory : CallAdapter.Factory() {
    override fun get(
        returnType: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): CallAdapter<*, *>? {
        if (getRawType(returnType) != Flow::class.java) return null
        check(returnType is ParameterizedType) { "Flow must be parametrized. (Flow<T>)" }
        val responseType = getParameterUpperBound(0, returnType)
        return when (getRawType(responseType)) {
            Response::class.java -> {
                check(responseType is ParameterizedType) {
                    "Response must be parametrized. (Flow<Response<T>>)"
                }
                ResponseAdapter<Any>(getParameterUpperBound(0, responseType))
            }
            Result::class.java -> {
                check(responseType is ParameterizedType) {
                    "Result must be parametrized. (Flow<Result<T>>)"
                }
                ResultAdapter<Any>(getParameterUpperBound(0, responseType))
            }
            else -> {
                BodyAdapter<Any>(responseType)
            }
        }

    }

    private class ResponseAdapter<T>(private val responseType: Type) :
        CallAdapter<T, Flow<Response<T>>> {
        override fun adapt(call: Call<T>): Flow<Response<T>> = flow {
            emit(
                suspendCancellableCoroutine<Response<T>> { continuation ->
                    call.enqueue(object : Callback<T> {
                        override fun onFailure(call: Call<T>, t: Throwable) {
                            Timber.e(t)
                            continuation.resumeWithException(t)
                        }

                        override fun onResponse(call: Call<T>, response: Response<T>) {
                            continuation.resume(response)
                        }
                    })
                    continuation.invokeOnCancellation { call.cancel() }
                }
            )
        }

        override fun responseType() = responseType
    }

    private class ResultAdapter<T>(private val responseType: Type) :
        CallAdapter<T, Flow<Result<T>>> {
        override fun adapt(call: Call<T>): Flow<Result<T>> = flow {
            emit(Result.Loading)
            emit(
                suspendCancellableCoroutine<Result<T>> { continuation ->
                    call.enqueue(object : Callback<T> {
                        override fun onFailure(call: Call<T>, t: Throwable) {
                            Timber.e(t)
                            continuation.resume(Result.Error(Exception(t)))
                        }

                        override fun onResponse(call: Call<T>, response: Response<T>) {
                            try {
                                val body = response.body()!!
                                if (body is List<*> && body.isNullOrEmpty()) {
                                    continuation.resume(Result.Empty)
                                } else {
                                    continuation.resume(Result.Success(body))
                                }

                            } catch (e: Exception) {
                                Timber.e(e)
                                continuation.resume(Result.Error(e))
                            }
                        }
                    })
                    continuation.invokeOnCancellation { call.cancel() }
                }
            )
        }

        override fun responseType() = responseType
    }

    private class BodyAdapter<T>(private val responseType: Type) :
        CallAdapter<T, Flow<T>> {
        override fun adapt(call: Call<T>): Flow<T> = flow {
            emit(
                suspendCancellableCoroutine<T> {
                    call.enqueue(object : Callback<T> {
                        override fun onFailure(call: Call<T>, t: Throwable) {
                            Timber.e(t)
                            it.resumeWithException(t)
                        }

                        override fun onResponse(call: Call<T>, response: Response<T>) {
                            try {
                                it.resume(response.body()!!)
                            } catch (e: Exception) {
                                Timber.e(e)
                                it.resumeWithException(e)
                            }
                        }
                    })
                    it.invokeOnCancellation { call.cancel() }
                }
            )
        }

        override fun responseType() = responseType
    }
}