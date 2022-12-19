package uz.idea.newinvoiceapplication.vm.containerVm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonElement
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import uz.idea.domain.database.measure.MeasureEntity
import uz.idea.domain.models.menuModel.Children
import uz.idea.domain.models.menuModel.Data
import uz.idea.domain.usesCase.apiUsesCase.ApiUsesCase
import uz.idea.domain.utils.NetworkErrorException
import uz.idea.domain.utils.loadState.ResponseState
import uz.idea.newinvoiceapplication.utils.appConstant.AppConstant
import uz.idea.newinvoiceapplication.utils.appConstant.AppConstant.API
import uz.idea.newinvoiceapplication.utils.appConstant.AppConstant.EMPTY
import uz.idea.newinvoiceapplication.utils.appConstant.AppConstant.EMPTY_MAP
import uz.idea.newinvoiceapplication.utils.extension.logData
import uz.idea.newinvoiceapplication.utils.networkHelper.NetworkHelper
import javax.inject.Inject
const val MEASURE_PATH = "utils/measure/list"
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
}