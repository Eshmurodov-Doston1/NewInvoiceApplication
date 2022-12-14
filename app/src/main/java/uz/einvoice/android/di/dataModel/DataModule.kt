package uz.einvoice.android.di.dataModel

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import uz.einvoice.data.repositories.apiRepositoryImpl.ApiRepositoryImpl
import uz.einvoice.data.repositories.databaseRepository.actProductRepoImpl.ActProductRepoImpl
import uz.einvoice.data.repositories.databaseRepository.measureRepoIml.MeasureRepoImpl
import uz.einvoice.domain.repositories.apiRepository.ApiRepository
import uz.einvoice.domain.repositories.dataBaseRepository.actProductRepo.ActProductRepo
import uz.einvoice.domain.repositories.dataBaseRepository.measureRepo.MeasureRepo

@Module(includes = [DataModule.BindModule::class])
@InstallIn(SingletonComponent::class)
class DataModule {
    @Module
    @InstallIn(SingletonComponent::class)
    abstract class BindModule {
        @Binds
        abstract fun bindsApiRepository(apiRepositoryImpl: ApiRepositoryImpl): ApiRepository

        @Binds
        abstract fun bindsMeasureRepository(measureRepoImpl: MeasureRepoImpl): MeasureRepo

        @Binds
        abstract fun bindsActProductRepo(actProductRepoImpl: ActProductRepoImpl): ActProductRepo
    }
}