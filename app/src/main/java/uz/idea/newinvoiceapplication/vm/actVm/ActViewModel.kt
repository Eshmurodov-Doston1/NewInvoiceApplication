package uz.idea.newinvoiceapplication.vm.actVm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonElement
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import uz.idea.domain.models.userInfo.ActiveCompanyInfo
import uz.idea.domain.models.userInfo.UserInfoModel
import uz.idea.domain.usesCase.actUsesCase.ActUsesCase
import uz.idea.domain.usesCase.apiUsesCase.ApiUsesCase
import uz.idea.domain.utils.NetworkErrorException
import uz.idea.domain.utils.loadState.ResponseState
import uz.idea.newinvoiceapplication.utils.appConstant.AppConstant
import uz.idea.newinvoiceapplication.utils.appConstant.AppConstant.API
import uz.idea.newinvoiceapplication.utils.extension.isNotEmptyOrNull
import uz.idea.newinvoiceapplication.utils.extension.parseClass
import uz.idea.newinvoiceapplication.utils.myshared.MySharedPreferences
import uz.idea.newinvoiceapplication.utils.networkHelper.NetworkHelper
import javax.inject.Inject
const val COMPANY_INFO = "rouming/company/info"
const val BRANCHES = "rouming/branch/info"
@HiltViewModel
class ActViewModel @Inject constructor(
    private val networkHelper: NetworkHelper,
    private val apiUsesCase: ApiUsesCase,
    private val actUsesCase: ActUsesCase,
    private val mySharedPreferences: MySharedPreferences
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

}