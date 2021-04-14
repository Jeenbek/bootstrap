package com.bootstrap.extensions

import android.content.Intent
import java.io.Serializable

fun Intent.put(vararg arguments: Serializable) = arguments.forEach {
    putExtra(it::class.qualifiedName.toString(), it)
}
