package uz.einvoice.android.presentation.controllers.actController

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.razir.progressbutton.attachTextChangeAnimator
import com.github.razir.progressbutton.bindProgressButton
import com.github.razir.progressbutton.hideProgress
import com.github.razir.progressbutton.showProgress
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import uz.einvoice.android.R
import uz.einvoice.android.adapters.genericPagingAdapter.GenericPagingAdapter
import uz.einvoice.android.adapters.genericRvAdapter.GenericRvAdapter
import uz.einvoice.android.adapters.loadState.ExampleLoadStateAdapter
import uz.einvoice.android.databinding.AddProductActBinding
import uz.einvoice.android.databinding.FragmentHomeBinding
import uz.einvoice.android.presentation.activities.MainActivity
import uz.einvoice.android.presentation.screens.homeScreen.HomeFragment
import uz.einvoice.android.utils.appConstant.AppConstant
import uz.einvoice.android.utils.appConstant.AppConstant.BUYER_TYPE
import uz.einvoice.android.utils.appConstant.AppConstant.CLICK_TYPE_SIGNED
import uz.einvoice.android.utils.appConstant.AppConstant.DEFAULT_CLICK_TYPE
import uz.einvoice.android.utils.appConstant.AppConstant.DELETE_CLICK
import uz.einvoice.android.utils.appConstant.AppConstant.EDITE_CLICK
import uz.einvoice.android.utils.appConstant.AppConstant.RECEIVE
import uz.einvoice.android.utils.appConstant.AppConstant.SELLER_TYPE
import uz.einvoice.android.utils.extension.*
import uz.einvoice.android.vm.actVm.ActViewModel
import uz.einvoice.domain.database.actProductEntity.ActProductEntity
import uz.einvoice.domain.database.measure.MeasureEntity
import uz.einvoice.domain.models.act.actDocument.actDocumentData.ActDocumentData
import uz.einvoice.domain.models.act.actDocument.actDocumentFilter.ActDocumentFilter
import uz.einvoice.domain.models.branchModel.BranchModel
import uz.einvoice.domain.models.branchModel.Data
import uz.einvoice.domain.models.companyInfo.CompanyInfo
import uz.einvoice.domain.models.createActModel.*
import uz.einvoice.domain.models.createActModel.resSaceAct.ResponseSaveAct
import uz.einvoice.domain.models.tasNifProduct.TasnifProduct
import uz.einvoice.domain.models.tinOrPinfl.pinfl.PinflModel
import uz.einvoice.domain.models.tinOrPinfl.tin456.Physical
import uz.einvoice.domain.models.tinOrPinfl.tinJuridic.LegalModel
import uz.einvoice.domain.models.uniqId.UniqId
import uz.einvoice.domain.usesCase.apiUsesCase.parseClass
import uz.einvoice.domain.utils.loadState.ResponseState
import uz.sicnt.horcrux.Constants
import java.math.BigDecimal
import java.util.*


class ActUiController<T>(
    private val mainActivity: MainActivity,
    private val binding:FragmentHomeBinding,
    private val actViewModel: ActViewModel,
    private val homeFragment: HomeFragment,
    private val onClick:(data:T,position:Int,typeDoc:String)->Unit
):ActController{
    val sendCount = 0
    // save act data
    var branchDataMainSeller:Data?=null
    var branchDataMainBuyer:Data?=null
    var buyerName:String?=null
    var companyInfoMain:CompanyInfo?=null
    var tasnifProduct:uz.einvoice.domain.models.tasNifProduct.Data?=null
    var orderNumber:Long = 0
    var measureData:MeasureEntity?=null


    // save act data
    private var actDate:String?=null
    private var actDateContract:String?=null
    override fun createAct() {
        // date picker
        binding.includeActCreate.apply {
            consActCreate.visible()
            // act date
            tvActDate.setOnClickListener {
               datePicker(tvActDate,0)
            }

            // dagavor date
            tvContractDate.setOnClickListener {
                datePicker(tvContractDate,1)
            }



            // seller data
            val userData = actViewModel.getUserData()
            actViewModel.getUserCompany(getLanguage(mainActivity), userData?.data?.tin.toString())
            homeFragment.lifecycleScope.launchWhenCreated {
                actViewModel.companyInfo.collect { result->
                    when(result){
                        is ResponseState.Loading->{
                            progressSellerName.visible()
                            progressSellerTin.visible()
                        }
                        is ResponseState.Success->{
                            swipeRefresh.isRefreshing = false
                            progressSellerName.gone()
                            progressSellerTin.gone()
                            if(result.data.isNotEmpty() && result.data[0]!=null){
                                var companyInfo = result.data[0]?.parseClass(CompanyInfo::class.java)
                                editSellerTin.setText(companyInfo?.data?.tin)
                                editSellerName.setText(companyInfo?.data?.shortName.toString())
                                companyInfoMain = companyInfo
                            }
                            if (result.data.isNotEmpty()){
                                val branchModel = result.data[1]?.parseClass(BranchModel::class.java)
                                if (branchModel?.data?.isNotEmpty() == true){
                                    val listBranch = LinkedList<Data>()
                                    if (userData?.data?.branch?.branchNum!=null){
                                        branchModel.data.onEach { data ->
                                            if (userData.data.branch.branchNum == data.branchNum){
                                                listBranch.add(data)
                                            }
                                        }
                                        if (listBranch.isNotEmpty()){
                                            layoutSellerBranches.visible()
                                            listSellerBranches.setOnClickListener {
                                                if (listBranch.isNotEmpty() && listBranch.size > 1){
                                                    mainActivity.containerApplication.applicationDialog(0, branchModel.data){ data ->
                                                        branchDataMainSeller =  data
                                                        listSellerBranches.text = data.branchName
                                                        cancelBranchSeller.visible()
                                                        descriptionText()
                                                        cancelBranchSeller.setOnClickListener {
                                                            listSellerBranches.text = mainActivity.getString(R.string.choose)
                                                            branchDataMainSeller = null
                                                            descriptionText()
                                                            cancelBranchSeller.gone()
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    } else if (branchModel.data.isNotEmpty()) {
                                        layoutSellerBranches.visible()
                                        listSellerBranches.setOnClickListener {
                                            if (branchModel.data.isNotEmpty() && branchModel.data.size > 1){
                                                mainActivity.containerApplication.applicationDialog(0, branchModel.data){ data ->
                                                    branchDataMainSeller =  data
                                                    listSellerBranches.text = data.branchName
                                                    cancelBranchSeller.visible()
                                                    descriptionText()
                                                    cancelBranchSeller.setOnClickListener {
                                                        listSellerBranches.text = mainActivity.getString(R.string.choose)
                                                        branchDataMainSeller = null
                                                        descriptionText()
                                                        cancelBranchSeller.gone()
                                                    }
                                                }
                                            }
                                        }
                                    }

                                }
                            }
                        }
                        is ResponseState.Error->{
                            progressSellerName.gone()
                            progressSellerTin.gone()
                        }
                    }
                }
            }


            editBuyerTinOrPinfl.addTextChangedListener(object:TextWatcher{
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                }
                var timer = Timer()
                val DELAY: Long = 1000 // Milliseconds
                override fun afterTextChanged(tinOrPinfl: Editable?) {
                    if (tinOrPinfl.toString().isNotEmptyOrNull()){
                        timer.cancel()
                        timer = Timer()
                        timer.schedule(object:TimerTask(){
                            override fun run() {
                                if(tinOrPinfl.toString().trim().isNotEmptyOrNull()){
                                    if (editBuyerTinOrPinfl.text.toString().trim().length == 9 || editBuyerTinOrPinfl.text.toString().trim().length == 14) {
                                        actViewModel.buyerData(getLanguage(mainActivity), tinOrPinfl.toString().trim())
                                        buyerData(tinOrPinfl.toString())
                                    } else {
                                        editActText.text.clear()
                                        editBuyerName.text.clear()
                                    }
                                }
                            }
                        },DELAY)
                    } else {
                        editActText.text.clear()
                        editBuyerName.text.clear()
                    }

                }

            })
            // product add button
            btnAddProduct.setOnClickListener {
                addProduct(false,null)
            }

            // product rv
            val productAdapter:GenericRvAdapter<ActProductEntity> by lazy {
                GenericRvAdapter(R.layout.recycler_act_product_item){ data, position, clickType ->
                    when(clickType){
                        EDITE_CLICK->{
                            addProduct(true,data)
                        }
                        DELETE_CLICK->{
                            actViewModel.deleteActProduct(data)
                        }
                    }
                }
            }
           homeFragment.lifecycleScope.launchWhenCreated {
               actViewModel.getAllActProduct().collect { listActProduct->
                   productAdapter.submitList(listActProduct)
                   binding.includeActCreate.recyclerViewProducts.adapter = productAdapter
               }
           }

            swipeRefresh.setOnRefreshListener {
                createAct()
                homeFragment.lifecycleScope.launchWhenCreated {
                    mainActivity.containerViewModel.actFilter.emit(null)
                }
            }
            // save btn
            btnSave.setOnClickListener {
               getUniqueId()
            }
        }
    }



    // save act
    private fun saveActData(uniqueId:String,uniqueIdProduct:String){
         binding.includeActCreate.apply {
            val actNumber = editActno.text.toString().trim()
            val actDate = tvActDate.text.toString().trim()
            val actText = editActText.text.toString().trim()

            var actDoc:Actdoc?=null
            var contractDoc:Contractdoc?=null

            if (!actNumber.isNotEmptyOrNull()){ mainActivity.motionAnimation("error",mainActivity.getString(R.string.no_act_number)) } else
            if (!actDate.isNotEmptyOrNull()){ mainActivity.motionAnimation("error",mainActivity.getString(R.string.no_act_data)) } else
            if (!actText.isNotEmptyOrNull()){ mainActivity.motionAnimation("error",mainActivity.getString(R.string.no_act_description)) } else {
                actDoc  = Actdoc(this@ActUiController.actDate.toString(),actNumber,actText)
            }
            val contractNumber = editContractno.text.toString().trim()
            val contractDate = tvContractDate.text.toString().trim()
            if (!contractNumber.isNotEmptyOrNull()){
                mainActivity.motionAnimation("error",mainActivity.getString(R.string.no_contract_number))
            } else if (!contractDate.isNotEmptyOrNull()){
                mainActivity.motionAnimation("error",mainActivity.getString(R.string.no_contract_date))
            } else {
                contractDoc = Contractdoc(this@ActUiController.actDateContract.toString(),contractNumber)
            }

            if (actDoc!=null && contractDoc!=null && companyInfoMain!=null){
                // seller data act
                val sellerTin = companyInfoMain?.data?.tin
                val sellerName = companyInfoMain?.data?.shortName
                val sellerBranchCode = branchDataMainSeller?.branchNum
                val sellerBranchName = branchDataMainSeller?.branchName
                // buyer data Act
                var buyerNameData:String?=null
                if (! binding.includeActCreate.editBuyerName.text.toString().isNotEmptyOrNull()) {
                    mainActivity.motionAnimation("error",mainActivity.getString(R.string.no_buyer_name))
                } else {
                   buyerNameData =  binding.includeActCreate.editBuyerName.text.toString()
                }
                var buyerTinOrPinfl:String?=null
                if (! binding.includeActCreate.editBuyerTinOrPinfl.text.toString().isNotEmptyOrNull()) {
                    mainActivity.motionAnimation("error",mainActivity.getString(R.string.no_buyer_tin_or_pinfl))
                }else{
                    buyerTinOrPinfl =  binding.includeActCreate.editBuyerTinOrPinfl.text.toString()
                }
                val buyerBranchCode = branchDataMainBuyer?.branchNum
                val buyerBranchName = branchDataMainBuyer?.branchName

                // productList
                val sellerTinProduct = sellerTin

                homeFragment.lifecycleScope.launchWhenResumed {
                    actViewModel.getAllActProduct().collect{ listProduct->
                        val listProducts = LinkedList<Product>()
                        listProduct.onEach { actProductEntity->
                            listProducts.add(Product(actProductEntity.catalogcode,
                                actProductEntity.catalogname, actProductEntity.count,
                                actProductEntity.measureid,actProductEntity.name,
                                actProductEntity.ordno,actProductEntity.summa,
                                BigDecimal(actProductEntity.totalSumma)
                                ))
                        }
                        val productList = Productlist(uniqueIdProduct,listProducts,sellerTinProduct.toString())
                        // big class act save
                        val createActModel = CreateActModel(actDoc, uniqueId,buyerBranchCode.toString(),
                            buyerBranchName.toString(),buyerNameData.toString(),
                            buyerTinOrPinfl.toString(),contractDoc, productList,sellerBranchCode.toString(),
                            sellerBranchName.toString(),sellerName.toString(),sellerTin.toString())

                        actViewModel.saveAct(getLanguage(mainActivity),createActModel)

                        actViewModel.saveAct.collect { result->
                            when(result){
                                is ResponseState.Loading->{
                                     binding.includeActCreate.btnSave.enabledFalse()
                                    mainActivity.loadingSave(true)
                                }
                                is ResponseState.Success->{
                                    loadingButton(false)
                                    mainActivity.loadingSave(false)
                                     binding.includeActCreate.btnSave.enabled()
                                    actViewModel.deleteTableActProduct()
                                    clearUiActCreate(result.data)
                                }
                                is ResponseState.Error->{
                                    actViewModel.deleteTableActProduct()
                                    loadingButton(false)
                                    mainActivity.loadingSave(false)
                                     binding.includeActCreate.btnSave.enabled()
                                    if (result.exception.localizedMessage.isNotEmptyOrNull()){
                                        val errorAuth = JsonParser.parseString(result.exception.localizedMessage).asJsonObject
                                        mainActivity.containerApplication.dialogData(AppConstant.MENU_ERROR,errorAuth){ clickType->
                                            if(clickType==1) saveActData(uniqueId,uniqueIdProduct)
                                        }
                                    } else {
                                        mainActivity.containerApplication.dialogData(AppConstant.NO_INTERNET,null){ clickType ->
                                            if(clickType==1) saveActData(uniqueId,uniqueIdProduct)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    private fun clearUiActCreate(jsonElement: JsonElement?){
         binding.includeActCreate.apply {
            editActno.text.clear()
            tvActDate.text = ""
            editContractno.text.clear()
            tvContractDate.text = ""
            editBuyerTinOrPinfl.text.clear()
            editBuyerName.clear()
            editActText.text.clear()
            val responseSaveAct = jsonElement?.parseClass(ResponseSaveAct::class.java)
             logData(responseSaveAct.toString())
             mainActivity.containerApplication.screenNavigate.createDocument(responseSaveAct?.data?.actid.toString(),1,SELLER_TYPE,null,null)
        }
    }
    @SuppressLint("SetTextI18n")
    private fun descriptionText(){
        if(companyInfoMain?.data?.shortName.isNotEmptyOrNull() && buyerName.isNotEmptyOrNull()){
             binding.includeActCreate.editActText.setText("${mainActivity.getText(R.string.act_description1)} ${companyInfoMain?.data?.shortName} ${if (branchDataMainSeller!=null) "-"+branchDataMainSeller?.branchName else ""}${mainActivity.getText(R.string.act_description2)} $buyerName  ${if (branchDataMainBuyer != null) "-"+ branchDataMainBuyer?.branchName else ""}, ${mainActivity.getText(R.string.act_description3)}")
        } else {
             binding.includeActCreate.editActText.text.clear()
        }
    }
    private fun buyerData(tinOrPinfl:String?){
        homeFragment.lifecycleScope.launchWhenCreated {
            actViewModel.buyerData.collect { result->
                when(result){
                    is ResponseState.Loading->{
                         binding.includeActCreate.progressBuyerName.visible()
                         binding.includeActCreate.progressBuyerTin.visible()
                    }
                    is ResponseState.Success->{
                         binding.includeActCreate.progressBuyerName.gone()
                         binding.includeActCreate.progressBuyerTin.gone()

                        if (tinOrPinfl.toString().trim().length == 9){
                            val juridicOrPhysical = tinOrPinfl.toString().getJuridicOrPhysical()

                            if (juridicOrPhysical){
                                // agar 4,5,6 bulsa Jismoniy Physical
                                val physical = result.data[0]?.parseClass(Physical::class.java)
                                 binding.includeActCreate.editBuyerName.setText(physical?.data?.fullName)
                                if (physical?.data?.personalNum.isNotEmptyOrNull()){
                                     binding.includeActCreate.editBuyerTinOrPinfl.setText(physical?.data?.personalNum)
                                }
                                buyerName = physical?.data?.fullName
                                descriptionText()
                            } else {
                                // agar text 4,5,6 dan boshqalarida bulsa yuridic legal
                                val legal = result.data[0]?.parseClass(LegalModel::class.java)
                                 binding.includeActCreate.editBuyerName.setText(legal?.data?.shortName)
                                buyerName = legal?.data?.shortName
                                descriptionText()
                            }
                        } else if (tinOrPinfl.toString().trim().length == 14){
                            // agar pinfl bulsa
                            val pinflModel = result.data[0]?.parseClass(PinflModel::class.java)
                             binding.includeActCreate.editBuyerName.setText(pinflModel?.data?.fullName)
                            buyerName = pinflModel?.data?.fullName
                            descriptionText()
                        }

                        val branches = result.data[1]
                        if (branches!=null){
                            val branchModel = branches.parseClass(BranchModel::class.java)
                            if(branchModel.data.isNotEmpty()){
                                 binding.includeActCreate.layoutBuyerBranches.visible()
                                 binding.includeActCreate.listBuyerBranches.setOnClickListener {
                                    mainActivity.containerApplication.applicationDialog(0, branchModel.data){ data ->
                                        branchDataMainBuyer =  data
                                         binding.includeActCreate.listBuyerBranches.text = data.branchName
                                         binding.includeActCreate.cancelBuyerBranch.visible()
                                        descriptionText()
                                         binding.includeActCreate.cancelBuyerBranch.setOnClickListener {
                                             binding.includeActCreate.cancelBuyerBranch.gone()
                                             binding.includeActCreate.listBuyerBranches.text = mainActivity.getString(R.string.choose)
                                            branchDataMainBuyer =  null
                                            descriptionText()
                                        }
                                    }
                                }
                            }
                        }
                    }


                    is ResponseState.Error->{
                         binding.includeActCreate.progressBuyerName.gone()
                         binding.includeActCreate.progressBuyerTin.gone()
                    }
                }
            }
        }
    }
    @SuppressLint("SetTextI18n")
    private fun addProduct(isUpdate:Boolean,actProductEntityUpdate:ActProductEntity?){
        val bottomSheetDialog = BottomSheetDialog(mainActivity)
        val addProductBinding = AddProductActBinding.inflate(mainActivity.layoutInflater)
        bottomSheetDialog.setOnShowListener {
          val dialog = it as BottomSheetDialog
            val bottomSheet = dialog.findViewById<View>(R.id.nested_product)
            bottomSheet.let { sheet->
                dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
                sheet?.parent?.parent?.requestLayout()
            }
        }
        addProductBinding.apply {
            actViewModel.productData(getLanguage(mainActivity))
            homeFragment.lifecycleScope.launchWhenCreated {
                actViewModel.productData.collect { result->
                    when(result){
                        is ResponseState.Loading->{
                            addProductBinding.shimmer.visible()
                            addProductBinding.addProductView.gone()
                        }
                        is ResponseState.Success->{
                            addProductBinding.shimmer.gone()
                            addProductBinding.addProductView.visible()
                            val tasnifProduct = result.data?.parseClass(TasnifProduct::class.java)
                            if (tasnifProduct?.data?.isNotEmpty() == true){
                                addProductBinding.listCatalogCode.text = "${if (isUpdate) actProductEntityUpdate?.catalogcode else mainActivity.getString(R.string.checked_text)} ${if (isUpdate) actProductEntityUpdate?.catalogname else ""}"
                                this@ActUiController.tasnifProduct = null
                                if (tasnifProduct.data.size > 1){
                                    if (isUpdate){
                                        tasnifProduct.data.map {  data ->
                                            if (data.mxikCode == actProductEntityUpdate?.catalogcode && data.mxikFullName == actProductEntityUpdate.catalogname){
                                                this@ActUiController.tasnifProduct = data
                                            }
                                        }
                                    }

                                    addProductBinding.listCatalogCode.setOnClickListener {
                                        mainActivity.containerApplication.applicationDialog(1, tasnifProduct.data){ data ->
                                            addProductBinding.listCatalogCode.text = "${data.mxikCode} ${data.mxikFullName}"
                                            this@ActUiController.tasnifProduct = data
                                        }
                                    }
                                }
                            } else {
                                addProductBinding.listCatalogCode.text = mainActivity.getString(R.string.no_data)
                            }
                            if (actViewModel.getMeasure().isNotEmpty()){
                                if (isUpdate){
                                    actViewModel.getMeasure().map { measureEntity ->
                                        if (measureEntity.measureId == actProductEntityUpdate?.measureid){
                                            addProductBinding.listMeasureId.text = measureEntity.name
                                            this@ActUiController.measureData = measureEntity
                                        }
                                    }
                                }else {
                                    addProductBinding.listMeasureId.text = actViewModel.getMeasure()[0].name
                                    this@ActUiController.measureData = actViewModel.getMeasure()[0]
                                }
                            }



                            addProductBinding.listMeasureId.setOnClickListener {
                                mainActivity.containerApplication.applicationDialog(2,  actViewModel.getMeasure()){ data ->
                                    addProductBinding.listMeasureId.text = data.name
                                    this@ActUiController.measureData = data
                                }
                            }
                            if (isUpdate) {
                                addProductBinding.editCount.setText(actProductEntityUpdate?.count.toString())
                                addProductBinding.editName.setText(actProductEntityUpdate?.name.toString())
                                addProductBinding.editSumma.setText(actProductEntityUpdate?.summa.toString())
                                addProductBinding.editTotalSum.setText(actProductEntityUpdate?.totalSumma.toString())
                                addProductBinding.btnSaveProduct.text = mainActivity.getString(R.string.close)
                                addProductBinding.btnAdded.text = mainActivity.getString(R.string.update)
                            } else {
                                addProductBinding.btnSaveProduct.text = mainActivity.getString(R.string.close)
                                addProductBinding.btnAdded.text = mainActivity.getString(R.string.added)
                            }
                            addProductBinding.editCount.doAfterTextChanged { countApp->
                                val priceOne = addProductBinding.editSumma.text.toString()
                                if (countApp.toString().isNotEmptyOrNull() && priceOne.isNotEmptyOrNull()){
                                    val price = addProductBinding.editSumma.text.toString()
                                    val priceAll = formatterApp(formatterApp(price).multiply(BigDecimal(countApp.toString())).toPlainString())
                                    addProductBinding.editTotalSum.setText(priceAll.toPlainString())
                                }
                            }

                            addProductBinding.editSumma.filters = arrayOf<InputFilter>(DecimalDigitsInputFilter(30, 2))

                            addProductBinding.editSumma.addTextChangedListener(object:TextWatcher{
                                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                                var timer = Timer()
                                val DELAY: Long = 1500 // Milliseconds
                                override fun afterTextChanged(priceApp: Editable?) {
                                    if (priceApp.toString().isNotEmptyOrNull()){
                                        timer.cancel()
                                        timer = Timer()
                                        timer.schedule(object:TimerTask(){
                                            override fun run() {
                                                if(priceApp.toString().trim().isNotEmptyOrNull()){
                                                    if (addProductBinding.editSumma.text.toString().trim().length > 1) {
                                                        val countApp = addProductBinding.editCount.getNumericValue()
                                                        if (priceApp.toString().isNotEmptyOrNull() && countApp.toString().isNotEmptyOrNull()){
                                                            val price = addProductBinding.editSumma.text.toString()
                                                            val priceAll = formatterApp(formatterApp(price).multiply(BigDecimal(countApp)).toPlainString())
                                                            homeFragment.lifecycleScope.launchWhenCreated {
                                                                addProductBinding.editTotalSum.setText(priceAll.toPlainString())
                                                            }
                                                        }
                                                    } else {
                                                        addProductBinding.editSumma.text.clear()
                                                        addProductBinding.editSumma.text.clear()
                                                    }
                                                }
                                            }
                                        },DELAY)
                                    } else {
                                        addProductBinding.editSumma.text.clear()
                                        addProductBinding.editSumma.text.clear()
                                    }
                                }
                            })


                            homeFragment.lifecycleScope.launchWhenCreated {
                                actViewModel.getAllActProduct().collect{ listActProduct->
                                    if (listActProduct.isNotEmpty()){
                                        addProductBinding.textCount.visible()
                                        orderNumber = listActProduct.size.toLong()
                                    } else {
                                        orderNumber = 1
                                        addProductBinding.textCount.gone()
                                    }
                                    addProductBinding.textCount.text = if (listActProduct.size>100) "99+" else listActProduct.size.toString()
                                }
                            }

                            // save product
                            addProductBinding.btnAdded.setOnClickListener {
                                val name = addProductBinding.editName.text.toString()
                                val count = addProductBinding.editCount.text.toString()
                                val summa = addProductBinding.editSumma.text.toString()
                                val totalSumma = addProductBinding.editTotalSum.getNumericValue().toString()
                                if (!name.isNotEmptyOrNull()){
                                    mainActivity.motionAnimation("error",mainActivity.getString(R.string.no_act_product_name))
                                } else if (!count.isNotEmptyOrNull() || count.toDouble()==0.0){
                                    mainActivity.motionAnimation("error",mainActivity.getString(R.string.no_act_product_count))
                                } else if (!summa.isNotEmptyOrNull()){
                                    mainActivity.motionAnimation("error",mainActivity.getString(R.string.no_act_product_summa))
                                } else if (!totalSumma.isNotEmptyOrNull()){
                                    mainActivity.motionAnimation("error",mainActivity.getString(R.string.no_act_product_total_summa))
                                } else {
                                    if (orderNumber != 0L && measureData!=null) {
                                        val orderNumberProduct = orderNumber
                                        val catalogCode = this@ActUiController.tasnifProduct?.mxikCode
                                        val catalogName = this@ActUiController.tasnifProduct?.mxikFullName
                                        val measureId = measureData?.measureId
                                        if (isUpdate){
                                            actProductEntityUpdate?.catalogcode = catalogCode.toString()
                                            actProductEntityUpdate?.catalogname = catalogName.toString()
                                            actProductEntityUpdate?.name = name
                                            actProductEntityUpdate?.measureid = measureId?:0
                                            actProductEntityUpdate?.count = count
                                            actProductEntityUpdate?.summa = summa
                                            actProductEntityUpdate?.totalSumma = totalSumma
                                            actViewModel.updateActProduct(actProductEntityUpdate!!)
                                            mainActivity.motionAnimation("success",mainActivity.getString(R.string.success_update_product))
                                            bottomSheetDialog.dismiss()
                                        } else {
                                            val actProductEntity = ActProductEntity(ordno = orderNumberProduct.toInt(), catalogcode = catalogCode.toString(),
                                                catalogname = catalogName.toString(), name = name, measureid = measureId?:0, count = count, summa = summa, totalSumma = totalSumma)
                                            actViewModel.saveActProduct(actProductEntity)
                                            mainActivity.motionAnimation("success",mainActivity.getString(R.string.success_add_product))
                                            addProductBinding.editName.text.clear()
                                            addProductBinding.editCount.text?.clear()
                                            addProductBinding.editSumma.text.clear()
                                            addProductBinding.editTotalSum.text?.clear()
                                        }
                                    }
                                }
                            }

                        }
                        is ResponseState.Error->{
                            addProductBinding.shimmer.gone()
                            addProductBinding.addProductView.visible()
                        }
                    }
                }
            }
            this.btnSaveProduct.setOnClickListener {
                bottomSheetDialog.dismiss()
            }
        }
        bottomSheetDialog.setContentView(addProductBinding.root)
        bottomSheetDialog.show()
    }


    private fun datePicker(textView:TextView,type:Int){
        mainActivity.containerApplication.datePicker { time,timeFormat->
            textView.text = time
            when(type) {
                0 -> {
                    this.actDate = timeFormat
                }
                1 -> {
                    this.actDateContract = timeFormat
                }
            }
        }
    }

    private fun getUniqueId(){
        actViewModel.getUniqueId(getLanguage(mainActivity))
        homeFragment.lifecycleScope.launchWhenCreated {
            actViewModel.uniqueId.collect { result->
                when(result){
                    is ResponseState.Loading->{
                        loadingButton(true)
                    }
                    is ResponseState.Success->{
                        loadingButton(false)
                        val uniqueId1 = result.data[0]?.parseClass(UniqId::class.java)
                        val uniqueId2 = result.data[1]?.parseClass(UniqId::class.java)
                        saveActData(uniqueId1?.data?.id.toString(),uniqueId2?.data?.id.toString())
                    }
                    is ResponseState.Error->{
                        loadingButton(false)
                        if (result.exception.localizedMessage.isNotEmptyOrNull()){
                            val errorAuth = JsonParser.parseString(result.exception.localizedMessage).asJsonObject
                            mainActivity.containerApplication.dialogData(AppConstant.MENU_ERROR,errorAuth){ clickType->
                                if(clickType==1) getUniqueId()
                            }
                        } else {
                            mainActivity.containerApplication.dialogData(AppConstant.NO_INTERNET,null){ clickType ->
                                if(clickType==1) getUniqueId()
                            }
                        }
                    }
                }
            }
        }
    }




    private lateinit var genericPagingAdapterActDocument:GenericPagingAdapter<ActDocumentData>
    override fun documentViews(type: String) {
        genericPagingAdapterActDocument = GenericPagingAdapter(R.layout.item_document,type){ data, position, clickType, viewBinding,typeDoc:String ->
            logData(clickType.toString())
            when(clickType){
                DEFAULT_CLICK_TYPE->{
                    mainActivity.containerApplication.screenNavigate.createDocument(data?._id.toString(),1,BUYER_TYPE,data?.stateid,RECEIVE)
                }
                CLICK_TYPE_SIGNED->{
                    if (mainActivity.horcrux.isEImzoInstalled()){
                        onClick.invoke(data as T,position,typeDoc)
                    } else {
                        noSign()
                    }
                }
            }
        }

        binding.includeActDocument.apply {
            documentLoadState()
            val layoutManager = LinearLayoutManager(mainActivity)
            rvDocuments.layoutManager = layoutManager
            rvDocuments.addOnScrollListener(object: RecyclerView.OnScrollListener(){
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (genericPagingAdapterActDocument.itemCount>=4){
                        if (layoutManager.findLastCompletelyVisibleItemPosition() == genericPagingAdapterActDocument.itemCount-1){
                            mainActivity.bottomBarView(false)
                        }
                    }
                }

                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (dy < 0) {
                        mainActivity.bottomBarView(true)
                    }
                }
            })
            var actDocumentFilter = ActDocumentFilter()
            homeFragment.lifecycleScope.launchWhenCreated {
                mainActivity.containerViewModel.actFilter.collect {
                    actDocumentFilter = ActDocumentFilter(it?.actdate_end,it?.actdate_start,it?.actno,it?.buyer_branchcode,it?.buyertin,it?.contractdate_end,
                        it?.contractdate_start,it?.contractno,it?.limit,it?.page,
                        it?.seller_branchcode,it?.sellertin,it?.stateid)
                }
            }
            actDocumentPaging(actDocumentFilter,type)

            swipeRefresh.setOnRefreshListener {
                actDocumentFilter = ActDocumentFilter()
                actDocumentPaging(actDocumentFilter,type)
                homeFragment.lifecycleScope.launchWhenCreated {
                    mainActivity.containerViewModel.actFilter.emit(null)
                }
            }
            rvDocuments.adapter = genericPagingAdapterActDocument
            swipeRefresh.setColorSchemeColors(ContextCompat.getColor(mainActivity,R.color.primary_color))
        }
    }
    private fun noSign(){
        mainActivity.containerApplication.dialogStatus(0,mainActivity.getString(R.string.no_eimzo)){ clickType->
            if (clickType==1) {
                mainActivity.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=${Constants.E_IMZO_APP}")))
            }
        }
    }

    private fun actDocumentPaging(actDocumentFilter: ActDocumentFilter,path:String){
        binding.includeActDocument.apply {
            homeFragment.lifecycleScope.launchWhenCreated {
                actViewModel.actDocuments(lang = getLanguage(mainActivity),actDocumentFilter,path).collect { pagingData->
                    genericPagingAdapterActDocument.submitData(pagingData)
                    swipeRefresh.isRefreshing = false
                }
            }
        }
    }

    private fun documentLoadState(){
        genericPagingAdapterActDocument
            .withLoadStateHeaderAndFooter(
                header = ExampleLoadStateAdapter(R.layout.load_state_draft,genericPagingAdapterActDocument::retry),
                footer = ExampleLoadStateAdapter(R.layout.load_state_draft,genericPagingAdapterActDocument::retry)
            )
        homeFragment.lifecycleScope.launch {
            genericPagingAdapterActDocument.loadStateFlow.collectLatest {  loadStates ->
                binding.includeActDocument.shimmerInclude.shimmer.isVisible = loadStates.refresh is LoadState.Loading
                binding.includeActDocument.rvDocuments.isVisible = loadStates.refresh !is LoadState.Loading
            }
        }
    }

    private fun loadingButton(isLoading:Boolean){
         binding.includeActCreate.apply {
            if (isLoading){
                homeFragment.bindProgressButton(btnSave)
                // (Optional) Enable fade In / Fade out animations
                btnSave.attachTextChangeAnimator()
                // Show progress with "Loading" text
                btnSave.showProgress {
                    buttonTextRes = R.string.loading_text
                    progressColor = Color.WHITE
                }
            }else{
                // Hide progress and show "Submit" text instead
                btnSave.hideProgress(R.string.save)
            }
        }
    }

}

