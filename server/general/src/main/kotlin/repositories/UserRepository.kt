package repositories

import BaseRepository
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.IdTable
import java.util.*

class UserRepository(table: UserTable, dao: UserDAO.Companion) : BaseRepository<UUID, UserDAO>(table, dao)
/*
class UserRepository(private val connection: Connection) : IEntityRepository<User> {
    companion object {
        private val CREATE_TABLE_USERS = """CREATE TABLE IF NOT EXISTS USERS (
            ID SERIAL PRIMARY KEY, 
            EMAIL VARCHAR(255), 
            PASSWORD_HASH VARCHAR(255), 
            ROLE VARCHAR(20) NOT NULL CHECK (role IN ('PARTICIPANT', 'CAPTAIN', 'MODERATOR')), 
            NAME VARCHAR(255),
            AVATAR_URL VARCHAR(255),
            CREATED_AT TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            UPDATED_AT TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        );""".trimIndent()
        private val SELECT_USER_BY_ID = """
            SELECT ID, EMAIL, PASSWORD_HASH, ROLE, NAME, AVATAR_URL, CREATED_AT, UPDATED_AT 
            FROM USERS 
            WHERE ID = ?
        """.trimIndent()
        private val INSERT_USER = """
            INSERT INTO USERS (EMAIL, PASSWORD_HASH, ROLE, NAME, AVATAR_URL) 
            VALUES (?, ?, ?, ?, ?)
        """.trimIndent()
        private val UPDATE_USER_BY_ID = """
            UPDATE USERS 
            SET EMAIL = ?, ROLE = ?, NAME = ?, AVATAR_URL = ?, UPDATED_AT = NOW() 
            WHERE ID = ?
        """.trimIndent()
        private const val DELETE_USER_BY_ID = "DELETE FROM USERS WHERE ID = ?"
    }

    override suspend fun getAll(): List<User> {
        TODO("Not yet implemented")
    }

    override suspend fun findById(id: UUID): User? {
        TODO("Not yet implemented")
    }

    override suspend fun delete(id: UUID) {
        TODO("Not yet implemented")
    }

    override suspend fun replace(item: User) {
        val statement = connection.prepareStatement(UPDATE_USER_BY_ID)
        statement.setString(1, item.email)
        statement.setString(2, item.role.name)
        statement.setString(3, item.name)
        statement.setString(4, item.avatarUrl)
        statement.setString(5, item.id.toString())
        statement.executeUpdate()
    }

    override suspend fun insert(entity: User) {
        val statement = connection.prepareStatement(INSERT_USER, Statement.RETURN_GENERATED_KEYS)
        statement.setString(1, entity.email)
        statement.setString(2, entity.passwordHash)
        statement.setString(3, entity.role.name)
        statement.setString(4, entity.name)
        statement.setString(5, entity.avatarUrl)

        statement.executeUpdate()
    }

    override suspend fun find(predicate: (User) -> Boolean): User? {

    }
}*/
