package com.hacksync.general.utils

object Environment {
    val isDebug: Boolean
        get() = System.getenv("DEBUG")?.toBoolean() ?: false

    val isProduction: Boolean
        get() = !isDebug
} 