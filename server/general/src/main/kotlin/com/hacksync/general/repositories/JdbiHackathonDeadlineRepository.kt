package com.hacksync.general.repositories

import com.hacksync.general.entities.Deadline
import com.hacksync.general.mapping.DeadlineMapper
import com.hacksync.general.repositories.interfaces.HackathonDeadlineRepository
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate
import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.statement.UseRowMapper
import java.util.UUID

interface JdbiHackathonDeadlineRepository : HackathonDeadlineRepository {
    @UseRowMapper(DeadlineMapper::class)
    @SqlQuery("""
        SELECT d.* FROM deadlines d
        INNER JOIN hackathon_deadlines hd ON d.id = hd.deadline_id
        WHERE hd.hackathon_id = :hackathonId
    """)
    override fun getDeadlinesForHackathon(@Bind("hackathonId") hackathonId: UUID): List<Deadline>

    @SqlUpdate("""
        INSERT INTO hackathon_deadlines (hackathon_id, deadline_id)
        VALUES (:hackathonId, :deadlineId)
    """)
    override fun addDeadlineToHackathon(
        @Bind("hackathonId") hackathonId: UUID,
        @Bind("deadlineId") deadlineId: UUID
    )

    @SqlUpdate("""
        DELETE FROM hackathon_deadlines
        WHERE hackathon_id = :hackathonId AND deadline_id = :deadlineId
    """)
    override fun removeDeadlineFromHackathon(
        @Bind("hackathonId") hackathonId: UUID,
        @Bind("deadlineId") deadlineId: UUID
    )

    @SqlUpdate("""
        DELETE FROM hackathon_deadlines
        WHERE hackathon_id = :hackathonId
    """)
    override fun removeAllDeadlinesFromHackathon(@Bind("hackathonId") hackathonId: UUID)
} 