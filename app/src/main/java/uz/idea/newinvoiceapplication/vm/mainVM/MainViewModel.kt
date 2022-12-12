package uz.idea.newinvoiceapplication.vm.mainVM

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonElement
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import uz.idea.domain.usesCase.apiUsesCase.ApiUsesCase
import uz.idea.domain.utils.NetworkErrorException
import uz.idea.domain.utils.loadState.ResponseState
import uz.idea.newinvoiceapplication.utils.appConstant.AppConstant
import uz.idea.newinvoiceapplication.utils.appConstant.AppConstant.API
import uz.idea.newinvoiceapplication.utils.appConstant.AppConstant.EMPTY_MAP
import uz.idea.newinvoiceapplication.utils.appConstant.AppConstant.MENU
import uz.idea.newinvoiceapplication.utils.networkHelper.NetworkHelper
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val networkHelper: NetworkHelper,
    private val apiUsesCase: ApiUsesCase,
):ViewModel() {
    // get menu list
    val menuState:StateFlow<ResponseState<JsonElement?>> get() = _menuState
    private val _menuState = MutableStateFlow<ResponseState<JsonElement?>>(ResponseState.Loading)

    fun getMenuList(lang:String) = viewModelScope.launch {
        if (networkHelper.isNetworkConnected()){
            val url = "/$API/$lang/$MENU"
            _menuState.emit(ResponseState.Loading)
            apiUsesCase.methodeGet(url, EMPTY_MAP).collect { response->
                _menuState.emit(response)
            }
        }else{
            _menuState.emit(ResponseState.Error(NetworkErrorException(AppConstant.NO_INTERNET,"")))
        }
    }
}