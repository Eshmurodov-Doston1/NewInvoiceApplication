package uz.einvoice.android.di.dataBaseModel

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import uz.einvoice.data.database.AppDatabase
import uz.einvoice.domain.database.actProductEntity.ActProductDao
import uz.einvoice.domain.database.errorModel.ErrorDao
import uz.einvoice.domain.database.measure.MeasureDao
import uz.einvoice.android.utils.appConstant.AppConstant.COMPANY_NAME
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