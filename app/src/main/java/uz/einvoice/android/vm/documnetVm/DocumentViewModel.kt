package uz.einvoice.android.vm.documnetVm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonElement
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import uz.einvoice.domain.repositories.dataBaseRepository.measureRepo.MeasureRepo
import uz.einvoice.domain.usesCase.apiUsesCase.ApiUsesCase
import uz.einvoice.domain.utils.NetworkErrorException
import uz.einvoice.domain.utils.loadState.ResponseState
import uz.einvoice.android.utils.appConstant.AppConstant.API
import uz.einvoice.android.utils.appConstant.AppConstant.EMPTY_MAP
import uz.einvoice.android.utils.appConstant.AppConstant.NO_INTERNET
import uz.einvoice.android.utils.networkHelper.NetworkHelper
import javax.inject.Inject

const val URL_VIEW = "documents/act/view"
const val URL_SIGN_VIEW = "documents/act/sign"
@HiltViewModel
class DocumentViewModel @Inject constructor(
    private val networkHelper: NetworkHelper,
    private val apiUsesCase: ApiUsesCase,
    private val measureRepo: MeasureRepo
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

    fun getMeasure(id:Int) = measureRepo.getMeasureEntity(id)

    // sign data
    val signData:StateFlow<ResponseState<JsonElement?>> get() = _signData
    private val _signData = MutableStateFlow<ResponseState<JsonElement?>>(ResponseState.Loading)
    fun getSignData(lang:String,docId:String) = viewModelScope.launch {
        if(networkHelper.isNetworkConnected()){
            val url = "/$API/$lang/$URL_SIGN_VIEW/$docId"
            _signData.emit(ResponseState.Loading)
            apiUsesCase.methodeGet(url, EMPTY_MAP).collect { response->
                _signData.emit(response)
            }
        }else{
            _signData.emit(ResponseState.Error(NetworkErrorException(NO_INTERNET,"")))
        }
    }
}