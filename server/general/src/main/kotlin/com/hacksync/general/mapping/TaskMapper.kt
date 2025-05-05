package com.hacksync.general.mapping

import com.hacksync.general.entities.Priority
import com.hacksync.general.entities.Task
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import java.sql.ResultSet
import java.time.Instant
import java.util.UUID

class TaskMapper : RowMapper<Task> {
    override fun map(rs: ResultSet, ctx: StatementContext): Task {
        return Task(
            id = UUID.fromString(rs.getString("id")),
            number = rs.getString("number"),
            name = rs.getString("name"),
            description = rs.getString("description"),
            priority = Priority.valueOf(rs.getString("priority")),
            linkId = rs.getString("link_id")?.let { UUID.fromString(it) },
            userId = rs.getString("user_id")?.let { UUID.fromString(it) },
            status = KanbanStatusMapper().map(rs, ctx),
            dueDate = rs.getTimestamp("due_date")?.toInstant(),
            createdAt = rs.getTimestamp("created_at").toInstant(),
            updatedAt = rs.getTimestamp("updated_at").toInstant()
        )
    }
} 