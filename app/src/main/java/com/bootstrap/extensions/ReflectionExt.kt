package com.bootstrap.extensions

inline fun <reified T> Any?.useField(name: String? = null, block: (T) -> Unit) = try {
    val field = this!!.javaClass.declaredFields.first {
        if (name == null) it.type.isAssignableFrom(T::class.java) else it.name == name
    }
    val oldAccessibility = field.isAccessible
    field.isAccessible = true
    val obj = field.get(this) as T
    block(obj)
    field.isAccessible = oldAccessibility
} catch (e: Exception) {
    e.printStackTrace()
}