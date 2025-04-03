package com.hacksync.general.mapping

import com.hacksync.general.entities.Role
import com.hacksync.general.entities.User
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import java.sql.ResultSet
import java.time.Instant
import java.util.*

class UserMapper : RowMapper<User> {
    override fun map(rs: ResultSet, ctx: StatementContext): User {
        return User(
            id = UUID.fromString(rs.getString("id")),
            email = rs.getString("email"),
            passwordHash = rs.getString("password_hash"),
            role = Role.valueOf(rs.getString("role")),
            name = rs.getString("name"),
            avatarUrl = rs.getString("avatar_url"),
            createdAt = rs.getTimestamp("created_at").toInstant(),
            updatedAt = rs.getTimestamp("updated_at").toInstant()
        )
    }
}