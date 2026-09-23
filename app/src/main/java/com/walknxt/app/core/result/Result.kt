package com.walknxt.app.core.result

sealed class Result<out T, out E : AppError> {
    data class Success<out T>(val data: T) : Result<T, Nothing>()
    data class Error<out E : AppError>(val error: E) : Result<Nothing, E>()
}

interface AppError

sealed class TrackingError : AppError {
    object PermissionRequired : TrackingError()
    object SensorUnavailable : TrackingError()
    object LocationUnavailable : TrackingError()
    object InvalidState : TrackingError()
    object Interrupted : TrackingError()
    data class Unknown(val message: String) : TrackingError()
}

sealed class PersistenceError : AppError {
    object DatabaseError : PersistenceError()
    object NotFound : PersistenceError()
}
