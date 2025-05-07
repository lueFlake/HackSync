package com.hacksync.general.repositories

import com.hacksync.general.entities.Deadline
import com.hacksync.general.repositories.interfaces.DeadlineRepository
import com.hacksync.general.mapping.DeadlineMapper
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate
import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.customizer.BindBean
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys
import org.jdbi.v3.sqlobject.statement.UseRowMapper
import java.util.UUID

interface JdbiDeadlineRepository : DeadlineRepository {
    @UseRowMapper(DeadlineMapper::class)
    @SqlQuery("SELECT * FROM deadline")
    override fun getAll(): List<Deadline>

    @UseRowMapper(DeadlineMapper::class)
    @SqlQuery("SELECT * FROM deadline WHERE id = :id")
    override fun getById(@Bind("id") id: UUID): Deadline?

    @SqlUpdate("INSERT INTO deadline (id, date, link_id, name, type) VALUES (:id, :date, :linkId, :name, :type) RETURNING *")
    override fun insert(@BindBean deadline: Deadline)

    @SqlUpdate("UPDATE deadline SET date = :date, link_id = :linkId, name = :name, type = :type WHERE id = :id")
    override fun update(@BindBean deadline: Deadline)

    @SqlUpdate("DELETE FROM deadline WHERE id = :id")
    override fun delete(@Bind("id") id: UUID)
} 