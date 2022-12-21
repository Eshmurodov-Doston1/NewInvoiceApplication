package uz.idea.newinvoiceapplication.presentation.controllers.actController

import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collect
import uz.idea.domain.models.act.actDraftModel.actDraftFilter.ActDraftFilter
import uz.idea.domain.models.branchModel.BranchModel
import uz.idea.domain.models.branchModel.Data
import uz.idea.domain.models.companyInfo.CompanyInfo
import uz.idea.domain.usesCase.apiUsesCase.parseClass
import uz.idea.domain.utils.loadState.ResponseState
import uz.idea.newinvoiceapplication.R
import uz.idea.newinvoiceapplication.databinding.FragmentFilterBinding
import uz.idea.newinvoiceapplication.presentation.activities.MainActivity
import uz.idea.newinvoiceapplication.presentation.screens.filterScreen.FilterFragment
import uz.idea.newinvoiceapplication.utils.extension.getLanguage
import uz.idea.newinvoiceapplication.utils.extension.gone
import uz.idea.newinvoiceapplication.utils.extension.logData
import uz.idea.newinvoiceapplication.utils.extension.visible
import uz.idea.newinvoiceapplication.vm.actVm.ActViewModel
import java.util.*

class ActFilterUiController(
    private val mainActivity: MainActivity,
    private val binding: FragmentFilterBinding,
    private val actViewModel: ActViewModel,
    private val filterFragment: FilterFragment
):ActController {

    override fun createAct() {

    }

    override fun incomingAct() {

    }

    override fun outgoingAct() {

    }

    // draft act
    private var actStartDate:String?=null
    private var actEndDate:String?=null
    // draft contract
    private var startContractDate:String?=null
    private var endContractDate:String?=null
    // draft sellerData
    private var branchDataMainSeller:Data?=null
    override fun draftAct() {
        binding.incomingActFilter.apply {
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
                val actDraftFilter = ActDraftFilter(actEndDate, actStartDate, actNumber, branchDataMainSeller?.branchNum, tin, endContractDate, startContractDate, contractNumber)
                filterFragment.lifecycleScope.launchWhenCreated {
                    mainActivity.containerViewModel.actFilter.emit(actDraftFilter)
                    mainActivity.navHostFragment.navController.popBackStack()
                }
            }
        }
    }

    override fun processSendingAct() {

    }

    // list draft status
    private fun getStatusList():List<String>{
        val listStatus = LinkedList<String>()
        listStatus.add(mainActivity.getString(R.string.all_status))
        listStatus.add(mainActivity.getString(R.string.confirmed_status))
        listStatus.add(mainActivity.getString(R.string.denied_status))
        listStatus.add(mainActivity.getString(R.string.signature_status))
        listStatus.add(mainActivity.getString(R.string.send_status))
        return listStatus
    }

    // date picker draft
    private fun datePickerFilter(type:Int){
        mainActivity.containerApplication.datePickerRange { time1,time2,timeFormat1,timeFormat2->
            when(type) {
                0 -> {
                    binding.incomingActFilter.dateActStart.text = time1
                    binding.incomingActFilter.dateActEnd.text = time2
                    actStartDate = timeFormat1
                    actEndDate = timeFormat2
                }
                1 -> {
                    binding.incomingActFilter.dateStartContract.text = time1
                    binding.incomingActFilter.dateContractEnd.text = time2
                    startContractDate = timeFormat1
                    endContractDate = timeFormat2
                }
            }
        }
    }

}