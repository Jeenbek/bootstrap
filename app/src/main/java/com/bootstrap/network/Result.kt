package com.bootstrap.network

sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val e: Exception) : Result<Nothing>()
    object Loading : Result<Nothing>()
    object Empty : Result<Nothing>()
}

fun <T> Result<T>.onSuccess(block: (T) -> Unit) {
    if (this is Result.Success) block(this.data) else Unit
}

val <T> Result<T>.data: T? get() = (this as? Result.Success<T>)?.data

fun <T, R> Result<T>.map(transform: (T) -> R): Result<R> {
    return when(this) {
        is Result.Success -> Result.Success(transform(data)).run {
            if (data is List<*> && data.isNullOrEmpty()) Result.Empty
            else this
        }
        is Result.Error -> this
        Result.Loading -> Result.Loading
        Result.Empty -> Result.Empty
    }
}