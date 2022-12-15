package uz.idea.domain.database.measure

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface MeasureDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveMeasure(listMeasure: List<MeasureEntity>)

    @Query("SELECT*FROM measureentity")
    fun getAllMeasure():List<MeasureEntity>
}