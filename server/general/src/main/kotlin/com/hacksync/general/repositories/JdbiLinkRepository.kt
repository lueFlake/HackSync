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
    @SqlQuery("SELECT * FROM link")
    override suspend fun getAll(): List<Link>

    @UseRowMapper(LinkMapper::class)
    @SqlQuery("SELECT * FROM link WHERE id = :id")
    override suspend fun getById(@Bind("id") id: UUID): Link?

    @SqlUpdate("INSERT INTO link (id, url) VALUES (:id, :url) RETURNING *")
    @GetGeneratedKeys
    override suspend fun insert(@BindBean link: Link): Unit

    @SqlUpdate("UPDATE link SET url = :url WHERE id = :id")
    @GetGeneratedKeys
    override suspend fun update(@BindBean link: Link): Unit

    @SqlUpdate("DELETE FROM link WHERE id = :id")
    @GetGeneratedKeys
    override suspend fun delete(@Bind("id") id: UUID): Unit
}

