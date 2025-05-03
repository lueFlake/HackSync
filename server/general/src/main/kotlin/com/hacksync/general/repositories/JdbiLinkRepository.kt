package com.hacksync.general.repositories

import com.hacksync.general.entities.Link
import com.hacksync.general.repositories.interfaces.LinkRepository
import org.jdbi.v3.sqlobject.config.RegisterConstructorMapper
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate
import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.customizer.BindBean
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys
import java.util.UUID

@RegisterConstructorMapper(Link::class)
interface JdbiLinkRepository : LinkRepository {
    @SqlQuery("SELECT * FROM link")
    override suspend fun getAll(): List<Link>

    @SqlQuery("SELECT * FROM link WHERE id = :id")
    override suspend fun getById(@Bind("id") id: UUID): Link?

    @SqlUpdate("INSERT INTO link (id, url, title, entity_id, entity_type) VALUES (:id, :url, :title, :entityId, :entityType) RETURNING *")
    @GetGeneratedKeys
    override suspend fun insert(@BindBean link: Link): Link

    @SqlUpdate("UPDATE link SET url = :url, title = :title, entity_id = :entityId, entity_type = :entityType WHERE id = :id")
    override suspend fun update(@BindBean link: Link)

    @SqlUpdate("DELETE FROM link WHERE id = :id")
    override suspend fun delete(@Bind("id") id: UUID)
}

