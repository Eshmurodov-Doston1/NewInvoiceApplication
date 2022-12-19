package uz.idea.newinvoiceapplication.vm.documnetVm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonElement
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import uz.idea.domain.usesCase.apiUsesCase.ApiUsesCase
import uz.idea.domain.utils.NetworkErrorException
import uz.idea.domain.utils.loadState.ResponseState
import uz.idea.newinvoiceapplication.utils.appConstant.AppConstant.API
import uz.idea.newinvoiceapplication.utils.appConstant.AppConstant.EMPTY_MAP
import uz.idea.newinvoiceapplication.utils.appConstant.AppConstant.NO_INTERNET
import uz.idea.newinvoiceapplication.utils.myshared.MySharedPreferences
import uz.idea.newinvoiceapplication.utils.networkHelper.NetworkHelper
import javax.inject.Inject

const val URL_VIEW = "documents/act/view"
@HiltViewModel
class DocumentViewModel @Inject constructor(
    private val networkHelper: NetworkHelper,
    private val apiUsesCase: ApiUsesCase,
    private val mySharedPreferences: MySharedPreferences
):ViewModel() {
    // get document with id
    val document:StateFlow<ResponseState<JsonElement?>> get() = _document
    private val _document = MutableStateFlow<ResponseState<JsonElement?>>(ResponseState.Loading)

    fun getDocument(lang:String,actID:String) = viewModelScope.launch {
        if(networkHelper.isNetworkConnected()){
            val url = "/$API/$lang/$URL_VIEW/$actID"
            _document.emit(ResponseState.Loading)
            apiUsesCase.methodeGet(url, EMPTY_MAP).collect { response->
                _document.emit(response)
            }
        }else{
            _document.emit(ResponseState.Error(NetworkErrorException(NO_INTERNET,"")))
        }
    }
}