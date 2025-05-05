package com.hacksync.general.mapping

import com.hacksync.general.entities.Team
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import java.sql.ResultSet
import java.util.UUID

class TeamMapper : RowMapper<Team> {
    override fun map(rs: ResultSet, ctx: StatementContext): Team {
        return Team(
            id = UUID.fromString(rs.getString("id")),
            linkId = rs.getString("link_id")?.let { UUID.fromString(it) },
            name = rs.getString("name")
        )
    }
} 