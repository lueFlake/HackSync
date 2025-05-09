package com.hacksync.general.repositories

import com.hacksync.general.entities.User
import com.hacksync.general.mapping.UserMapper
import com.hacksync.general.repositories.interfaces.IEntityRepository
import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.customizer.BindBean
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate
import org.jdbi.v3.sqlobject.statement.UseRowMapper
import java.util.*

interface UserRepository {
    fun getAll(): List<User>
    fun getAll(skip: Int, limit: Int): List<User>
    fun getById(@Bind("id") id: UUID): User?
    fun delete(@Bind("id") id: UUID)
    fun update(@BindBean entity: User)
    fun update(@Bind("email") email: String?,
               @Bind("name") name: String?,
               @Bind("avatar_url") avatarUrl: String?)
    fun insert(@BindBean entity: User)
    fun getByEmail(@Bind("email") email: String): User?
    fun updatePassword(@Bind("id") id: UUID, @Bind("password_hash") passwordHash: String)
    fun updateLastLogin(@Bind("id") id: UUID)
}