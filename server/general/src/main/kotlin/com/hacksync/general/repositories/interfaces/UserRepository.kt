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
    fun getById(id: UUID): User?
    fun delete(id: UUID)
    fun update(entity: User)
    fun update(id: UUID,
               email: String?,
               name: String?,
               avatarUrl: String?)
    fun insert(entity: User)
    fun getByEmail(email: String): User?
    fun updatePassword(id: UUID, passwordHash: String)
    fun updateLastLogin(id: UUID)
}