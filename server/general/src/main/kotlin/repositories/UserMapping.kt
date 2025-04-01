package repositories

import entities.Priority
import entities.Role
import entities.Task
import entities.User
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.dao.id.UUIDTable
import java.util.*

object UserTable : UUIDTable("task") {
    val name = varchar("name", 64)
    val email = varchar("email", 64)
    val password = varchar("password", 512)
    val role = varchar("role", 16)
    val avatarUrl = varchar("avatarUrl", 128).nullable()
}

class UserDAO(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<UserDAO>(UserTable) {
    }

    var name by UserTable.name
    var email by UserTable.email
    var password by UserTable.password
    var role by UserTable.role
    var avatarUrl by UserTable.avatarUrl
}