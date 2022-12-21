package uz.idea.newinvoiceapplication.presentation.controllers.actController

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.razir.progressbutton.attachTextChangeAnimator
import com.github.razir.progressbutton.bindProgressButton
import com.github.razir.progressbutton.hideProgress
import com.github.razir.progressbutton.showProgress
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import uz.idea.domain.database.actProductEntity.ActProductEntity
import uz.idea.domain.database.measure.MeasureEntity
import uz.idea.domain.models.act.actDraftModel.actDraftFilter.ActDraftFilter
import uz.idea.domain.models.act.actIncoming.incomingFilterModule.IncomingFilterModel
import uz.idea.domain.models.act.actSend.actSendFilter.ActSendFilter
import uz.idea.domain.models.branchModel.BranchModel
import uz.idea.domain.models.branchModel.Data
import uz.idea.domain.models.companyInfo.CompanyInfo
import uz.idea.domain.models.createActModel.*
import uz.idea.domain.models.createActModel.resSaceAct.ResponseSaveAct
import uz.idea.domain.models.tasNifProduct.TasnifProduct
import uz.idea.domain.models.tinOrPinfl.pinfl.PinflModel
import uz.idea.domain.models.tinOrPinfl.tin456.Physical
import uz.idea.domain.models.tinOrPinfl.tinJuridic.LegalModel
import uz.idea.domain.models.uniqId.UniqId
import uz.idea.domain.usesCase.apiUsesCase.parseClass
import uz.idea.domain.utils.loadState.ResponseState
import uz.idea.newinvoiceapplication.R
import uz.idea.newinvoiceapplication.adapters.genericPagingAdapter.GenericPagingAdapter
import uz.idea.newinvoiceapplication.adapters.genericRvAdapter.GenericRvAdapter
import uz.idea.newinvoiceapplication.adapters.loadState.ExampleLoadStateAdapter
import uz.idea.newinvoiceapplication.databinding.AddProductActBinding
import uz.idea.newinvoiceapplication.databinding.FragmentHomeBinding
import uz.idea.newinvoiceapplication.presentation.activities.MainActivity
import uz.idea.newinvoiceapplication.presentation.screens.homeScreen.HomeFragment
import uz.idea.newinvoiceapplication.utils.appConstant.AppConstant
import uz.idea.newinvoiceapplication.utils.appConstant.AppConstant.DELETE_CLICK
import uz.idea.newinvoiceapplication.utils.appConstant.AppConstant.EDITE_CLICK
import uz.idea.newinvoiceapplication.utils.extension.*
import uz.idea.newinvoiceapplication.vm.actVm.ActViewModel
import java.math.BigDecimal
import java.util.*

class ActUiController(
    private val mainActivity: MainActivity,
    private val binding:FragmentHomeBinding,
    private val actViewModel: ActViewModel,
    private val homeFragment: HomeFragment
):ActController{

    // save act data
    var branchDataMainSeller:Data?=null
    var branchDataMainBuyer:Data?=null
    var buyerName:String?=null
    var companyInfoMain:CompanyInfo?=null
    var tasnifProduct:uz.idea.domain.models.tasNifProduct.Data?=null
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

            // buyer data
            editBuyerTinOrPinfl.doAfterTextChanged { tinOrPinfl->
                if(tinOrPinfl.toString().trim().isNotEmptyOrNull()){
                    if (editBuyerTinOrPinfl.text.toString().trim().length == 9) {
                       Handler(Looper.getMainLooper()).postDelayed({
                           if(editBuyerTinOrPinfl.text.toString().trim().length == 9) {
                               actViewModel.buyerData(
                                   getLanguage(mainActivity),
                                   tinOrPinfl.toString().trim()
                               )
                               buyerData(tinOrPinfl.toString())
                           }
                       },1000)
                    } else if (editBuyerTinOrPinfl.text.toString().trim().length == 14){
                        actViewModel.buyerData(
                            getLanguage(mainActivity),
                            tinOrPinfl.toString().trim()
                        )
                        buyerData(tinOrPinfl.toString())
                    }
                }
            }

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

                homeFragment.lifecycleScope.launchWhenCreated {
                    actViewModel.getAllActProduct().collect{ listProduct->
                        val listProducts = LinkedList<Product>()
                        listProduct.onEach { actProductEntity->
                            listProducts.add(Product(actProductEntity.catalogcode,
                                actProductEntity.catalogname, actProductEntity.count,
                                actProductEntity.measureid,actProductEntity.name,
                                actProductEntity.ordno,actProductEntity.summa,
                                actProductEntity.totalSumma))
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
                                    logData("SaveActData->${result.data.toString()}")
                                }
                                is ResponseState.Error->{
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
            mainActivity.containerApplication.screenNavigate.createDocument(responseSaveAct?.data?.actid.toString(),1)
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
                                addProductBinding.listCatalogCode.text =
                                    "${if (isUpdate) actProductEntityUpdate?.catalogcode else tasnifProduct.data[0].mxikCode} ${if (isUpdate) actProductEntityUpdate?.catalogname else tasnifProduct.data[0].mxikFullName}"
                                this@ActUiController.tasnifProduct = tasnifProduct.data[0]
                                if (tasnifProduct.data.size > 1){
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
                                addProductBinding.btnSaveProduct.text = mainActivity.getString(R.string.update)
                            }
                            addProductBinding.editCount.doAfterTextChanged { countApp->
                                val priceOne = addProductBinding.editSumma.text.toString()
                                if (countApp.toString().isNotEmptyOrNull() && priceOne.isNotEmptyOrNull()){
                                    val price = addProductBinding.editSumma.text.toString()
                                    val priceAll = formatterApp(formatterApp(price).multiply(BigDecimal(countApp.toString())).toPlainString())
                                    addProductBinding.editTotalSum.setText(priceAll.toPlainString())
                                }
                            }

                            addProductBinding.editSumma.doAfterTextChanged { priceApp->
                                val countApp = addProductBinding.editCount.text.toString()
                                if (priceApp.toString().isNotEmptyOrNull() && countApp.isNotEmptyOrNull()){
                                    val price = addProductBinding.editSumma.text.toString()
                                    val priceAll = formatterApp(formatterApp(price).multiply(BigDecimal(countApp)).toPlainString())
                                    addProductBinding.editTotalSum.setText(priceAll.toPlainString())
                                }
                            }
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
                            addProductBinding.btnSaveProduct.setOnClickListener {
                                val name = addProductBinding.editName.text.toString()
                                val count = addProductBinding.editCount.text.toString()
                                val summa = addProductBinding.editSumma.text.toString()
                                val totalSumma = addProductBinding.editTotalSum.text.toString()
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
                                        } else
                                        {
                                            val actProductEntity = ActProductEntity(ordno = orderNumberProduct.toInt(), catalogcode = catalogCode.toString(),
                                                catalogname = catalogName.toString(), name = name, measureid = measureId?:0, count = count, summa = summa, totalSumma = totalSumma)
                                            actViewModel.saveActProduct(actProductEntity)
                                            mainActivity.motionAnimation("success",mainActivity.getString(R.string.success_add_product))
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
            this.btnClose.setOnClickListener {
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


    // Incoming act
    private val genericPagingAdapterIncoming:GenericPagingAdapter<uz.idea.domain.models.act.actIncoming.actIn.Data> by lazy {
        GenericPagingAdapter(R.layout.item_draft){ data, position, clickType, viewBinding ->
            mainActivity.containerApplication.screenNavigate.createDocument(data?._id.toString(),1)
        }
    }
    override fun incomingAct() {
        binding.includeActIncoming.apply {
            val layoutManager = LinearLayoutManager(mainActivity)
            rvIncoming.layoutManager = layoutManager

            rvIncoming.addOnScrollListener(object: RecyclerView.OnScrollListener(){
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (genericPagingAdapter.itemCount>5){
                        if (layoutManager.findLastCompletelyVisibleItemPosition() == genericPagingAdapterIncoming.itemCount-1){
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
            val incomingFilter = IncomingFilterModel()
            pagingDataIncoming(incomingFilter)
            swipeRefresh.setOnRefreshListener {
                pagingDataIncoming(incomingFilter)
            }
            rvIncoming.adapter = genericPagingAdapterIncoming
            swipeRefresh.setColorSchemeColors(ContextCompat.getColor(mainActivity,R.color.primary_color))
        }
        incomingLoadState()
    }
    private fun pagingDataIncoming(incomingFilterModel: IncomingFilterModel) {
        binding.includeActIncoming.apply {
            homeFragment.lifecycleScope.launchWhenCreated {
                actViewModel.actIncomingData(lang = getLanguage(mainActivity),incomingFilterModel).collect { pagingData->
                    genericPagingAdapterIncoming.submitData(pagingData)
                    swipeRefresh.isRefreshing = false
                }
            }
        }
    }
    private fun incomingLoadState(){
        genericPagingAdapterIncoming
            .withLoadStateHeaderAndFooter(
                header = ExampleLoadStateAdapter(R.layout.load_state_draft,genericPagingAdapterIncoming::retry),
                footer = ExampleLoadStateAdapter(R.layout.load_state_draft,genericPagingAdapterIncoming::retry)
            )
        homeFragment.lifecycleScope.launch {
            genericPagingAdapterIncoming.loadStateFlow.collectLatest {  loadStates ->
                binding.includeActIncoming.shimmerInclude.shimmer.isVisible = loadStates.refresh is LoadState.Loading
                binding.includeActIncoming.rvIncoming.isVisible = loadStates.refresh !is LoadState.Loading
                binding.includeActIncoming.swipeRefresh.isRefreshing = loadStates.refresh is LoadState.Error
            }
        }
    }
    // Incoming act


    // send act
    private val genericPagingAdapterActSend:GenericPagingAdapter<uz.idea.domain.models.act.actSend.actSendData.Data> by lazy {
        GenericPagingAdapter(R.layout.item_draft){ data, position, clickType, viewBinding ->
            mainActivity.containerApplication.screenNavigate.createDocument(data?._id.toString(),1)
        }
    }
    override fun outgoingAct() {
        binding.includeActSent.apply {
            val layoutManager = LinearLayoutManager(mainActivity)
            rvActSend.layoutManager = layoutManager

            rvActSend.addOnScrollListener(object: RecyclerView.OnScrollListener(){
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (genericPagingAdapter.itemCount>5){
                        if (layoutManager.findLastCompletelyVisibleItemPosition() == genericPagingAdapterActSend.itemCount-1){
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

            val actSendFilter = ActSendFilter()
            pagingDataActSend(actSendFilter)
            swipeRefresh.setOnRefreshListener {
                pagingDataActSend(actSendFilter)
            }
            rvActSend.adapter = genericPagingAdapterActSend
            swipeRefresh.setColorSchemeColors(ContextCompat.getColor(mainActivity,R.color.primary_color))
        }
        actSendLoadState()
    }
    private fun pagingDataActSend(actSendFilter: ActSendFilter){
        binding.includeActDraft.apply {
            homeFragment.lifecycleScope.launchWhenCreated {
                actViewModel.actSendData(lang = getLanguage(mainActivity),actSendFilter).collect { pagingData->
                    genericPagingAdapterActSend.submitData(pagingData)
                    swipeRefresh.isRefreshing = false
                }
            }
        }
    }
    private fun actSendLoadState(){
        genericPagingAdapterActSend
            .withLoadStateHeaderAndFooter(
                header = ExampleLoadStateAdapter(R.layout.load_state_draft,genericPagingAdapter::retry),
                footer = ExampleLoadStateAdapter(R.layout.load_state_draft,genericPagingAdapter::retry)
            )
        homeFragment.lifecycleScope.launch {
            genericPagingAdapterActSend.loadStateFlow.collectLatest {  loadStates ->
                binding.includeActSent.shimmerInclude.shimmer.isVisible = loadStates.refresh is LoadState.Loading
                binding.includeActSent.rvActSend.isVisible = loadStates.refresh !is LoadState.Loading
                binding.includeActSent.swipeRefresh.isRefreshing = loadStates.refresh is LoadState.Error
            }
        }
    }
   // send act

    // draft act
    private val genericPagingAdapter:GenericPagingAdapter<uz.idea.domain.models.act.actDraftModel.actDraft.Data> by lazy {
        GenericPagingAdapter(R.layout.item_draft){ data, position, clickType, viewBinding ->
            mainActivity.containerApplication.screenNavigate.createDocument(data?._id.toString(),1)
        }
    }
    override fun draftAct() {
        binding.includeActDraft.apply {
            val layoutManager = LinearLayoutManager(mainActivity)
            rvDraft.layoutManager = layoutManager

            rvDraft.addOnScrollListener(object: RecyclerView.OnScrollListener(){
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (genericPagingAdapter.itemCount>5){
                        if (layoutManager.findLastCompletelyVisibleItemPosition() == genericPagingAdapter.itemCount-1){
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
            var actFilter = ActDraftFilter()
            homeFragment.lifecycleScope.launchWhenCreated {
                mainActivity.containerViewModel.actFilter.collect {
                    actFilter = it?:ActDraftFilter()
                }
            }
            pagingData(actFilter)
            swipeRefresh.setOnRefreshListener {
                actFilter = ActDraftFilter()
                pagingData(actFilter)
            }
            rvDraft.adapter = genericPagingAdapter
            swipeRefresh.setColorSchemeColors(ContextCompat.getColor(mainActivity,R.color.primary_color))
        }
        draftLoadState()
    }
    private fun pagingData(actFilter: ActDraftFilter){
        binding.includeActDraft.apply {
            homeFragment.lifecycleScope.launchWhenCreated {
                actViewModel.actDraftData(lang = getLanguage(mainActivity),actFilter).collect { pagingData->
                    genericPagingAdapter.submitData(pagingData)
                    swipeRefresh.isRefreshing = false
                }
            }
        }
    }
    private fun draftLoadState(){
        genericPagingAdapter
            .withLoadStateHeaderAndFooter(
                header = ExampleLoadStateAdapter(R.layout.load_state_draft,genericPagingAdapter::retry),
                footer = ExampleLoadStateAdapter(R.layout.load_state_draft,genericPagingAdapter::retry)
            )
        homeFragment.lifecycleScope.launch {
            genericPagingAdapter.loadStateFlow.collectLatest {  loadStates ->
                binding.includeActDraft.shimmerInclude.shimmer.isVisible = loadStates.refresh is LoadState.Loading
                binding.includeActDraft.rvDraft.isVisible = loadStates.refresh !is LoadState.Loading
                binding.includeActDraft.swipeRefresh.isRefreshing = loadStates.refresh is LoadState.Error
            }
        }
    }
    // draft act

    override fun processSendingAct() {
        binding.includeActCreate
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

