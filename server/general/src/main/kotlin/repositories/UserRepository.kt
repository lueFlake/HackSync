package repositories

import BaseRepository
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.IdTable
import java.util.*

class UserRepository(table: UserTable, dao: UserDAO.Companion) : BaseRepository<UUID, UserDAO>(table, dao)