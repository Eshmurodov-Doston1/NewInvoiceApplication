package uz.idea.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import uz.idea.domain.database.errorModel.ErrorDao
import uz.idea.domain.database.errorModel.ErrorEntity

@Database(entities = [ErrorEntity::class], version = 1)
abstract class AppDatabase:RoomDatabase() {
    abstract fun errorDao():ErrorDao
}