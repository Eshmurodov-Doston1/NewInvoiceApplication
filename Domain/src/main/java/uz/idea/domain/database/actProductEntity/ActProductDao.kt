package uz.idea.domain.database.actProductEntity

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface ActProductDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveProduct(productEntity: ActProductEntity)

   @Delete
    fun deleteProduct(productEntity: ActProductEntity)

    @Update
    fun updateProduct(productEntity: ActProductEntity)


    @Query("SELECT*FROM ActProductEntity")
    fun getAllActProductEntity():LiveData<List<ActProductEntity>>

    @Query("DELETE FROM ActProductEntity")
    fun deleteTableAct()
}