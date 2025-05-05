package com.hacksync.general.mapping

import com.hacksync.general.entities.Deadline
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import java.sql.ResultSet
import java.time.LocalDate
import java.util.UUID

class DeadlineMapper : RowMapper<Deadline> {
    override fun map(rs: ResultSet, ctx: StatementContext): Deadline {
        return Deadline(
            id = UUID.fromString(rs.getString("id")),
            date = rs.getDate("date")?.toLocalDate(),
            linkId = rs.getString("link_id")?.let { UUID.fromString(it) },
            name = rs.getString("name"),
            type = rs.getString("type")
        )
    }
} 