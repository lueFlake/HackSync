package com.hacksync.general.repositories

import com.hacksync.general.entities.User
import com.hacksync.general.mapping.UserMapper
import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.customizer.BindBean
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate
import org.jdbi.v3.sqlobject.statement.UseRowMapper
import java.util.*

interface JdbiUserRepository : UserRepository {
    @UseRowMapper(UserMapper::class)
    @SqlQuery("SELECT * FROM users")
    override fun getAll(): List<User>

    @UseRowMapper(UserMapper::class)
    @SqlQuery("SELECT * FROM users SKIP :skip LIMIT :limit")
    override fun getAll(skip: Int, limit: Int): List<User>

    @UseRowMapper(UserMapper::class)
    @SqlQuery("SELECT * FROM users WHERE id = :id")
    override fun getById(@Bind("id") id: UUID): User?

    @SqlUpdate("DELETE FROM users WHERE id = :id")
    override fun delete(@Bind("id") id: UUID)

    @SqlUpdate("""
        UPDATE users SET
        email = COALESCE(:email, email),
        password_hash = COALESCE(:password_hash, password_hash),
        role = COALESCE(:role, role),
        name = COALESCE(:name, name),
        avatar_url = COALESCE(:avatar_url, avatar_url),
        updated_at = NOW()
        WHERE id = :id
    """)
    override fun update(@BindBean entity: User)

    @SqlUpdate("""
        UPDATE users SET
        email = COALESCE(:email, email),
        name = COALESCE(:name, name),
        avatar_url = COALESCE(:avatar_url, avatar_url),
        updated_at = NOW()
        WHERE id = :id
        RETURNING id
    """)
    override fun update(@Bind("email") email: String?,
                        @Bind("name") name: String?,
                        @Bind("avatar_url") avatarUrl: String?)

    @SqlUpdate("""
        INSERT INTO users (id, email, password_hash, role, name, avatar_url, created_at, updated_at) 
        VALUES (:id, :email, :passwordHash, :role, :name, :avatarUrl, :createdAt, :updatedAt)
    """)
    override fun insert(@BindBean entity: User)

    @UseRowMapper(UserMapper::class)
    @SqlQuery("SELECT * FROM users WHERE email = :email")
    override fun getByEmail(@Bind("email") email: String): User?

    @SqlUpdate("UPDATE users SET last_login_at = NOW() WHERE id = :id")
    override fun updateLastLogin(@Bind("id") id: UUID)

    @SqlUpdate("UPDATE users SET password_hash = :password_hash WHERE id = :id")
    override fun updatePassword(@Bind("id") id: UUID, @Bind("password_hash") passwordHash: String)
}