package com.hacksync.general.repositories

import com.hacksync.general.entities.Task
import com.hacksync.general.entities.User
import com.hacksync.general.mapping.UserMapper
import com.hacksync.general.mapping.TaskMapper
import com.hacksync.general.mapping.KanbanStatusMapper
import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.customizer.BindBean
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate
import org.jdbi.v3.sqlobject.statement.UseRowMapper
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys
import java.util.*

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
    fun delete(@Bind("id") id: UUID): Unit

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
    fun update(@BindBean entity: User): Unit

    @SqlUpdate("""
        UPDATE users SET
        email = COALESCE(:email, email)
        name = COALESCE(:name, name),
        avatar_url = COALESCE(:avatar_url, avatar_url)
        updated_at = NOW()
        WHERE id = :id
    """)
    fun update(@Bind("email") email: String?,
               @Bind("name") name: String?,
               @Bind("avatar_url") avatarUrl: String?): Unit

    @SqlUpdate("""
        INSERT INTO tasks (id, email, password_hash, role, name, avatar_url, created_at, updated_at) 
        VALUES (:id, :email, :passwordHash, :role, :name, :avatarUrl, :createdAt, :updatedAt)
    """)
    fun insert(@BindBean entity: Task): Unit

    @UseRowMapper(TaskMapper::class)
    @SqlQuery("SELECT * FROM task")
    fun getAllTasks(): List<Task>

    @UseRowMapper(TaskMapper::class)
    @SqlQuery("SELECT * FROM task WHERE id = :id")
    fun getTaskById(@Bind("id") id: UUID): Task?

    @SqlUpdate("INSERT INTO task (id, description, link_id, name, status_id, team_id) VALUES (:id, :description, :linkId, :name, :statusId, :teamId) RETURNING *")
    @GetGeneratedKeys
    fun insertTask(@BindBean task: Task): Task

    @SqlUpdate("UPDATE task SET description = :description, link_id = :linkId, name = :name, status_id = :statusId, team_id = :teamId WHERE id = :id")
    fun updateTask(@BindBean task: Task): Unit

    @SqlUpdate("DELETE FROM task WHERE id = :id")
    fun deleteTask(@Bind("id") id: UUID): Unit
}
