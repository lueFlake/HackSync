package com.hacksync.general.repositories.interfaces

import com.hacksync.general.entities.IEntity
import java.util.*

interface IEntityRepository<TEntity : IEntity> {
    suspend fun getAll() : List<TEntity>
    suspend fun getById(id: UUID) : TEntity?
    suspend fun insert(entity : TEntity): TEntity
    suspend fun delete(id: UUID)
    suspend fun update(entity : TEntity)
}