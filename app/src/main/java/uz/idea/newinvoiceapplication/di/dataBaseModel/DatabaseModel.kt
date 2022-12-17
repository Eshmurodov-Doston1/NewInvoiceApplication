package uz.idea.newinvoiceapplication.di.dataBaseModel

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import uz.idea.data.database.AppDatabase
import uz.idea.domain.database.actProductEntity.ActProductDao
import uz.idea.domain.database.errorModel.ErrorDao
import uz.idea.domain.database.measure.MeasureDao
import uz.idea.newinvoiceapplication.utils.appConstant.AppConstant.COMPANY_NAME
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModel {
    @Provides
    @Singleton
    fun providesDataBase(@ApplicationContext context: Context):AppDatabase {
        return Room.databaseBuilder(context,AppDatabase::class.java,COMPANY_NAME)
            .fallbackToDestructiveMigration()
            .allowMainThreadQueries()
            .build()
    }
    @Provides
    @Singleton
    fun providesErrorDao(appDatabase: AppDatabase): ErrorDao = appDatabase.errorDao()

    @Provides
    @Singleton
    fun providesMeasureDao(appDatabase: AppDatabase): MeasureDao = appDatabase.measureDao()

    @Provides
    @Singleton
    fun providesActProductDao(appDatabase: AppDatabase): ActProductDao = appDatabase.actProductDao()
}