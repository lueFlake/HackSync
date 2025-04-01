import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.IdTable

abstract class BaseRepository<TId : Any, TEntity : Entity<TId>>(
    private val table: IdTable<TId>,
    private val dao: EntityClass<TId, TEntity>
) {

    suspend fun getAll(): List<TEntity> = dbQuery {
        dao.all().toList()
    }

    suspend fun getById(id: TId): TEntity? = dbQuery {
        dao.findById(id)
    }

    suspend fun insert(block: TEntity.() -> Unit): Entity<TId> = dbQuery {
        dao.new(block)
    }

    suspend fun delete(id: TId): Boolean = dbQuery {
        dao.findById(id)?.delete() != null
    }

    suspend fun update(id: TId, block: TEntity.() -> Unit): Boolean = dbQuery {
        dao.findById(id)?.apply(block) != null
    }
}