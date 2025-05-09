package com.hacksync.general.repositories

import com.hacksync.general.entities.KanbanStatus
import com.hacksync.general.repositories.interfaces.KanbanStatusRepository
import com.hacksync.general.mapping.KanbanStatusMapper
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate
import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.customizer.BindBean
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys
import org.jdbi.v3.sqlobject.statement.UseRowMapper
import java.util.UUID

interface JdbiKanbanStatusRepository : KanbanStatusRepository {
    @UseRowMapper(KanbanStatusMapper::class)
    @SqlQuery("SELECT * FROM kanban_statuses")
    override fun getAll(): List<KanbanStatus>

    @UseRowMapper(KanbanStatusMapper::class)
    @SqlQuery("SELECT * FROM kanban_statuses WHERE id = :id")
    override fun getById(@Bind("id") id: UUID): KanbanStatus?

    @SqlUpdate("""
        INSERT INTO kanban_statuses (
            id, next_id, name, color, created_at, updated_at
        ) VALUES (
            :id, :nextId, :name, :color, :createdAt, :updatedAt
        )
    """)
    override fun insert(@BindBean status: KanbanStatus)

    @SqlUpdate("""
        UPDATE kanban_statuses 
        SET next_id = :nextId, 
            name = :name, 
            color = :color,
            updated_at = :updatedAt 
        WHERE id = :id
    """)
    override fun update(@BindBean status: KanbanStatus)

    @SqlUpdate("DELETE FROM kanban_statuses WHERE id = :id")
    override fun delete(@Bind("id") id: UUID)
} 