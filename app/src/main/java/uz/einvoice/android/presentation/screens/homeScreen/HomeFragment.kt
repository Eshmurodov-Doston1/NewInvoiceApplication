package uz.einvoice.android.presentation.screens.homeScreen

import android.content.Intent
import android.util.Base64
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.gson.JsonParser
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject
import uz.einvoice.android.R
import uz.einvoice.android.databinding.FragmentHomeBinding
import uz.einvoice.android.presentation.controllers.actController.ActUiController
import uz.einvoice.android.presentation.screens.baseFragment.BaseFragment
import uz.einvoice.android.utils.appConstant.AppConstant
import uz.einvoice.android.utils.appConstant.AppConstant.ACT_ADD
import uz.einvoice.android.utils.appConstant.AppConstant.ACT_DRAFT
import uz.einvoice.android.utils.appConstant.AppConstant.ACT_QUEUE
import uz.einvoice.android.utils.appConstant.AppConstant.ACT_RECEIVE
import uz.einvoice.android.utils.appConstant.AppConstant.ACT_SENT
import uz.einvoice.android.utils.extension.getLanguage
import uz.einvoice.android.utils.extension.gone
import uz.einvoice.android.utils.extension.logData
import uz.einvoice.android.utils.extension.visible
import uz.einvoice.android.utils.language.LocaleManager
import uz.einvoice.android.vm.actVm.ActViewModel
import uz.einvoice.android.vm.documnetVm.DocumentViewModel
import uz.einvoice.android.vm.mainVM.MainViewModel
import uz.einvoice.domain.models.act.actDocument.actDocumentData.ActDocumentData
import uz.einvoice.domain.models.act.saveSignAct.SaveSignAct
import uz.einvoice.domain.models.timesTemp.TimestempModel
import uz.einvoice.domain.usesCase.apiUsesCase.parseClass
import uz.einvoice.domain.utils.loadState.ResponseState
import uz.sicnt.horcrux.Constants
import kotlin.math.abs

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>() {
    // act controller
    private lateinit var actUIController: ActUiController<Any>
    // act view Model
    private val actViewModel:ActViewModel by viewModels()
    // main view model
    private val mainViewModel:MainViewModel by viewModels()
    private var isCreate = false
    // document viewModel
    private val documentViewModel:DocumentViewModel by viewModels()
    // doc id
    private var docId:String?=""
    // click sign
    private var clickSign = 0
    override fun init() {
        binding.apply {

            lifecycleScope.launchWhenCreated {
                mainActivity.containerViewModel.children.collect { children->
                    mainActivity.liveData.observe(viewLifecycleOwner) { data ->
                        when(LocaleManager.getLanguage(requireContext())){
                            AppConstant.UZ -> {
                                mainActivity.supportActionBar?.title = data.title_uz + " | ${children?.title_uz}"
                            }
                            AppConstant.RU ->{
                                mainActivity.supportActionBar?.title = data.title + " | ${children?.title}"
                            }
                            AppConstant.EN ->{
                                mainActivity.supportActionBar?.title = data.title_uz + " | ${children?.title_uz}"
                            }
                            else->{
                                mainActivity.supportActionBar?.title = data.title + " | ${children?.title}"
                            }
                        }
                    }
                }
            }

            mainViewModel.getUserData(getLanguage(requireContext()))
            lifecycleScope.launchWhenResumed {
                mainActivity.containerViewModel.children.collect { children->
                    when(children?.path){
                        ACT_ADD->{
                            if (!isCreate)  actUiController()
                            binding.includeActCreate.nested.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, _, scrollY, _, oldScrollY ->
                                if (scrollY < oldScrollY) {
                                    mainActivity.bottomBarView(true)
                                }
                                if (scrollY == abs( v.measuredHeight - v.getChildAt(0).measuredHeight)) {
                                    mainActivity.bottomBarView(false)
                                }
                            })
                            actUIController.createAct()
                            binding.includeActDocument.consDocuments.gone()
                            binding.includeActCreate.consActCreate.visible()
                        }
                        else ->{
                          if (!isCreate) actUiController()
                            binding.includeActDocument.consDocuments.visible()
                            binding.includeActCreate.consActCreate.gone()

                          actUIController.documentViews(children?.path.toString())
                        }
                    }
                }
            }
        }
    }




    private fun  actUiController(){
      try {
          actUIController = ActUiController(mainActivity, binding, actViewModel, this){ data, _ ,typeDoc:String->
              if (data is ActDocumentData) {
                  docId = data._id
                  when(typeDoc){
                      ACT_DRAFT->{
                          sendSign(data)
                      }
                      ACT_RECEIVE->{
//                          val decode = Base64.decode(base, Base64.NO_WRAP).decodeToString()
//                          logData(decode)
//                          mainActivity.horcrux.appendPkcs7(mainActivity,decode,mainActivity.horcrux.getSerialNumber())
                      }
                      ACT_SENT->{

                      }
                      ACT_QUEUE->{

                      }
                  }
              }
          }
          isCreate = true
      }catch (e:Exception){
          e.printStackTrace()
      }
    }


    private fun sendSign(data:ActDocumentData){
        documentViewModel.getSignData(getLanguage(requireContext()), data._id)
        lifecycleScope.launchWhenCreated {
            documentViewModel.signData.collect { result ->
                when (result) {
                    is ResponseState.Loading -> {
                        mainActivity.containerApplication.loadingSaved(true)
                    }
                    is ResponseState.Success -> {
                        val dataResult = result.data?.asJsonObject
                        val jsonObject = JSONObject(dataResult.toString())
                        if (jsonObject.has("data")) {
                            if (clickSign == 0) {
                                clickSign++
                                mainActivity.horcrux.createPKCS7(
                                    requireActivity(),
                                    jsonObject.getJSONObject("data").toString())
                            }
                        }
                    }
                    is ResponseState.Error -> {
                        val errorData =
                            JsonParser.parseString(result.exception.localizedMessage).asJsonObject
                        mainActivity.containerApplication.dialogData(AppConstant.ERROR_STATUS_UPDATE_ACT, errorData) { clickType ->
                            if (clickType == 1) actUiController()
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        isCreate = false
    }




    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        clickSign = 0
        when (requestCode) {
            Constants.CREATE_PKCS7 ->{
                if (data != null) {
                    val pkcs7 = Base64.encodeToString(data.getByteArrayExtra(Constants.EXTRA_RESULT_PKCS7), Base64.NO_WRAP)
                    val signature = mainActivity.horcrux.toHexString(data.getByteArrayExtra(
                        Constants.EXTRA_RESULT_SIGNATURE
                    ))
                    val serialNumber = data.getCharSequenceExtra(Constants.EXTRA_RESULT_SERIAL_NUMBER)
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
                                    mainActivity.containerApplication.dialogData(AppConstant.ERROR_STATUS_UPDATE_ACT,errorData){ }
                                }
                            }
                        }
                    }
                } else {
                    mainActivity.containerApplication.loadingSaved(false)
                }
            }
            Constants.APPEND_CODE -> {
                val pkcs7 = Base64.encodeToString(data?.getByteArrayExtra(Constants.EXTRA_RESULT_PKCS7), Base64.NO_WRAP)
                val signature = mainActivity.horcrux.toHexString(data?.getByteArrayExtra(Constants.EXTRA_RESULT_SIGNATURE))
                val serialNumber = data?.getCharSequenceExtra(Constants.EXTRA_RESULT_SERIAL_NUMBER)
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
                                mainActivity.containerApplication.dialogData(AppConstant.ERROR_STATUS_UPDATE_ACT,errorData){ }
                            }
                        }
                    }
                }
            }
            Constants.ATTACH_CODE ->{
                val pkcs7 = Base64.encodeToString(data!!.getByteArrayExtra(Constants.EXTRA_RESULT_PKCS7), Base64.NO_WRAP)
                val saveSignAct = SaveSignAct(docId.toString(), pkcs7)
                actViewModel.saveSignAct(getLanguage(requireContext()),saveSignAct)
                lifecycleScope.launchWhenCreated {
                    actViewModel.saveSignAct.collect { result->
                        when(result){
                            is ResponseState.Loading->{
                                mainActivity.containerApplication.loadingSaved(true)
                            }
                            is ResponseState.Success->{
                                mainActivity.containerApplication.loadingSaved(false)
                                mainActivity.containerApplication.dialogStatus(
                                    AppConstant.CHECK_STATUS,getString(R.string.check_sign)){
                                    init()
                                }
                            }
                            is ResponseState.Error->{
                                mainActivity.containerApplication.loadingSaved(false)
                                val errorData = JsonParser.parseString(result.exception.localizedMessage).asJsonObject
                                mainActivity.containerApplication.dialogData(AppConstant.ERROR_STATUS_UPDATE_ACT,errorData){ }
                            }
                        }
                    }
                }
            }

        }
    }


    override fun inflateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentHomeBinding.inflate(inflater,container,false)
}