package uz.einvoice.android.di.viewModelModule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import uz.einvoice.android.di.viewModelKeyModel.ViewModelKey
import uz.einvoice.android.vm.actVm.ActViewModel
import uz.einvoice.android.vm.authVm.AuthViewModel
import uz.einvoice.android.vm.containerVm.ContainerViewModel
import uz.einvoice.android.vm.mainVM.MainViewModel
import uz.einvoice.android.vm.settingsViewModel.SettingsViewModel
import uz.einvoice.android.vm.vmFactory.ViewModelFactory

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
    @IntoMap
    @ViewModelKey(ContainerViewModel::class)
    abstract fun bindsContainerViewModel(containerViewModel: ContainerViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ContainerViewModel::class)
    abstract fun bindsActViewModel(actViewModel: ActViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SettingsViewModel::class)
    abstract fun bindsSettingsViewModel(settingsViewModel: SettingsViewModel): ViewModel

    @Binds
    abstract fun bindsViewModelFactory(viewModelFactory: ViewModelFactory): ViewModelProvider.Factory

}