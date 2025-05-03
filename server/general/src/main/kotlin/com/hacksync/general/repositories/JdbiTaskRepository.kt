package com.hacksync.general.repositories

import com.hacksync.general.entities.Task
import com.hacksync.general.entities.User
import com.hacksync.general.mapping.UserMapper
import org.jdbi.v3.sqlobject.config.RegisterConstructorMapper
import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.customizer.BindBean
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate
import org.jdbi.v3.sqlobject.statement.UseRowMapper
import java.util.*

@RegisterConstructorMapper(Task::class)
interface JdbiTaskRepository  {

    @UseRowMapper(UserMapper::class)
    @SqlQuery("SELECT * FROM users")
    fun getAll(): List<User>

    @UseRowMapper(UserMapper::class)
    @SqlQuery("SELECT * FROM users SKIP :skip LIMIT :limit")
    fun getAll(skip: Int, limit: Int): List<User>

    @UseRowMapper(UserMapper::class)
    @SqlQuery("SELECT * FROM users WHERE id = :id")
    fun getById(@Bind("id") id: UUID): User?

    @SqlUpdate("DELETE FROM users WHERE id = :id")
    fun delete(@Bind("id") id: UUID)

    @SqlUpdate("""
        UPDATE users SET
        email = COALESCE(:email, email),
        password_hash = COALESCE(:password_hash, password_hash),
        role = COALESCE(:role, role),
        name = COALESCE(:name, name),
        avatar_url = COALESCE(:avatar_url, avatar_url)
        updated_at = NOW()
        WHERE id = :id
    """)
    fun update(@BindBean entity: User)

    @SqlUpdate("""
        UPDATE users SET
        email = COALESCE(:email, email)
        name = COALESCE(:name, name),
        avatar_url = COALESCE(:avatar_url, avatar_url)
        updated_at = NOW()
        WHERE id = :id
        RETURNING id
    """)
    fun update(@Bind("email") email: String?,
               @Bind("name") name: String?,
               @Bind("avatar_url") avatarUrl: String?)

    @SqlUpdate("""
        INSERT INTO tasks (id, email, password_hash, role, name, avatar_url, created_at, updated_at) 
        VALUES (:id, :email, :passwordHash, :role, :name, :avatarUrl, :createdAt, :updatedAt)
    """)
    fun insert(@BindBean entity: Task)
}
