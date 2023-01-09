package uz.einvoice.android.vm.containerVm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonElement
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import uz.einvoice.domain.database.measure.MeasureEntity
import uz.einvoice.domain.models.menuModel.Children
import uz.einvoice.domain.usesCase.apiUsesCase.ApiUsesCase
import uz.einvoice.domain.utils.NetworkErrorException
import uz.einvoice.domain.utils.loadState.ResponseState
import uz.einvoice.android.utils.appConstant.AppConstant
import uz.einvoice.android.utils.appConstant.AppConstant.API
import uz.einvoice.android.utils.appConstant.AppConstant.EMPTY_MAP
import uz.einvoice.android.utils.networkHelper.NetworkHelper
import uz.einvoice.domain.models.act.actDocument.actDocumentFilter.ActDocumentFilter
import javax.inject.Inject
const val MEASURE_PATH = "utils/measure/list"
const val TIMES_TAMP = "utils/timestamp"
@HiltViewModel
class ContainerViewModel @Inject constructor(
    private val networkHelper: NetworkHelper,
    private val apiUsesCase: ApiUsesCase
):ViewModel() {
    val children:StateFlow<Children?> get() = _children
    private val _children = MutableStateFlow<Children?>(null)

    fun setData(children: Children?) = viewModelScope.launch {
        _children.emit(children)
    }

    val actFilter = MutableStateFlow<ActDocumentFilter?>(null)
    val updateDocument = MutableLiveData(false)


    // measure list
    val measureList:StateFlow<ResponseState<List<MeasureEntity>?>> get() = _measureList
    private val _measureList = MutableStateFlow<ResponseState<List<MeasureEntity>?>>(ResponseState.Loading)

    fun getMeasure(lang:String) = viewModelScope.launch {
        if (networkHelper.isNetworkConnected()){
            val url = "$API/$lang/$MEASURE_PATH"
            _measureList.emit(ResponseState.Loading)
            apiUsesCase.getMeasureList(url, EMPTY_MAP).collect { response->
                _measureList.emit(response)
            }
        } else {
            _measureList.emit(ResponseState.Error(NetworkErrorException(AppConstant.NO_INTERNET,"")))
        }
    }
    // times tamp
    val timesTempData:StateFlow<ResponseState<JsonElement?>> get() = _timesTempData
    private val _timesTempData = MutableStateFlow<ResponseState<JsonElement?>>(ResponseState.Loading)

    fun getTimesTemp(lang:String,hex:String) = viewModelScope.launch{
        if (networkHelper.isNetworkConnected()){
            val url = "$API/$lang/$TIMES_TAMP"
            val headerMap = HashMap<String,String>()
            headerMap["signatureHex"] = hex
            _timesTempData.emit(ResponseState.Loading)
            apiUsesCase.methodeGet(url, headerMap).collect { response->
                _timesTempData.emit(response)
            }
        } else {
            _timesTempData.emit(ResponseState.Error(NetworkErrorException(AppConstant.NO_INTERNET,"")))
        }
    }
}