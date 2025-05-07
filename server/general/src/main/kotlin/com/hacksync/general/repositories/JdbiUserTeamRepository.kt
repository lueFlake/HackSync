package com.hacksync.general.repositories

import com.hacksync.general.entities.UserTeam
import com.hacksync.general.repositories.interfaces.UserTeamRepository
import com.hacksync.general.mapping.UserTeamMapper
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate
import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.customizer.BindBean
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys
import org.jdbi.v3.sqlobject.statement.UseRowMapper
import java.util.UUID

interface JdbiUserTeamRepository : UserTeamRepository {
    @UseRowMapper(UserTeamMapper::class)
    @SqlQuery("SELECT * FROM user_teams")
    override fun getAll(): List<UserTeam>

    @UseRowMapper(UserTeamMapper::class)
    @SqlQuery("SELECT * FROM user_teams WHERE user_id = :userId AND team_id = :teamId")
    override fun getById(@Bind("userId") userId: UUID, @Bind("teamId") teamId: UUID): UserTeam?

    @SqlUpdate("INSERT INTO user_teams (user_id, team_id, role) VALUES (:userId, :teamId, :role)")
    override fun insert(@BindBean userTeam: UserTeam)

    @SqlUpdate("UPDATE user_teams SET role = :role WHERE user_id = :userId AND team_id = :teamId")
    override fun update(@BindBean userTeam: UserTeam)

    @SqlUpdate("DELETE FROM user_teams WHERE user_id = :userId AND team_id = :teamId")
    override fun delete(@Bind("userId") userId: UUID, @Bind("teamId") teamId: UUID)

    @SqlQuery("SELECT user_id FROM user_teams WHERE team_id = :teamId")
    override fun getTeamMembers(@Bind("teamId") teamId: UUID): List<UUID>

    @SqlQuery("SELECT team_id FROM user_teams WHERE user_id = :userId")
    override fun getUserTeams(@Bind("userId") userId: UUID): List<UUID>
} 