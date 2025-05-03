package com.hacksync.general.repositories

import com.hacksync.general.entities.UserTeam
import com.hacksync.general.repositories.interfaces.UserTeamRepository
import org.jdbi.v3.sqlobject.config.RegisterConstructorMapper
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate
import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.customizer.BindBean
import java.util.UUID

@RegisterConstructorMapper(UserTeam::class)
interface JdbiUserTeamRepository : UserTeamRepository {
    @SqlQuery("SELECT * FROM userteam")
    override suspend fun getAll(): List<UserTeam>

    @SqlQuery("SELECT * FROM userteam WHERE user_id = :userId AND team_id = :teamId")
    override suspend fun getById(@Bind("userId") userId: UUID, @Bind("teamId") teamId: UUID): UserTeam?

    @SqlUpdate("INSERT INTO userteam (user_id, team_id) VALUES (:userId, :teamId)")
    override suspend fun insert(@BindBean userTeam: UserTeam)

    @SqlUpdate("UPDATE userteam SET user_id = :userId, team_id = :teamId WHERE user_id = :userId AND team_id = :teamId")
    override suspend fun update(@BindBean userTeam: UserTeam)

    @SqlUpdate("DELETE FROM userteam WHERE user_id = :userId AND team_id = :teamId")
    override suspend fun delete(@Bind("userId") userId: UUID, @Bind("teamId") teamId: UUID)
} 