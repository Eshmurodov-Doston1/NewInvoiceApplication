package uz.idea.newinvoiceapplication.di.viewModelModule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import uz.idea.newinvoiceapplication.di.viewModelKeyModel.ViewModelKey
import uz.idea.newinvoiceapplication.vm.authVm.AuthViewModel
import uz.idea.newinvoiceapplication.vm.mainVM.MainViewModel
import uz.idea.newinvoiceapplication.vm.vmFactory.ViewModelFactory

@Module
@InstallIn(SingletonComponent::class)
abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(AuthViewModel::class)
    abstract fun bindsAuthViewModel(authViewModel: AuthViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AuthViewModel::class)
    abstract fun bindsMainViewModel(mainViewModel: MainViewModel): ViewModel

    @Binds
    abstract fun bindsViewModelFactory(viewModelFactory: ViewModelFactory): ViewModelProvider.Factory

}