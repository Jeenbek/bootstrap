package com.bootstrap.model

import java.io.Serializable

data class Message(
    val message: String,
    val message_duration: Int? = null,
    var type: Type = Type.WARNING
) : Serializable {
    enum class Type : Serializable { SUCCESS, WARNING, ERROR, IDENTITY_VERIFICATION, UPDATE_APP, UNAVAILABLE_FOR_LEGAL_REASONS }
}