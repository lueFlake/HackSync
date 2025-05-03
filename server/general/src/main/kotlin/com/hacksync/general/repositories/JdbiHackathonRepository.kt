package com.hacksync.general.repositories

import com.hacksync.general.entities.Hackathon
import com.hacksync.general.repositories.interfaces.HackathonRepository
import org.jdbi.v3.sqlobject.config.RegisterConstructorMapper
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate
import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.customizer.BindBean
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys
import java.util.UUID

@RegisterConstructorMapper(Hackathon::class)
interface JdbiHackathonRepository : HackathonRepository {
    @SqlQuery("SELECT * FROM hackathon")
    override suspend fun getAll(): List<Hackathon>

    @SqlQuery("SELECT * FROM hackathon WHERE id = :id")
    override suspend fun getById(@Bind("id") id: UUID): Hackathon?

    @SqlUpdate("INSERT INTO hackathon (id, description, date_of_register, date_of_start, date_of_end, extra_destfine, link_id, name) VALUES (:id, :description, :dateOfRegister, :dateOfStart, :dateOfEnd, :extraDestfine, :linkId, :name) RETURNING *")
    @GetGeneratedKeys
    override suspend fun insert(@BindBean hackathon: Hackathon): Hackathon

    @SqlUpdate("UPDATE hackathon SET description = :description, date_of_register = :dateOfRegister, date_of_start = :dateOfStart, date_of_end = :dateOfEnd, extra_destfine = :extraDestfine, link_id = :linkId, name = :name WHERE id = :id")
    override suspend fun update(@BindBean hackathon: Hackathon)

    @SqlUpdate("DELETE FROM hackathon WHERE id = :id")
    override suspend fun delete(@Bind("id") id: UUID)
} 