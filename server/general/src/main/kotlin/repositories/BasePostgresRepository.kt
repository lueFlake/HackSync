package repositories

import entities.Entity
import repositories.interfaces.IEntityRepository
import java.util.*

class BasePostgresRepository : IEntityRepository<Entity>{
    override suspend fun getAll(): List<Entity> {
        TODO("Not yet implemented")

    }

    override suspend fun findById(id: UUID): Entity? {
        TODO("Not yet implemented")
    }

    override suspend fun delete(id: UUID) {
        TODO("Not yet implemented")
    }

    override suspend fun replace(item: Entity) {
        TODO("Not yet implemented")
    }

    override suspend fun insert(entity: Entity) {
        TODO("Not yet implemented")
    }

    override suspend fun find(predicate: (Entity) -> Boolean): Entity? {
        TODO("Not yet implemented")
    }

}