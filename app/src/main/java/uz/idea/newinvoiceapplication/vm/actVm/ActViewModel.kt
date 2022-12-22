package uz.idea.newinvoiceapplication.vm.actVm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonElement
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import uz.idea.domain.database.actProductEntity.ActProductEntity
import uz.idea.domain.models.act.actCopy.copyRequest.ActCopyModel
import uz.idea.domain.models.act.actDraftModel.actDraftFilter.ActDraftFilter
import uz.idea.domain.models.act.actIncoming.incomingFilterModule.IncomingFilterModel
import uz.idea.domain.models.act.actSend.actSendFilter.ActSendFilter
import uz.idea.domain.models.act.deleteAct.requestDeleteAct.DeleteActModel
import uz.idea.domain.models.act.editStatus.editStatusrequest.EditStatusRequest
import uz.idea.domain.models.createActModel.CreateActModel
import uz.idea.domain.models.userInfo.UserInfoModel
import uz.idea.domain.repositories.dataBaseRepository.actProductRepo.ActProductRepo
import uz.idea.domain.repositories.dataBaseRepository.measureRepo.MeasureRepo
import uz.idea.domain.usesCase.actUsesCase.ActUsesCase
import uz.idea.domain.usesCase.apiUsesCase.ApiUsesCase
import uz.idea.domain.usesCase.pagingUsesCase.actPaging.DraftPagingUsesCase
import uz.idea.domain.utils.NetworkErrorException
import uz.idea.domain.utils.loadState.ResponseState
import uz.idea.newinvoiceapplication.utils.appConstant.AppConstant
import uz.idea.newinvoiceapplication.utils.appConstant.AppConstant.API
import uz.idea.newinvoiceapplication.utils.appConstant.AppConstant.EMPTY_MAP
import uz.idea.newinvoiceapplication.utils.extension.parseClass
import uz.idea.newinvoiceapplication.utils.myshared.MySharedPreferences
import uz.idea.newinvoiceapplication.utils.networkHelper.NetworkHelper
import javax.inject.Inject

const val COMPANY_INFO = "rouming/company/info"
const val BRANCHES = "rouming/branch/info"
const val PRODUCT_PATH = "provider/tasnif"
const val UNIQUE_PATH = "utils/get/id"
const val ACT_PATH = "documents/act/save"
const val ACT_UPDATE_PATH = "documents/act/update"
const val ACT_QUEUE_PATH = "documents/act/queue"
const val ACT_STATUS_EDIT = "documents/act/update/status"
const val COPY_ACT = "documents/act/clone"
const val DELETE_ACT = "documents/act/delete"
@HiltViewModel
class ActViewModel @Inject constructor(
    private val networkHelper: NetworkHelper,
    private val apiUsesCase: ApiUsesCase,
    private val actUsesCase: ActUsesCase,
    private val mySharedPreferences: MySharedPreferences,
    private val measureRepo: MeasureRepo,
    private val actProductRepo: ActProductRepo,
    private val draftPagingUsesCase: DraftPagingUsesCase
):ViewModel() {
    fun getUserData() = mySharedPreferences.userData?.parseClass(UserInfoModel::class.java)

    // company info
    val companyInfo:StateFlow<ResponseState<List<JsonElement?>>> get() = _companyInfo
    private val _companyInfo = MutableStateFlow<ResponseState<List<JsonElement?>>>(ResponseState.Loading)

    fun getUserCompany(lang:String,tin:String) = viewModelScope.launch {
        if (networkHelper.isNetworkConnected()){
            _companyInfo.emit(ResponseState.Loading)
            val queryMapCompanyAndBranches = HashMap<String,String>()
            queryMapCompanyAndBranches["tin"] = tin

            val urlCompanyInfo = "/$API/$lang/$COMPANY_INFO"

            val urlBranches = "/$API/$lang/$BRANCHES"

            actUsesCase.getActData(
                urlCompanyInfo,queryMapCompanyAndBranches,
                urlBranches,queryMapCompanyAndBranches
            ).collect { response->
                _companyInfo.emit(response)
            }
        }else{
            _companyInfo.emit(ResponseState.Error(NetworkErrorException(AppConstant.NO_INTERNET,"")))
        }
    }


    // buyerData info
    val buyerData:StateFlow<ResponseState<List<JsonElement?>>> get() = _buyerData
    private val _buyerData = MutableStateFlow<ResponseState<List<JsonElement?>>>(ResponseState.Loading)

    fun buyerData(lang:String,tin:String) = viewModelScope.launch {
        if (networkHelper.isNetworkConnected()){
            _buyerData.emit(ResponseState.Loading)
            val queryMapCompanyAndBranches = HashMap<String,String>()
            queryMapCompanyAndBranches["tin"] = tin

            val urlCompanyInfo = "/$API/$lang/$COMPANY_INFO"

            val urlBranches = "/$API/$lang/$BRANCHES"

            actUsesCase.getActData(
                urlCompanyInfo,queryMapCompanyAndBranches,
                urlBranches,queryMapCompanyAndBranches
            ).collect { response->
                _buyerData.emit(response)
            }
        }else{
            _buyerData.emit(ResponseState.Error(NetworkErrorException(AppConstant.NO_INTERNET,"")))
        }
    }

    // buyerData info
    val productData:StateFlow<ResponseState<JsonElement?>> get() = _productData
    private val _productData = MutableStateFlow<ResponseState<JsonElement?>>(ResponseState.Loading)

    fun productData(lang:String) = viewModelScope.launch {
        if (networkHelper.isNetworkConnected()){
            _productData.emit(ResponseState.Loading)
            val url = "/$API/$lang/$PRODUCT_PATH"

            apiUsesCase.methodeGet(url, EMPTY_MAP).collect { response->
                _productData.emit(response)
            }
        }else{
            _productData.emit(ResponseState.Error(NetworkErrorException(AppConstant.NO_INTERNET,"")))
        }
    }



    // unique id
    val uniqueId:StateFlow<ResponseState<List<JsonElement?>>> get() = _uniqueId
    private val _uniqueId = MutableStateFlow<ResponseState<List<JsonElement?>>>(ResponseState.Loading)
    fun getUniqueId(lang:String){
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()){
                val url = "/$API/$lang/$UNIQUE_PATH"
                _uniqueId.emit(ResponseState.Loading)
                actUsesCase.getDoubleUniqueId(url, EMPTY_MAP).collect { response->
                    _uniqueId.emit(response)
                }
            }else{
                _uniqueId.emit(ResponseState.Error(NetworkErrorException(AppConstant.NO_INTERNET,"")))
            }
        }
    }


    // save act
    val saveAct:StateFlow<ResponseState<JsonElement?>> get() = _saveAct
    private val _saveAct = MutableStateFlow<ResponseState<JsonElement?>>(ResponseState.Loading)

    fun saveAct(lang:String,createActModel: CreateActModel) = viewModelScope.launch {
        if (networkHelper.isNetworkConnected()){
            val url = "/$API/$lang/$ACT_PATH"
            _saveAct.emit(ResponseState.Loading)
                apiUsesCase.methodePOST(url,createActModel, EMPTY_MAP).collect { response->
                    _saveAct.emit(response)
                }
        }else {
            _saveAct.emit(ResponseState.Error(NetworkErrorException(AppConstant.NO_INTERNET,"")))
        }
    }

    // save act
    val updateAct:StateFlow<ResponseState<JsonElement?>> get() = _updateAct
    private val _updateAct = MutableStateFlow<ResponseState<JsonElement?>>(ResponseState.Loading)

    fun updateAct(lang:String,createActModel: CreateActModel) = viewModelScope.launch {
        if (networkHelper.isNetworkConnected()){
            val url = "/$API/$lang/$ACT_UPDATE_PATH"
            _updateAct.emit(ResponseState.Loading)
            apiUsesCase.methodePOST(url,createActModel, EMPTY_MAP).collect { response->
                _updateAct.emit(response)
            }
        }else {
            _updateAct.emit(ResponseState.Error(NetworkErrorException(AppConstant.NO_INTERNET,"")))
        }
    }

    // act status edit
    val statusEditAct:StateFlow<ResponseState<JsonElement?>> get() = _statusEditAct
    private val _statusEditAct = MutableStateFlow<ResponseState<JsonElement?>>(ResponseState.Loading)

    fun statusEditAct(lang:String,editStatusRequest: EditStatusRequest) = viewModelScope.launch {
        if (networkHelper.isNetworkConnected()){
            val url = "/$API/$lang/$ACT_STATUS_EDIT"
            _statusEditAct.emit(ResponseState.Loading)
            apiUsesCase.methodePOST(url, editStatusRequest, EMPTY_MAP).collect { response->
                _statusEditAct.emit(response)
            }
        }else {
            _statusEditAct.emit(ResponseState.Error(NetworkErrorException(AppConstant.NO_INTERNET,"")))
        }
    }

 // act status edit
    val copyAct:StateFlow<ResponseState<JsonElement?>> get() = _copyAct
    private val _copyAct = MutableStateFlow<ResponseState<JsonElement?>>(ResponseState.Loading)

    fun actCopy(lang:String,actCopyModel: ActCopyModel) = viewModelScope.launch {
        if (networkHelper.isNetworkConnected()){
            val url = "/$API/$lang/$COPY_ACT"
            _copyAct.emit(ResponseState.Loading)
            apiUsesCase.methodePOST(url, actCopyModel, EMPTY_MAP).collect { response->
                _copyAct.emit(response)
            }
        }else {
            _copyAct.emit(ResponseState.Error(NetworkErrorException(AppConstant.NO_INTERNET,"")))
        }
    }


    // act status edit
    val deleteAct:StateFlow<ResponseState<JsonElement?>> get() = _deleteAct
    private val _deleteAct = MutableStateFlow<ResponseState<JsonElement?>>(ResponseState.Loading)

    fun deleteAct(lang:String,deleteActModel: DeleteActModel) = viewModelScope.launch {
        if (networkHelper.isNetworkConnected()){
            val url = "/$API/$lang/$DELETE_ACT"
            _deleteAct.emit(ResponseState.Loading)
            apiUsesCase.methodePOST(url, deleteActModel, EMPTY_MAP).collect { response->
                _deleteAct.emit(response)
            }
        }else {
            _deleteAct.emit(ResponseState.Error(NetworkErrorException(AppConstant.NO_INTERNET,"")))
        }
    }




    // act draft
    fun actDraftData(lang:String,body: ActDraftFilter)
    = draftPagingUsesCase.getDraftData("/$API/$lang/documents/act/draft",body, EMPTY_MAP)
 // act Incoming
    fun actIncomingData(lang:String,body: IncomingFilterModel)
    = draftPagingUsesCase.getIncomingData("/$API/$lang/documents/act/receive",body, EMPTY_MAP)
    // act send
    fun actSendData(lang:String,body: ActSendFilter)
    = draftPagingUsesCase.getActSendData("/$API/$lang/documents/act/send",body, EMPTY_MAP)

    // act queue
    fun actProcess(lang:String,body: ActSendFilter)
    = draftPagingUsesCase.getActSendData("/$API/$lang/documents/act/queue",body, EMPTY_MAP)

    // measure
    fun getMeasure() = measureRepo.getAllMeasureEntity()

    // save act product
    fun saveActProduct(actProductEntity: ActProductEntity) = actProductRepo.saveProduct(actProductEntity)
    // update act product
    fun updateActProduct(actProductEntity: ActProductEntity) = actProductRepo.updateProduct(actProductEntity)
    // delete product
    fun deleteActProduct(actProductEntity: ActProductEntity) = actProductRepo.deleteProduct(actProductEntity)
    // get all product
    fun getAllActProduct() = actProductRepo.getAllProductEntity()

    // delete table
    fun deleteTableActProduct() = actProductRepo.deleteTableActProduct()

}