package uz.einvoice.android.vm.authVm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonElement
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import uz.einvoice.domain.models.authModel.reqAuth.ReqAuthModel
import uz.einvoice.domain.models.authModel.resAuth.ResAuthModel
import uz.einvoice.domain.usesCase.apiUsesCase.ApiUsesCase
import uz.einvoice.android.utils.appConstant.AppConstant.API
import uz.einvoice.android.utils.appConstant.AppConstant.EMPTY_MAP
import uz.einvoice.android.utils.appConstant.AppConstant.LOGIN
import uz.einvoice.android.utils.appConstant.AppConstant.NO_INTERNET
import uz.einvoice.domain.utils.NetworkErrorException
import uz.einvoice.domain.utils.loadState.ResponseState
import uz.einvoice.android.utils.extension.logData
import uz.einvoice.android.utils.myshared.MySharedPreferences
import uz.einvoice.android.utils.networkHelper.NetworkHelper
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val apiUsesCase: ApiUsesCase,
    private val networkHelper: NetworkHelper,
    private val mySharedPreferences: MySharedPreferences
):ViewModel() {
    fun getMySharedPreferences() = mySharedPreferences
    // login data
    val login:StateFlow<ResponseState<JsonElement?>> get() = _login
    private val _login = MutableStateFlow<ResponseState<JsonElement?>>(ResponseState.Loading)

    fun loginApplication(language:String,reqAuthModel: ReqAuthModel) = viewModelScope.launch {
        val url =  "/$API/$language/$LOGIN"
        _login.emit(ResponseState.Loading)
        if (networkHelper.isNetworkConnected()){
            logData(reqAuthModel.toString())
            apiUsesCase.methodePOST(url,reqAuthModel,EMPTY_MAP).collect { response->
                _login.emit(response)
            }
        }else{
            _login.emit(ResponseState.Error(NetworkErrorException(NO_INTERNET,"")))
        }
    }

    fun saveAuthResponse(resAuthModel: ResAuthModel?){
        mySharedPreferences.accessToken = resAuthModel?.access_token
        mySharedPreferences.refreshToken = resAuthModel?.refresh_token
        mySharedPreferences.tokenType = resAuthModel?.token_type
    }
}