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
    @SqlQuery("SELECT * FROM teams")
    override fun getAll(): List<Team>

    @UseRowMapper(TeamMapper::class)
    @SqlQuery("SELECT * FROM teams WHERE id = :id")
    override fun getById(@Bind("id") id: UUID): Team?

    @SqlUpdate("INSERT INTO teams (id, name) VALUES (:id, :name) RETURNING *")
    override fun insert(@BindBean team: Team)

    @SqlUpdate("UPDATE teams SET name = :name WHERE id = :id")
    override fun update(@BindBean team: Team)

    @SqlUpdate("DELETE FROM teams WHERE id = :id")
    override fun delete(@Bind("id") id: UUID)
} 