package repositories.interfaces

import entities.IEntity
import java.util.*

interface IEntityRepository<TEntity> where TEntity : IEntity{
    suspend fun getAll() : List<TEntity>
    suspend fun findById(id: UUID) : TEntity?
    suspend fun find(predicate: (TEntity) -> Boolean) : TEntity?
    suspend fun insert(entity : TEntity)
    suspend fun delete(id: UUID)
    suspend fun replace(item : TEntity)
}