package com.hacksync.general.repositories

import com.hacksync.general.entities.Navigation
import com.hacksync.general.repositories.interfaces.NavigationRepository
import org.jdbi.v3.sqlobject.config.RegisterConstructorMapper
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate
import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.customizer.BindBean
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys
import java.util.UUID

@RegisterConstructorMapper(Navigation::class)
interface JdbiNavigationRepository : NavigationRepository {
    @SqlQuery("SELECT * FROM navigation")
    override suspend fun getAll(): List<Navigation>

    @SqlQuery("SELECT * FROM navigation WHERE id = :id")
    override suspend fun getById(@Bind("id") id: UUID): Navigation?

    @SqlUpdate("INSERT INTO navigation (id, description, date_of_register, date_of_start, date_of_end, extra_destfine, link_id, name) VALUES (:id, :description, :dateOfRegister, :dateOfStart, :dateOfEnd, :extraDestfine, :linkId, :name) RETURNING *")
    @GetGeneratedKeys
    override suspend fun insert(@BindBean navigation: Navigation): Navigation

    @SqlUpdate("UPDATE navigation SET description = :description, date_of_register = :dateOfRegister, date_of_start = :dateOfStart, date_of_end = :dateOfEnd, extra_destfine = :extraDestfine, link_id = :linkId, name = :name WHERE id = :id")
    override suspend fun update(@BindBean navigation: Navigation)

    @SqlUpdate("DELETE FROM navigation WHERE id = :id")
    override suspend fun delete(@Bind("id") id: UUID)
} 