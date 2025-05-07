package com.hacksync.general.repositories

import com.hacksync.general.repositories.interfaces.RevokedTokenRepository

import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate
import java.time.Instant
import java.util.*

interface JdbiRevokedTokenRepository: RevokedTokenRepository  {
    @SqlUpdate("""
        INSERT INTO revoked_tokens (token_hash, expires_at)
        VALUES (:tokenHash, :expiresAt)
        ON CONFLICT (token_hash) DO NOTHING
    """)
    override fun revoke(
        @Bind("tokenHash") tokenHash: String,
        @Bind("expiresAt") expiresAt: Instant
    )

    @SqlQuery("""
        SELECT EXISTS(
            SELECT 1 FROM revoked_tokens
            WHERE token_hash = :tokenHash
            AND expires_at > NOW()
        )
    """)
    override fun isRevoked(@Bind("tokenHash") tokenHash: String): Boolean

    @SqlUpdate("""
        DELETE FROM revoked_tokens
        WHERE expires_at <= NOW()
    """)
    override fun cleanupExpired()

    @SqlQuery("""
        SELECT COUNT(*) FROM revoked_tokens
        WHERE token_hash = :tokenHash
        AND expires_at > NOW()
    """)
    fun countValid(@Bind("tokenHash") tokenHash: String): Int

    @SqlUpdate("""
        DELETE FROM revoked_tokens
        WHERE token_hash = :tokenHash
    """)
    fun removeRevocation(@Bind("tokenHash") tokenHash: String)
}