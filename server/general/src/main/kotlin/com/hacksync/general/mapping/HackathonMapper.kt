package com.hacksync.general.mapping

import com.hacksync.general.entities.Hackathon
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import java.sql.ResultSet
import java.time.Instant
import java.util.UUID

class HackathonMapper : RowMapper<Hackathon> {
    override fun map(rs: ResultSet, ctx: StatementContext): Hackathon {
        return Hackathon(
            id = UUID.fromString(rs.getString("id")),
            description = rs.getString("description"),
            dateOfRegister = rs.getTimestamp("date_of_register")?.toInstant(),
            dateOfStart = rs.getTimestamp("date_of_start")?.toInstant(),
            dateOfEnd = rs.getTimestamp("date_of_end")?.toInstant(),
            name = rs.getString("name"),
            createdAt = rs.getTimestamp("created_at")?.toInstant(),
            updatedAt = rs.getTimestamp("updated_at")?.toInstant(),
            // deadlines will be set elsewhere
        )
    }
} 