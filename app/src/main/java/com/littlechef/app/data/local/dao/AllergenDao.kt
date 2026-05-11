package com.littlechef.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.littlechef.app.data.local.entity.AllergenEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AllergenDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(allergen: AllergenEntity)

    @Update
    suspend fun update(allergen: AllergenEntity)

    @Delete
    suspend fun delete(allergen: AllergenEntity)

    @Query("SELECT * FROM allergens WHERE id = :id")
    suspend fun getById(id: String): AllergenEntity?

    @Query("SELECT * FROM allergens")
    suspend fun getAll(): List<AllergenEntity>

    @Query("SELECT * FROM allergens")
    fun observeAll(): Flow<List<AllergenEntity>>

    @Query("SELECT * FROM allergens WHERE updated_at > :timestamp")
    suspend fun getChangedSince(timestamp: Long): List<AllergenEntity>
}
