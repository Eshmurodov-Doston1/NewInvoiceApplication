package uz.einvoice.android.vm.mainVM

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.JsonElement
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import uz.einvoice.domain.models.userInfo.UserInfoModel
import uz.einvoice.domain.usesCase.apiUsesCase.ApiUsesCase
import uz.einvoice.domain.usesCase.apiUsesCase.parseClass
import uz.einvoice.domain.utils.NetworkErrorException
import uz.einvoice.domain.utils.loadState.ResponseState
import uz.einvoice.android.utils.appConstant.AppConstant
import uz.einvoice.android.utils.appConstant.AppConstant.API
import uz.einvoice.android.utils.appConstant.AppConstant.EMPTY_MAP
import uz.einvoice.android.utils.appConstant.AppConstant.MENU
import uz.einvoice.android.utils.myshared.MySharedPreferences
import uz.einvoice.android.utils.networkHelper.NetworkHelper
import javax.inject.Inject
const val USER_DATA_PATH = "profile/me"
@HiltViewModel
class MainViewModel @Inject constructor(
    private val networkHelper: NetworkHelper,
    private val apiUsesCase: ApiUsesCase,
    private val mySharedPreferences: MySharedPreferences
):ViewModel() {
    // get menu list
    val menuState:StateFlow<ResponseState<JsonElement?>> get() = _menuState
    private val _menuState = MutableStateFlow<ResponseState<JsonElement?>>(ResponseState.Loading)

    fun getMenuList(lang:String) = viewModelScope.launch {
        if (networkHelper.isNetworkConnected()){
            val url = "/$API/$lang/$MENU"
            _menuState.emit(ResponseState.Loading)
            apiUsesCase.methodeGet(url, EMPTY_MAP).collect { response-> _menuState.emit(response) }
        }else{
            _menuState.emit(ResponseState.Error(NetworkErrorException(AppConstant.NO_INTERNET,"")))
        }
    }

    // get user Data
    val userData:StateFlow<ResponseState<JsonElement?>> get() = _userData
    private val _userData = MutableStateFlow<ResponseState<JsonElement?>>(ResponseState.Loading)

    fun getUserData(lang:String) = viewModelScope.launch {
        if (networkHelper.isNetworkConnected()){
            val url = "/$API/$lang/$USER_DATA_PATH"
            _userData.emit(ResponseState.Loading)
            apiUsesCase.methodeGet(url, EMPTY_MAP).collect { response->
                when(response){
                    is ResponseState.Success->{
                        val userData = response.data?.parseClass(UserInfoModel::class.java)
                        val userJson = Gson().toJson(userData)
                        mySharedPreferences.userData = userJson
                    }
                    else->{ResponseState.Error(Throwable(response.toString()))}
                }
                _userData.emit(response)
            }
        }else{
            _userData.emit(ResponseState.Error(NetworkErrorException(AppConstant.NO_INTERNET,"")))
        }
    }

}