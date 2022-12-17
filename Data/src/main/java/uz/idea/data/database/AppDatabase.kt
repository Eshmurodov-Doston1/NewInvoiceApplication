package uz.idea.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import uz.idea.domain.database.actProductEntity.ActProductDao
import uz.idea.domain.database.actProductEntity.ActProductEntity
import uz.idea.domain.database.errorModel.ErrorDao
import uz.idea.domain.database.errorModel.ErrorEntity
import uz.idea.domain.database.measure.MeasureDao
import uz.idea.domain.database.measure.MeasureEntity

@Database(entities = [ErrorEntity::class,MeasureEntity::class,ActProductEntity::class], version = 1)

abstract class AppDatabase:RoomDatabase() {
    abstract fun errorDao():ErrorDao
    abstract fun measureDao():MeasureDao
    abstract fun actProductDao():ActProductDao
}