package com.hacksync.general.repositories

import com.hacksync.general.entities.Task
import com.hacksync.general.entities.User
import com.hacksync.general.mapping.UserMapper
import com.hacksync.general.mapping.TaskMapper
import com.hacksync.general.mapping.KanbanStatusMapper
import com.hacksync.general.repositories.interfaces.TaskRepository
import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.customizer.BindBean
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate
import org.jdbi.v3.sqlobject.statement.UseRowMapper
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys
import java.util.*

interface JdbiTaskRepository : TaskRepository {
    @UseRowMapper(TaskMapper::class)
    @SqlQuery("SELECT * FROM tasks")
    override fun getAll(): List<Task>

    @UseRowMapper(TaskMapper::class)
    @SqlQuery("SELECT * FROM tasks WHERE id = :id")
    override fun getById(@Bind("id") id: UUID): Task?

    @UseRowMapper(TaskMapper::class)
    @SqlQuery("SELECT * FROM tasks WHERE user_id = :userId")
    override fun getByUserId(@Bind("userId") userId: UUID): List<Task>

    @UseRowMapper(TaskMapper::class)
    @SqlQuery("SELECT * FROM tasks WHERE status_id = :statusId")
    override fun getByStatusId(@Bind("statusId") statusId: UUID): List<Task>

    @SqlUpdate("""
        INSERT INTO tasks (
            id, number, name, description, priority, link_id, user_id, 
            status_id, due_date, created_at, updated_at
        ) VALUES (
            :id, :number, :name, :description, :priority, :linkId, :userId,
            :status.id, :dueDate, :createdAt, :updatedAt
        )
    """)
    override fun insert(@BindBean task: Task)

    @SqlUpdate("""
        UPDATE tasks SET
            number = :number,
            name = :name,
            description = :description,
            priority = :priority,
            link_id = :linkId,
            user_id = :userId,
            status_id = :status.id,
            due_date = :dueDate,
            updated_at = :updatedAt
        WHERE id = :id
    """)
    override fun update(@BindBean task: Task)

    @SqlUpdate("DELETE FROM tasks WHERE id = :id")
    override fun delete(@Bind("id") id: UUID)
}
