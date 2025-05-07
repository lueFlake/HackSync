package com.hacksync.general.repositories.interfaces

import java.time.Instant

interface RevokedTokenRepository {
    fun revoke(tokenHash: String, expiresAt: Instant)
    fun isRevoked(tokenHash: String): Boolean
    fun cleanupExpired()
}
