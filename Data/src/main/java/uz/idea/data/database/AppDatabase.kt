package uz.idea.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import uz.idea.domain.database.errorModel.ErrorDao
import uz.idea.domain.database.errorModel.ErrorEntity
import uz.idea.domain.database.measure.MeasureDao
import uz.idea.domain.database.measure.MeasureEntity

@Database(entities = [ErrorEntity::class,MeasureEntity::class], version = 1)

abstract class AppDatabase:RoomDatabase() {
    abstract fun errorDao():ErrorDao
    abstract fun measureDao():MeasureDao
}