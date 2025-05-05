package com.hacksync.general.repositories

import com.hacksync.general.entities.Hackathon
import com.hacksync.general.mapping.HackathonMapper
import com.hacksync.general.repositories.interfaces.HackathonRepository
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate
import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.customizer.BindBean
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys
import org.jdbi.v3.sqlobject.statement.UseRowMapper
import java.util.UUID

interface JdbiHackathonRepository : HackathonRepository {
    @UseRowMapper(HackathonMapper::class)
    @SqlQuery("SELECT * FROM hackathons")
    override fun getAll(): List<Hackathon>

    @UseRowMapper(HackathonMapper::class)
    @SqlQuery("SELECT * FROM hackathons WHERE id = :id")
    override fun getById(@Bind("id") id: UUID): Hackathon?

    @SqlUpdate("INSERT INTO hackathons (id, name, description, date_of_register, date_of_start, date_of_end, extra_destfine) VALUES (:id, :name, :description, :dateOfRegister, :dateOfStart, :dateOfEnd, :extraDestfine)")
    override fun insert(@BindBean hackathon: Hackathon)

    @SqlUpdate("UPDATE hackathons SET name = :name, description = :description, date_of_register = :dateOfRegister, date_of_start = :dateOfStart, date_of_end = :dateOfEnd, extra_destfine = :extraDestfine WHERE id = :id")
    override fun update(@BindBean hackathon: Hackathon)

    @SqlUpdate("DELETE FROM hackathons WHERE id = :id")
    override fun delete(@Bind("id") id: UUID)
} 