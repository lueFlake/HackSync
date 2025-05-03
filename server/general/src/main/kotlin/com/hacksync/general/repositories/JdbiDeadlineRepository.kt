package com.hacksync.general.repositories

import com.hacksync.general.entities.Deadline
import com.hacksync.general.repositories.interfaces.DeadlineRepository
import org.jdbi.v3.sqlobject.config.RegisterConstructorMapper
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate
import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.customizer.BindBean
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys
import java.util.UUID

@RegisterConstructorMapper(Deadline::class)
interface JdbiDeadlineRepository : DeadlineRepository {
    @SqlQuery("SELECT * FROM deadline")
    override suspend fun getAll(): List<Deadline>

    @SqlQuery("SELECT * FROM deadline WHERE id = :id")
    override suspend fun getById(@Bind("id") id: UUID): Deadline?

    @SqlUpdate("INSERT INTO deadline (id, date, link_id, name, type) VALUES (:id, :date, :linkId, :name, :type) RETURNING *")
    @GetGeneratedKeys
    override suspend fun insert(@BindBean deadline: Deadline): Deadline

    @SqlUpdate("UPDATE deadline SET date = :date, link_id = :linkId, name = :name, type = :type WHERE id = :id")
    override suspend fun update(@BindBean deadline: Deadline)

    @SqlUpdate("DELETE FROM deadline WHERE id = :id")
    override suspend fun delete(@Bind("id") id: UUID)
} 