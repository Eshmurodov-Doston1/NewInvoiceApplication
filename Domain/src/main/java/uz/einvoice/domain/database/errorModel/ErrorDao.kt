package uz.einvoice.domain.database.errorModel

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ErrorDao {
    @Insert
    fun saveError(errorEntity: ErrorEntity)

    @Query("DELETE FROM errorentity")
    fun deleteErrorTable()

    @Query("SELECT*FROM errorentity")
    fun getAllError():LiveData<List<ErrorEntity>>
}