package com.hacksync.general.repositories

import com.hacksync.general.entities.KanbanStatus
import com.hacksync.general.repositories.interfaces.KanbanStatusRepository
import org.jdbi.v3.sqlobject.config.RegisterConstructorMapper
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate
import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.customizer.BindBean
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys
import java.util.UUID

@RegisterConstructorMapper(KanbanStatus::class)
interface JdbiKanbanStatusRepository : KanbanStatusRepository {
    @SqlQuery("SELECT * FROM kanbanstatus")
    override suspend fun getAll(): List<KanbanStatus>

    @SqlQuery("SELECT * FROM kanbanstatus WHERE id = :id")
    override suspend fun getById(@Bind("id") id: UUID): KanbanStatus?

    @SqlUpdate("INSERT INTO kanbanstatus (id, next_id, name, color) VALUES (:id, :nextId, :name, :color) RETURNING *")
    @GetGeneratedKeys
    override suspend fun insert(@BindBean status: KanbanStatus): Unit

    @SqlUpdate("UPDATE kanbanstatus SET next_id = :nextId, name = :name, color = :color WHERE id = :id")
    override suspend fun update(@BindBean status: KanbanStatus): Unit

    @SqlUpdate("DELETE FROM kanbanstatus WHERE id = :id")
    override suspend fun delete(@Bind("id") id: UUID): Unit
} 