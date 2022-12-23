package uz.einvoice.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import uz.einvoice.domain.database.actProductEntity.ActProductDao
import uz.einvoice.domain.database.actProductEntity.ActProductEntity
import uz.einvoice.domain.database.errorModel.ErrorDao
import uz.einvoice.domain.database.errorModel.ErrorEntity
import uz.einvoice.domain.database.measure.MeasureDao
import uz.einvoice.domain.database.measure.MeasureEntity

@Database(entities = [ErrorEntity::class,MeasureEntity::class,ActProductEntity::class], version = 1)

abstract class AppDatabase:RoomDatabase() {
    abstract fun errorDao():ErrorDao
    abstract fun measureDao():MeasureDao
    abstract fun actProductDao():ActProductDao
}