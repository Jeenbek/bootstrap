package com.bootstrap.network

import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class ResultFlowAdapter<T>(private val type: Type) : CallAdapter<T, ResultFlow<T>> {
    override fun responseType(): Type = type
    override fun adapt(call: Call<T>): ResultFlow<T> = ResultFlow(call)

    class Factory : CallAdapter.Factory() {
        override fun get(
            returnType: Type,
            annotations: Array<Annotation>,
            retrofit: Retrofit
        ): CallAdapter<*, *>? {
            if (getRawType(returnType) != ResultFlow::class.java) return null
            val responseType = getParameterUpperBound(0, returnType as ParameterizedType)
            return ResultFlowAdapter<Any>(responseType)
        }
    }
}