package com.hacksync.general.repositories

import com.hacksync.general.entities.Link
import com.hacksync.general.repositories.interfaces.LinkRepository
import com.hacksync.general.mapping.LinkMapper
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate
import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.customizer.BindBean
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys
import org.jdbi.v3.sqlobject.statement.UseRowMapper
import java.util.UUID

interface JdbiLinkRepository : LinkRepository {
    @UseRowMapper(LinkMapper::class)
    @SqlQuery("SELECT * FROM links")
    override fun getAll(): List<Link>

    @UseRowMapper(LinkMapper::class)
    @SqlQuery("SELECT * FROM links WHERE id = :id")
    override fun getById(@Bind("id") id: UUID): Link?

    @SqlUpdate("INSERT INTO links (id, url, title, entity_id, entity_type) VALUES (:id, :url, :title, :entityId, :entityType) RETURNING *")
    override fun insert(@BindBean link: Link)

    @SqlUpdate("UPDATE links SET url = :url, title = :title, entity_id = :entityId, entity_type = :entityType WHERE id = :id")
    override fun update(@BindBean link: Link)

    @SqlUpdate("DELETE FROM links WHERE id = :id")
    override fun delete(@Bind("id") id: UUID)
}

