package uz.einvoice.android.vm.settingsViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonElement
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import uz.einvoice.domain.repositories.dataBaseRepository.actProductRepo.ActProductRepo
import uz.einvoice.domain.usesCase.apiUsesCase.ApiUsesCase
import uz.einvoice.domain.utils.NetworkErrorException
import uz.einvoice.domain.utils.loadState.ResponseState
import uz.einvoice.android.utils.appConstant.AppConstant
import uz.einvoice.android.utils.appConstant.AppConstant.API
import uz.einvoice.android.utils.appConstant.AppConstant.EMPTY
import uz.einvoice.android.utils.appConstant.AppConstant.EMPTY_MAP
import uz.einvoice.android.utils.myshared.MySharedPreferences
import uz.einvoice.android.utils.networkHelper.NetworkHelper
import javax.inject.Inject

const val LOG_OUT = "logout"
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val networkHelper: NetworkHelper,
    private val apiUsesCase: ApiUsesCase,
    private val mySharedPreferences: MySharedPreferences,
    private val actProductRepo: ActProductRepo
):ViewModel() {
    // logOut
    val logOut:StateFlow<ResponseState<JsonElement?>> get() = _logOut
    private val _logOut = MutableStateFlow<ResponseState<JsonElement?>>(ResponseState.Loading)

    fun logOut(lang:String) = viewModelScope.launch {
        if (networkHelper.isNetworkConnected()){
            _logOut.emit(ResponseState.Loading)
            val url = "/$API/$lang/$LOG_OUT"
            apiUsesCase.methodePOST(url, EMPTY, EMPTY_MAP).collect { response->
                _logOut.emit(response)
            }
        }else{
            _logOut.emit(ResponseState.Error(NetworkErrorException(AppConstant.NO_INTERNET,"")))
        }
    }

    fun clearShared() = mySharedPreferences.clearAuth()
}