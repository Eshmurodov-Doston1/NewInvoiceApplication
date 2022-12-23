package uz.einvoice.domain.database.measure

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MeasureDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveMeasure(listMeasure: List<MeasureEntity>)

    @Query("SELECT*FROM measureentity")
    fun getAllMeasure():List<MeasureEntity>

    @Query("SELECT*FROM measureentity WHERE measureId= :id")
    fun getMeasure(id:Int):MeasureEntity
}