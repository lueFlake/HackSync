package com.hacksync.general.repositories

import com.hacksync.general.entities.Team
import com.hacksync.general.repositories.interfaces.TeamRepository
import org.jdbi.v3.sqlobject.config.RegisterConstructorMapper
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate
import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.customizer.BindBean
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys
import java.util.UUID

@RegisterConstructorMapper(Team::class)
interface JdbiTeamRepository : TeamRepository {
    @SqlQuery("SELECT * FROM team")
    override suspend fun getAll(): List<Team>

    @SqlQuery("SELECT * FROM team WHERE id = :id")
    override suspend fun getById(@Bind("id") id: UUID): Team?

    @SqlUpdate("INSERT INTO team (id, link_id, name) VALUES (:id, :linkId, :name) RETURNING *")
    @GetGeneratedKeys
    override suspend fun insert(@BindBean team: Team): Team

    @SqlUpdate("UPDATE team SET link_id = :linkId, name = :name WHERE id = :id")
    override suspend fun update(@BindBean team: Team)

    @SqlUpdate("DELETE FROM team WHERE id = :id")
    override suspend fun delete(@Bind("id") id: UUID)
} 