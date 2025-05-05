package com.hacksync.general.repositories

import com.hacksync.general.entities.Team
import com.hacksync.general.repositories.interfaces.TeamRepository
import com.hacksync.general.mapping.TeamMapper
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate
import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.customizer.BindBean
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys
import org.jdbi.v3.sqlobject.statement.UseRowMapper
import java.util.UUID

interface JdbiTeamRepository : TeamRepository {
    @UseRowMapper(TeamMapper::class)
    @SqlQuery("SELECT * FROM team")
    override suspend fun getAll(): List<Team>

    @UseRowMapper(TeamMapper::class)
    @SqlQuery("SELECT * FROM team WHERE id = :id")
    override suspend fun getById(@Bind("id") id: UUID): Team?

    @SqlUpdate("INSERT INTO team (id, hackathon_id, name) VALUES (:id, :hackathonId, :name) RETURNING *")
    @GetGeneratedKeys
    override suspend fun insert(@BindBean team: Team): Unit

    @SqlUpdate("UPDATE team SET hackathon_id = :hackathonId, name = :name WHERE id = :id")
    override suspend fun update(@BindBean team: Team): Unit

    @SqlUpdate("DELETE FROM team WHERE id = :id")
    override suspend fun delete(@Bind("id") id: UUID): Unit
} 