package com.hacksync.general.mapping

import com.hacksync.general.entities.UserTeam
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import java.sql.ResultSet
import java.util.UUID

class UserTeamMapper : RowMapper<UserTeam> {
    override fun map(rs: ResultSet, ctx: StatementContext): UserTeam {
        return UserTeam(
            userId = UUID.fromString(rs.getString("user_id")),
            teamId = UUID.fromString(rs.getString("team_id"))
        )
    }
} 