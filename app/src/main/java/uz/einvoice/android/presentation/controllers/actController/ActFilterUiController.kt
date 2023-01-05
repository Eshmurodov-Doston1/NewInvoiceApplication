package uz.einvoice.android.presentation.controllers.actController

import androidx.lifecycle.lifecycleScope
import uz.einvoice.android.R
import uz.einvoice.android.databinding.FragmentFilterBinding
import uz.einvoice.android.presentation.activities.MainActivity
import uz.einvoice.android.presentation.screens.filterScreen.FilterFragment
import uz.einvoice.android.utils.extension.*
import uz.einvoice.android.vm.actVm.ActViewModel
import uz.einvoice.domain.models.act.actDocument.actDocumentFilter.ActDocumentFilter
import uz.einvoice.domain.models.branchModel.BranchModel
import uz.einvoice.domain.models.branchModel.Data
import uz.einvoice.domain.usesCase.apiUsesCase.parseClass
import uz.einvoice.domain.utils.loadState.ResponseState
import java.util.*

class ActFilterUiController(
    private val mainActivity: MainActivity,
    private val binding: FragmentFilterBinding,
    private val actViewModel: ActViewModel,
    private val filterFragment: FilterFragment
):ActController {

    override fun createAct() {
        binding.includeCreateFilter.apply {
            consCreateFilter.visible()
        }
    }
    // draft act
    private var actStartDate:String?=null
    private var actEndDate:String?=null
    // draft contract
    private var startContractDate:String?=null
    private var endContractDate:String?=null
    // draft sellerData
    private var branchDataMainSeller:Data?=null


    override fun documentViews(type: String) {
        binding.documentActFilter.apply {
            // act date
            dateActStart.setOnClickListener {
                datePickerFilter(0)
            }
            dateActEnd.setOnClickListener {
                datePickerFilter(0)
            }

            // contract date
            dateStartContract.setOnClickListener {
                datePickerFilter(1)
            }

            dateContractEnd.setOnClickListener {
                datePickerFilter(1)
            }
            // status check
            statusChecked.text = getStatusList()[0]
            statusChecked.setOnClickListener {
                mainActivity.containerApplication.applicationDialog(3,getStatusList()){ data ->
                    statusChecked.text = data
                }
            }

            // company branch
            subdivisionsEditTv.text = mainActivity.getString(R.string.choose)
            val userData = actViewModel.getUserData()
            actViewModel.getUserCompany(getLanguage(mainActivity),userData?.data?.tin.toString())
            filterFragment.lifecycleScope.launchWhenCreated {
                actViewModel.companyInfo.collect { result->
                    when(result){
                        is ResponseState.Loading->{
                            progressActDraft.visible()
                        }
                        is ResponseState.Success->{
                            progressActDraft.gone()
                            if (result.data[1]!=null && result.data[1].toString().isNotEmptyOrNull()){
                                val branchModel = result.data[1]?.parseClass(BranchModel::class.java)
                                if (branchModel?.data?.isNotEmpty() == true && branchModel.data.size > 1){
                                    subdivisionsEditTv.setOnClickListener {
                                        mainActivity.containerApplication.applicationDialog(0, branchModel.data){ data ->
                                            branchDataMainSeller =  data
                                            subdivisionsEditTv.text = data.branchName
                                            cancelBranchSeller.visible()
                                            cancelBranchSeller.setOnClickListener {
                                                subdivisionsEditTv.text = mainActivity.getString(R.string.choose)
                                                branchDataMainSeller = null
                                                cancelBranchSeller.gone()
                                            }
                                        }
                                    }
                                } else {
                                    subdivisionsEditTv.enabledFalse()
                                }
                            }
                        }
                        is ResponseState.Error->{
                            progressActDraft.gone()
                        }
                    }
                }
            }
            buttonFilter.setOnClickListener {
                val actNumber = docNumber.text.toString()
                val tin = inn.text.toString()
                val contractNumber = contractNumber.text.toString()
                val status = getStatus(statusChecked.text.toString(),mainActivity)

                val actDocumentFilter = ActDocumentFilter(actEndDate, actStartDate, actNumber, null, null,
                    endContractDate, startContractDate, contractNumber,null,null,branchDataMainSeller?.branchNum,if(tin.isNotEmptyOrNull()) tin.toLong() else null,status.toLong())
                filterFragment.lifecycleScope.launchWhenCreated {
                    mainActivity.containerViewModel.actFilter.emit(actDocumentFilter)
                    mainActivity.navHostFragment.navController.popBackStack()
                }
            }
        }
    }

    // list draft status
    private fun getStatusList():List<String>{
        val listStatus = LinkedList<String>()
        listStatus.add(mainActivity.getString(R.string.all_status))
        listStatus.add(mainActivity.getString(R.string.created))
        listStatus.add(mainActivity.getString(R.string.process_of_sending))
        listStatus.add(mainActivity.getString(R.string.error_while_settings))
        listStatus.add(mainActivity.getString(R.string.send_status))
        return listStatus
    }

    // date picker draft
    private fun datePickerFilter(type:Int){
        mainActivity.containerApplication.datePickerRange { time1,time2,timeFormat1,timeFormat2->
            when(type) {
                0 -> {
                    binding.documentActFilter.dateActStart.text = time1
                    binding.documentActFilter.dateActEnd.text = time2
                    actStartDate = timeFormat1
                    actEndDate = timeFormat2
                }
                1 -> {
                    binding.documentActFilter.dateStartContract.text = time1
                    binding.documentActFilter.dateContractEnd.text = time2
                    startContractDate = timeFormat1
                    endContractDate = timeFormat2
                }
            }
        }
    }

}