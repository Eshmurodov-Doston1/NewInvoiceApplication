package uz.idea.newinvoiceapplication.vm.authVm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonElement
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import uz.idea.domain.models.authModel.reqAuth.ReqAuthModel
import uz.idea.domain.usesCase.apiUsesCase.ApiUsesCase
import uz.idea.newinvoiceapplication.utils.AppConstant.API
import uz.idea.newinvoiceapplication.utils.AppConstant.EMPTY_MAP
import uz.idea.newinvoiceapplication.utils.AppConstant.LOGIN
import uz.idea.newinvoiceapplication.utils.AppConstant.NO_INTERNET
import uz.idea.domain.utils.NetworkErrorException
import uz.idea.domain.utils.loadState.ResponseState
import uz.idea.newinvoiceapplication.utils.myshared.MySharedPreferences
import uz.idea.newinvoiceapplication.utils.networkHelper.NetworkHelper
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val apiUsesCase: ApiUsesCase,
    private val networkHelper: NetworkHelper,
    private val mySharedPreferences: MySharedPreferences
):ViewModel() {
    // login data
    val login:StateFlow<ResponseState<JsonElement?>> get() = _login
    private val _login = MutableStateFlow<ResponseState<JsonElement?>>(ResponseState.Loading)

    fun loginApplication(language:String,reqAuthModel: ReqAuthModel) = viewModelScope.launch {
        if (networkHelper.isNetworkConnected()){
            val url =  "/$API/$language/$LOGIN"
            _login.emit(ResponseState.Loading)
            apiUsesCase.methodePOST(url,reqAuthModel,EMPTY_MAP).collect { response->
                _login.emit(response)
            }
        }else{
            _login.emit(ResponseState.Error(NetworkErrorException(NO_INTERNET,"")))
        }
    }
}