package repositories

import BaseRepository
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.IdTable

class TaskRepository(table: TaskTable, dao: TaskDAO.Companion) : BaseRepository<Int, TaskDAO>(table, dao)