package uz.einvoice.android.presentation.screens.docDataScreen

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject
import uz.einvoice.android.R
import uz.einvoice.android.adapters.genericRvAdapter.GenericRvAdapter
import uz.einvoice.android.databinding.FragmentDocumentInfoBinding
import uz.einvoice.android.presentation.screens.baseFragment.BaseFragment
import uz.einvoice.android.utils.appConstant.AppConstant.CHECK_STATUS
import uz.einvoice.android.utils.appConstant.AppConstant.DELETE_TYPE
import uz.einvoice.android.utils.appConstant.AppConstant.DOCUMENT_ID
import uz.einvoice.android.utils.appConstant.AppConstant.DOCUMENT_STATE_ID
import uz.einvoice.android.utils.appConstant.AppConstant.DOC_STATUS
import uz.einvoice.android.utils.appConstant.AppConstant.DOC_TYPE
import uz.einvoice.android.utils.appConstant.AppConstant.ERROR_STATUS_UPDATE_ACT
import uz.einvoice.android.utils.appConstant.AppConstant.SELLER_TYPE
import uz.einvoice.android.utils.appConstant.AppConstant.STATE_ID
import uz.einvoice.android.utils.extension.*
import uz.einvoice.android.vm.actVm.ActViewModel
import uz.einvoice.android.vm.documnetVm.DocumentViewModel
import uz.einvoice.domain.models.act.actCopy.copyRequest.ActCopyModel
import uz.einvoice.domain.models.act.actCopy.responceCopyAct.ResActCopy
import uz.einvoice.domain.models.act.cancelAct.CancelAct
import uz.einvoice.domain.models.act.cancelAct.CancelActApp
import uz.einvoice.domain.models.act.deleteAct.requestDeleteAct.DeleteActModel
import uz.einvoice.domain.models.act.editStatus.editStatusrequest.EditStatusRequest
import uz.einvoice.domain.models.act.saveSignAct.SaveSignAct
import uz.einvoice.domain.models.documents.actDocument.ActDocument
import uz.einvoice.domain.models.localeClass.table.ActCreateTable
import uz.einvoice.domain.models.timesTemp.TimestempModel
import uz.einvoice.domain.usesCase.apiUsesCase.parseClass
import uz.einvoice.domain.utils.loadState.ResponseState
import uz.sicnt.horcrux.Constants.*
import java.math.BigDecimal
import java.util.*


@AndroidEntryPoint
class DocumentInfoFragment : BaseFragment<FragmentDocumentInfoBinding>() {
    private var docId:String?=null
    private var docStatus:Int?=-1
    private var stateId:Int?=-1
    private var documentStateId:Int?=-1
    // document viewModel
    private val documentViewModel:DocumentViewModel by viewModels()
    private var clickStatus:Int = -1
    private var docType:String?=null
    // click sign
    private var clickSign = 0
    override fun inflateViewBinding(inflater: LayoutInflater, container: ViewGroup?)
    = FragmentDocumentInfoBinding.inflate(inflater,container,false)

    private val actViewModel:ActViewModel by viewModels()

    // buttons
    private val tableAdapter:GenericRvAdapter<ActCreateTable> by lazy {
        GenericRvAdapter(R.layout.item_act_doc){ data, position, clickType ->

        }
    }

    private val tableAdapterId:GenericRvAdapter<String> by lazy {
        GenericRvAdapter(R.layout.item_id){ data, position, clickType ->

        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments.let {
            docId = it?.getString(DOCUMENT_ID,"")
            docStatus = it?.getInt(DOC_STATUS,0)
            stateId = it?.getInt(STATE_ID,0)
            documentStateId = it?.getInt(DOCUMENT_STATE_ID,-1)
            docType = it?.getString(DOC_TYPE,"")
        }
    }

    @SuppressLint("SetTextI18n")
    override fun init() {
        binding.apply {
            when(docStatus){
                1->{
                    mainActivity.containerViewModel.updateDocument.observeForever {
                        actDocument()
                        mainActivity.containerViewModel.updateDocument.removeObservers(this@DocumentInfoFragment)
                    }
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun actDocument(){

        binding.apply {

            includeActCreateDoc.copyDocId.setOnClickListener {
                val clipBoardManager = mainActivity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clipData = ClipData.newPlainText("copy_text",docId)
                clipBoardManager.setPrimaryClip(clipData)
                Toast.makeText(mainActivity, mainActivity.getString(R.string.copy_doc_id), Toast.LENGTH_SHORT).show()
            }

            documentViewModel.getDocument(getLanguage(requireContext()),docId.toString())
            lifecycleScope.launchWhenCreated {
                documentViewModel.document.collect { result->
                    when(result){
                        is ResponseState.Loading->{
                            progress.visible()
                        }
                        is ResponseState.Success->{
                            progress.gone()
                            val actDocument = result.data?.parseClass(ActDocument::class.java)
                            if (documentStateId!=null && docType.isNotEmptyOrNull()){
                                val checkDocType = checkDocType(docType?.lowercase().toString())
                                logData(documentStateId.toString())
                                when(documentStateId) {
                                    15->{
                                        if (checkDocType==2){
                                            checked.visible()
                                            cancel.visible()
                                            delete.gone()
                                            sendSign.gone()
                                            edit.gone()
                                            copy.gone()
                                            checked.setOnClickListener {
                                                // vxodyashi tomonidan tasdiqlash
                                            }
                                            cancel.setOnClickListener {
                                                cancelAct(actDocument)
                                            }
                                        } else if (checkDocType==3){
                                            cancel.visible()
                                            sendSign.gone()
                                            edit.gone()
                                            copy.gone()
                                            cancel.setOnClickListener {
                                                cancelAct(actDocument)
                                            }
                                        }
                                    }
                                    1,17,20,30->{
                                        if (checkDocType==2){
                                            // recive vxodyashi
                                            cancel.gone()
                                            sendSign.gone()
                                            edit.gone()
                                            delete.gone()
                                            copy.gone()

                                        } else if (checkDocType==3) {
                                            // send act isxodyashi
                                            cancel.gone()
                                            sendSign.gone()
                                            edit.gone()
                                            delete.gone()
                                        }
                                    }
                                }
                            }
                            includeActCreateDoc.idDoc.text = "ID: ${actDocument?.data?._id}"
                            includeActCreateDoc.actNumber.text = "${getString(R.string.act)} № ${actDocument?.data?.actdoc?.actno} \n ${getString(R.string.from)} ${getDateFormat(actDocument?.data?.actdoc?.actdate.toString(),requireContext())}"
                            includeActCreateDoc.contractNumber.text = "${getString(R.string.contract)} № ${actDocument?.data?.contractdoc?.contractno} \n ${getString(R.string.from)} ${getDateFormat(actDocument?.data?.contractdoc?.contractdate.toString(),requireContext())}"
                            includeActCreateDoc.textAct.text = actDocument?.data?.actdoc?.acttext
                            mainActivity.supportActionBar?.title = "${requireActivity().getString(R.string.act)} № ${actDocument?.data?.actdoc?.actno} ${requireActivity().getString(R.string.from)} ${actDocument?.data?.actdoc?.actdate}"
                            // update status act
                            editStatus.setOnClickListener {
                                actStatusUpdate(actDocument)
                            }
                            // copy act
                            copy.setOnClickListener {
                                actCopy(actDocument)
                            }
                            // delete act

                            delete.setOnClickListener {
                                mainActivity.containerApplication.dialogStatus(DELETE_TYPE,requireActivity().getString(R.string.delete_text)){ clickType ->
                                    if (clickType==1)  actDelete(actDocument)
                                }
                            }
                            // update act
                            edit.setOnClickListener {
                                mainActivity.containerApplication.screenNavigate.createUpdateDocumentScreen(1,docId)
                            }

                            // send
                            sendSign.setOnClickListener {
                                if (mainActivity.horcrux.isEImzoInstalled()){
                                    documentViewModel.getSignData(getLanguage(requireContext()),docId.toString())
                                    lifecycleScope.launchWhenCreated {
                                        documentViewModel.signData.collect { result->
                                            when(result){
                                                is ResponseState.Loading->{
                                                    mainActivity.containerApplication.loadingSaved(true)
                                                }
                                                is ResponseState.Success->{
                                                    val data = result.data?.asJsonObject
                                                    val jsonObject = JSONObject(data.toString())
                                                    if (jsonObject.has("data")) {
                                                        if (clickSign==0) {
                                                         clickSign++
                                                         //   mainActivity.horcrux.attachPkcs7()
                                                            mainActivity.horcrux.createPKCS7(
                                                                requireActivity(),
                                                                jsonObject.getJSONObject("data")
                                                                    .toString()
                                                            )
                                                            clickStatus = 0
                                                        }
                                                    }
                                                }
                                                is ResponseState.Error->{
                                                    val errorData = JsonParser.parseString(result.exception.localizedMessage).asJsonObject
                                                    errorDialog(errorData,actDocument)
                                                }
                                            }
                                        }
                                    }
                                }else{
                                    noSign()
                                }
                            }
                            tableList(actDocument)
                        }
                        is ResponseState.Error->{
                            progress.gone()
                        }
                    }
                }
            }
            includeActCreateDoc.rvData.adapter = tableAdapter
            includeActCreateDoc.rvId.adapter = tableAdapterId
        }
    }

    // act accptni api tayyor emas buni kurish kerak
    private fun actAccepted(actDocument: ActDocument?){
       // actViewModel.cancelAct()
    }


    private fun cancelAct(actDocument: ActDocument?){
        clickStatus = 1
        val cancelAct = CancelActApp(docId,actDocument?.data?.sellertin.toString())
        val gsonCancelAct = Gson().toJson(cancelAct)
        mainActivity.horcrux.createPKCS7(requireActivity(),gsonCancelAct)
       // actViewModel.cancelAct(getLanguage(requireContext()))
    }
    private fun errorDialog(errorData:JsonObject,actDocument: ActDocument?){
        mainActivity.containerApplication.dialogData(ERROR_STATUS_UPDATE_ACT,errorData){ clickType ->
            if (clickType==1) actStatusUpdate(actDocument)
        }
    }

    private fun noSign(){
        mainActivity.containerApplication.dialogStatus(0,mainActivity.getString(R.string.no_eimzo)){ clickType->
            if (clickType==1) {
                mainActivity.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$E_IMZO_APP")))
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        clickSign = 0
        when (requestCode) {
            CREATE_PKCS7->{

                if (data != null) {
                    val pkcs7 = Base64.encodeToString(data.getByteArrayExtra(EXTRA_RESULT_PKCS7), Base64.NO_WRAP)
                    val signature = mainActivity.horcrux.toHexString(data.getByteArrayExtra(EXTRA_RESULT_SIGNATURE))
                    val serialNumber = data.getCharSequenceExtra(EXTRA_RESULT_SERIAL_NUMBER)
                    mainActivity.containerViewModel.getTimesTemp(getLanguage(requireContext()), signature)
                    lifecycleScope.launchWhenCreated {
                        mainActivity.containerViewModel.timesTempData.collect { result->
                            when(result){
                                is ResponseState.Loading->{
                                    mainActivity.containerApplication.loadingSaved(true)
                                }
                                is ResponseState.Success->{
                                    val timesTemp = result.data?.parseClass(TimestempModel::class.java)
                                    mainActivity.horcrux.attachPkcs7(requireActivity(),pkcs7,serialNumber.toString(),timesTemp?.data?.token.toString())
                                }
                                is ResponseState.Error->{
                                    val errorData = JsonParser.parseString(result.exception.localizedMessage).asJsonObject
                                    mainActivity.containerApplication.dialogData(ERROR_STATUS_UPDATE_ACT,errorData){ clickType -> }
                                }
                            }
                        }
                    }
                } else {
                    mainActivity.containerApplication.loadingSaved(false)
                }
            }
            APPEND_CODE-> {
                val pkcs7 = Base64.encodeToString(data?.getByteArrayExtra(EXTRA_RESULT_PKCS7), Base64.NO_WRAP)
                val signature = mainActivity.horcrux.toHexString(data?.getByteArrayExtra(EXTRA_RESULT_SIGNATURE))
                val serialNumber = data?.getCharSequenceExtra(EXTRA_RESULT_SERIAL_NUMBER)
                mainActivity.containerViewModel.getTimesTemp(getLanguage(requireContext()), signature)
                lifecycleScope.launchWhenCreated {
                    mainActivity.containerViewModel.timesTempData.collect { result->
                        when(result){
                            is ResponseState.Loading->{
                                mainActivity.containerApplication.loadingSaved(true)
                            }
                            is ResponseState.Success->{
                                val timesTemp = result.data?.parseClass(TimestempModel::class.java)
                                mainActivity.horcrux.attachPkcs7(requireActivity(),pkcs7,serialNumber.toString(),timesTemp?.data?.token.toString())
                            }
                            is ResponseState.Error->{
                                val errorData = JsonParser.parseString(result.exception.localizedMessage).asJsonObject
                                mainActivity.containerApplication.dialogData(ERROR_STATUS_UPDATE_ACT,errorData){ clickType -> }
                            }
                        }
                    }
                }
            }
            ATTACH_CODE->{
                val pkcs7 = Base64.encodeToString(data!!.getByteArrayExtra(EXTRA_RESULT_PKCS7), Base64.NO_WRAP)
                val saveSignAct = SaveSignAct(docId.toString(), pkcs7)

                when(clickStatus){
                    0->{
                        actViewModel.saveSignAct(getLanguage(requireContext()),saveSignAct)
                        lifecycleScope.launchWhenCreated {
                            actViewModel.saveSignAct.collect { result->
                                when(result){
                                    is ResponseState.Loading->{
                                        mainActivity.containerApplication.loadingSaved(true)
                                    }
                                    is ResponseState.Success->{
                                        mainActivity.containerApplication.loadingSaved(false)
                                        mainActivity.containerApplication.dialogStatus(CHECK_STATUS,getString(R.string.check_sign)){
                                            init()
                                        }
                                    }
                                    is ResponseState.Error->{
                                        mainActivity.containerApplication.loadingSaved(false)
                                        val errorData = JsonParser.parseString(result.exception.localizedMessage).asJsonObject
                                        mainActivity.containerApplication.dialogData(ERROR_STATUS_UPDATE_ACT,errorData){ clickType -> }
                                    }
                                }
                            }
                        }
                    }
                    1->{
                        actViewModel.cancelAct(getLanguage(requireContext()), CancelAct(docId,pkcs7))
                        lifecycleScope.launchWhenCreated {
                            actViewModel.cancelActData.collect { result->
                                when(result){
                                    is ResponseState.Loading->{
                                        mainActivity.containerApplication.loadingSaved(true)
                                    }
                                    is ResponseState.Success->{
                                        mainActivity.containerApplication.loadingSaved(false)
                                        mainActivity.containerApplication.dialogStatus(CHECK_STATUS,getString(R.string.check_sign)){
                                            init()
                                        }
                                    }
                                    is ResponseState.Error->{
                                        mainActivity.containerApplication.loadingSaved(false)
                                        val errorData = JsonParser.parseString(result.exception.localizedMessage).asJsonObject
                                        mainActivity.containerApplication.dialogData(ERROR_STATUS_UPDATE_ACT,errorData){ clickType -> }
                                    }
                                }
                            }
                        }
                    }
                }
            }

        }
    }





    // TODO: Act status update
    private fun actStatusUpdate(actDocument: ActDocument?){
        val editStatusRequest = EditStatusRequest(actDocument?.data?._id.toString(),if (stateId == SELLER_TYPE) "seller" else "buyer")
        actViewModel.statusEditAct(getLanguage(requireContext()),editStatusRequest)
        lifecycleScope.launchWhenCreated {
            actViewModel.statusEditAct.collect { result->
                when(result){
                    is ResponseState.Loading->{
                        mainActivity.containerApplication.loadingSaved(true)
                    }
                    is ResponseState.Success->{
                        mainActivity.containerApplication.loadingSaved(false)
                        mainActivity.containerViewModel.updateDocument.postValue(true)
                    }
                    is ResponseState.Error->{
                        mainActivity.containerApplication.loadingSaved(false)
                        val errorAuth = JsonParser.parseString(result.exception.localizedMessage).asJsonObject
                        mainActivity.containerApplication.dialogData(ERROR_STATUS_UPDATE_ACT,errorAuth){ clickType ->
                            if (clickType==1) actStatusUpdate(actDocument)
                        }
                    }
                }
            }
        }
    }

    // TODO:  act copy
    private fun actCopy(actDocument: ActDocument?){
        mainActivity.containerApplication.dialogStatus(4,getString(R.string.check_type)){ clickType ->
            if (clickType==1){
                mainActivity.containerApplication.screenNavigate.createUpdateDocumentScreen(2,actDocument?.data?._id)
            }
        }
    }

    // TODO: act delete
    private fun actDelete(actDocument: ActDocument?){
        val deleteActModel = DeleteActModel(actDocument?.data?._id.toString())
        actViewModel.deleteAct(getLanguage(requireContext()),deleteActModel)
        lifecycleScope.launchWhenCreated {
            actViewModel.deleteAct.collect { result->
                when(result){
                    is ResponseState.Loading->{
                        mainActivity.containerApplication.loadingSaved(true)
                    }
                    is ResponseState.Success->{
                        mainActivity.containerApplication.loadingSaved(false)
                        binding.menuViewAct.close(true)
                        findNavController().popBackStack()
                    }
                    is ResponseState.Error->{
                        mainActivity.containerApplication.loadingSaved(false)
                        binding.menuViewAct.close(true)
                        val errorAuth = JsonParser.parseString(result.exception.localizedMessage).asJsonObject
                        mainActivity.containerApplication.dialogData(ERROR_STATUS_UPDATE_ACT,errorAuth){ clickType ->
                            if (clickType==1) actStatusUpdate(actDocument)
                        }
                    }
                }
            }
        }
    }


    @SuppressLint("SetTextI18n")
    private fun tableList(actDocument: ActDocument?):List<ActCreateTable>{
        val actCreateTableList = LinkedList<ActCreateTable>()
        val orderList = LinkedList<String>()
        actCreateTableList.add(ActCreateTable("№",getString(R.string.act_table_two_text),getString(R.string.name_works),getString(R.string.measure_table),
        getString(R.string.count_table),getString(R.string.summa_product_table),getString(R.string.summa_total_table)))
        orderList.add("№")
        var allSumma = BigDecimal(0)
        actDocument?.data?.productlist?.products?.onEach {  product->
            orderList.add(product.ordno)
            actCreateTableList.add(ActCreateTable(product.ordno,"${product.catalogcode?:""} - ${product.catalogname?:""}",product.name,documentViewModel.getMeasure(product.measureid).name, product.count.toDouble().toString(), product.summa.toDouble().numberFormatter(), product.totalsum.toDouble().numberFormatter()))
            allSumma +=  BigDecimal(product.totalsum.toDouble())
        }
        binding.includeActCreateDoc.totalSumma.text = allSumma.toDouble().numberFormatter()
        binding.includeActCreateDoc.textActSumma.text = "${getString(R.string.act_summa)} ${actDocument?.data?.payabletotal?.toDouble()?.numberFormatter()}"
        var sellerBranch =""
        var buyerBranch =""
        if (actDocument?.data?.sellerbranchname.toString() != "null"){
            sellerBranch = "(${actDocument?.data?.sellerbranchname.toString()})"
        }
        if (actDocument?.data?.buyerbranchname.toString() != "null"){
            sellerBranch = "(${actDocument?.data?.buyerbranchname.toString()})"
        }
        binding.includeActCreateDoc.executorName.text = "${actDocument?.data?.sellername} $sellerBranch"
        binding.includeActCreateDoc.customerName.text = "${actDocument?.data?.buyername} $buyerBranch"
        tableAdapter.submitList(actCreateTableList)
        tableAdapterId.submitList(orderList)
        return actCreateTableList
    }



}