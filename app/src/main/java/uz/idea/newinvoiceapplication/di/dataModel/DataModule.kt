package uz.idea.newinvoiceapplication.di.dataModel

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import uz.idea.data.repositories.apiRepositoryImpl.ApiRepositoryImpl
import uz.idea.domain.repositories.apiRepository.ApiRepository

@Module(includes = [DataModule.BindModule::class])
@InstallIn(SingletonComponent::class)
class DataModule {
    @Module
    @InstallIn(SingletonComponent::class)
    abstract class BindModule {
        @Binds
        abstract fun bindsApiRepository(apiRepositoryImpl: ApiRepositoryImpl): ApiRepository
    }
}