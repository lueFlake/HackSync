// src/main/kotlin/com/hacksync/general/utils/PasswordHashing.kt
package com.hacksync.general.utils

import org.mindrot.jbcrypt.BCrypt

object PasswordHashing {
    fun hash(password: String): String {
        return BCrypt.hashpw(password, BCrypt.gensalt())
    }

    fun verify(password: String, hashedPassword: String): Boolean {
        return BCrypt.checkpw(password, hashedPassword)
    }
}