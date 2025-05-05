package com.hacksync.general.mapping

import com.hacksync.general.entities.Link
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import java.sql.ResultSet
import java.util.UUID

class LinkMapper : RowMapper<Link> {
    override fun map(rs: ResultSet, ctx: StatementContext): Link {
        return Link(
            id = UUID.fromString(rs.getString("id")),
            url = rs.getString("url"),
            title = rs.getString("title"),
            entityId = UUID.fromString(rs.getString("entity_id")),
            entityType = rs.getString("entity_type")
        )
    }
} 