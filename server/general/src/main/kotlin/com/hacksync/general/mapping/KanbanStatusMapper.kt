package com.hacksync.general.mapping

import com.hacksync.general.entities.KanbanStatus
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import java.sql.ResultSet
import java.time.Instant
import java.util.UUID

class KanbanStatusMapper : RowMapper<KanbanStatus> {
    override fun map(rs: ResultSet, ctx: StatementContext): KanbanStatus {
        val meta = rs.metaData
        for (i in 1..meta.columnCount) {
            println("Column ${i}: ${meta.getColumnName(i)}")
        }
        return KanbanStatus(
            id = UUID.fromString(rs.getString("id")),
            nextId = rs.getString("next_id")?.let { UUID.fromString(it) },
            name = rs.getString("name"),
            color = rs.getString("color"),
            createdAt = rs.getTimestamp("created_at").toInstant(),
            updatedAt = rs.getTimestamp("updated_at").toInstant()
        )
    }
} 