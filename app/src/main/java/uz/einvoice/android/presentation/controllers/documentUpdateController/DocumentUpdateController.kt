package uz.einvoice.android.presentation.controllers.documentUpdateController


import android.annotation.SuppressLint
import android.graphics.Color
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.widget.TextView
import androidx.core.widget.NestedScrollView
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import com.github.razir.progressbutton.attachTextChangeAnimator
import com.github.razir.progressbutton.bindProgressButton
import com.github.razir.progressbutton.hideProgress
import com.github.razir.progressbutton.showProgress
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.JsonParser
import uz.einvoice.domain.database.measure.MeasureEntity
import uz.einvoice.domain.models.branchModel.BranchModel
import uz.einvoice.domain.models.branchModel.Data
import uz.einvoice.domain.models.companyInfo.CompanyInfo
import uz.einvoice.domain.models.createActModel.Actdoc
import uz.einvoice.domain.models.createActModel.Contractdoc
import uz.einvoice.domain.models.createActModel.CreateActModel
import uz.einvoice.domain.models.createActModel.Productlist
import uz.einvoice.domain.models.documents.actDocument.ActDocument
import uz.einvoice.domain.models.documents.actDocument.Product
import uz.einvoice.domain.models.tasNifProduct.TasnifProduct
import uz.einvoice.domain.models.tinOrPinfl.pinfl.PinflModel
import uz.einvoice.domain.models.tinOrPinfl.tin456.Physical
import uz.einvoice.domain.models.tinOrPinfl.tinJuridic.LegalModel
import uz.einvoice.domain.usesCase.apiUsesCase.parseClass
import uz.einvoice.domain.utils.loadState.ResponseState
import uz.einvoice.android.R
import uz.einvoice.android.adapters.genericRvAdapter.GenericRvAdapter
import uz.einvoice.android.databinding.AddProductActBinding
import uz.einvoice.android.databinding.FragmentUpdateBinding
import uz.einvoice.android.presentation.activities.MainActivity
import uz.einvoice.android.presentation.screens.updateDocument.UpdateFragment
import uz.einvoice.android.utils.appConstant.AppConstant
import uz.einvoice.android.utils.appConstant.AppConstant.DELETE_CLICK
import uz.einvoice.android.utils.appConstant.AppConstant.EDITE_CLICK
import uz.einvoice.android.utils.appConstant.AppConstant.ERROR_BRANCH_CODE
import uz.einvoice.android.utils.extension.*
import uz.einvoice.android.vm.actVm.ActViewModel
import uz.einvoice.android.vm.documnetVm.DocumentViewModel
import uz.einvoice.domain.models.act.actCopy.copyRequest.ActCopyModel
import uz.einvoice.domain.models.act.actCopy.responceCopyAct.ResActCopy
import java.math.BigDecimal
import java.util.*
import kotlin.math.abs

class DocumentUpdateController(
    private val mainActivity: MainActivity,
    private val binding: FragmentUpdateBinding,
    private val actViewModel: ActViewModel,
    private val documentViewModel: DocumentViewModel,
    private val updateFragment: UpdateFragment,
    private var docId:String?
):UpdateController {
    // save act data
    private var actDate:String?=null
    private var actDateContract:String?=null
    private var branchDataMainSeller:Data?=null
    var companyInfoMain: CompanyInfo?=null
    var buyerName:String?=null
    var branchDataMainBuyer:Data?=null
    var tasnifProduct:uz.einvoice.domain.models.tasNifProduct.Data?=null
    var orderNumber:Long = 0
    var measureData: MeasureEntity?=null
    private var liveList = MutableLiveData<List<Product>>()
    override fun actDocumentUpdate() {
      actDocument(false)
    }


    override fun copyActDocument() {
        binding.apply {
            val actCopyModel = ActCopyModel(docId.toString())
            actViewModel.actCopy(getLanguage(mainActivity),actCopyModel)
            updateFragment.lifecycleScope.launchWhenCreated {
                actViewModel.copyAct.collect { result->
                    when(result){
                        is ResponseState.Loading->{
                            mainActivity.containerApplication.loadingSaved(true)
                        }
                        is ResponseState.Success->{
                            mainActivity.containerApplication.loadingSaved(false)
                           /// binding.menuViewAct.close(true)
                            val resActCopy = result.data?.parseClass(ResActCopy::class.java)
                            docId = resActCopy?.data?.actid.toString()
                            mainActivity.containerViewModel.updateDocument.postValue(true)
                            actDocument(true)
                            // update btn save
                        }
                        is ResponseState.Error->{
                            mainActivity.containerApplication.loadingSaved(false)
                            //binding.menuViewAct.close(true)
                            val errorAuth = JsonParser.parseString(result.exception.localizedMessage).asJsonObject
                            mainActivity.containerApplication.dialogData(AppConstant.ERROR_STATUS_UPDATE_ACT,errorAuth){ clickType ->
                                if (clickType==1) copyActDocument()
                            }
                        }
                    }
                }
            }
        }
    }


    private fun actDocument(isUpdate: Boolean){
        binding.apply {
            documentViewModel.getDocument(getLanguage(mainActivity),docId.toString())
            updateFragment.lifecycleScope.launchWhenCreated {
                documentViewModel.document.collect { result->
                    when(result){
                        is ResponseState.Loading->{
                            nestedActEdit.visible()
                        }
                        is ResponseState.Success->{
                            includeShimmerEditAct.shimmerCreateAct.gone()
                            val actDocument = result.data?.parseClass(ActDocument::class.java)
                            // act data
                            tvActDate.text = actDocument?.data?.actdoc?.actdate
                            editActno.setText(actDocument?.data?.actdoc?.actno)
                            editActText.setText(actDocument?.data?.actdoc?.acttext)
                            actDate = actDocument?.data?.actdoc?.actdate
                            // seller
                            editSellerName.setText(actDocument?.data?.sellername)
                            editSellerTin.setText(actDocument?.data?.sellertin.toString())
                            // seller branch data
                            sellerBranch(actDocument)
                            // contract data
                            editContractno.setText(actDocument?.data?.contractdoc?.contractno)
                            tvContractDate.text = actDocument?.data?.contractdoc?.contractdate
                            actDateContract = actDocument?.data?.contractdoc?.contractdate
                            // buyer data
                            editBuyerName.setText(actDocument?.data?.buyername)
                            editBuyerTinOrPinfl.setText(actDocument?.data?.buyertin.toString())
                            // buyer branch
                            if (actDocument?.data?.buyerbranchname!=null){
                                layoutBuyerBranches.visible()
                                listBuyerBranches.text = actDocument.data.buyerbranchname
                            }


                            editBuyerTinOrPinfl.addTextChangedListener(object: TextWatcher {
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


                            // product list
                            productList(actDocument)
                            liveList.observe(updateFragment.viewLifecycleOwner){ listProduct->
                                genericProductAdapter.submitList(listProduct)
                            }
                            // add product
                            btnAddProduct.setOnClickListener {
                                addProduct(false,null,null)
                            }
                            // update act
                            if (isUpdate) btnUpdate.text = updateFragment.getString(R.string.save)
                            btnUpdate.setOnClickListener {
                                updateActData(actDocument,isUpdate)
                            }
                        }
                        is ResponseState.Error->{
                            includeShimmerEditAct.shimmerCreateAct.gone()
                        }
                    }
                }
            }
            // nested scroll
            binding.nestedActEdit.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
                if (scrollY < oldScrollY) {
                    mainActivity.bottomBarView(true)
                }
                if (scrollY == abs( v.measuredHeight - v.getChildAt(0).measuredHeight)) {
                    mainActivity.bottomBarView(false)
                }
            })

            // act date
            tvActDate.setOnClickListener {
                datePicker(tvActDate,0)
            }

            // dagavor date
            tvContractDate.setOnClickListener {
                datePicker(tvContractDate,1)
            }


        }
    }


    // save act
    private fun updateActData(actDocument: ActDocument?,isUpdate: Boolean){
        binding.apply {
            val actNumber = editActno.text.toString().trim()
            val actDate = tvActDate.text.toString().trim()
            val actText = editActText.text.toString().trim()

            var actDoc: Actdoc?=null
            var contractDoc: Contractdoc?=null

            if (!actNumber.isNotEmptyOrNull()){ mainActivity.motionAnimation("error",mainActivity.getString(R.string.no_act_number)) } else
                if (!actDate.isNotEmptyOrNull()){ mainActivity.motionAnimation("error",mainActivity.getString(R.string.no_act_data)) } else
                    if (!actText.isNotEmptyOrNull()){ mainActivity.motionAnimation("error",mainActivity.getString(R.string.no_act_description)) } else {
                        actDoc  = Actdoc(this@DocumentUpdateController.actDate.toString(),actNumber,actText)
                    }
            val contractNumber = editContractno.text.toString().trim()
            val contractDate = tvContractDate.text.toString().trim()
            if (!contractNumber.isNotEmptyOrNull()){
                mainActivity.motionAnimation("error",mainActivity.getString(R.string.no_contract_number))
            } else if (!contractDate.isNotEmptyOrNull()){
                mainActivity.motionAnimation("error",mainActivity.getString(R.string.no_contract_date))
            } else {
                contractDoc = Contractdoc(this@DocumentUpdateController.actDateContract.toString(),contractNumber)
            }

            if (actDoc!=null && contractDoc!=null && companyInfoMain!=null){
                // seller data act
                val sellerTin = companyInfoMain?.data?.tin
                val sellerName = companyInfoMain?.data?.shortName
                val sellerBranchCode = branchDataMainSeller?.branchNum
                val sellerBranchName = branchDataMainSeller?.branchName
                // buyer data Act
                var buyerNameData:String?=null
                if (! binding.editBuyerName.text.toString().isNotEmptyOrNull()) {
                    mainActivity.motionAnimation("error",mainActivity.getString(R.string.no_buyer_name))
                } else {
                    buyerNameData =  binding.editBuyerName.text.toString()
                }
                var buyerTinOrPinfl:String?=null
                if (! binding.editBuyerTinOrPinfl.text.toString().isNotEmptyOrNull()) {
                    mainActivity.motionAnimation("error",mainActivity.getString(R.string.no_buyer_tin_or_pinfl))
                }else{
                    buyerTinOrPinfl =  binding.editBuyerTinOrPinfl.text.toString()
                }
                val buyerBranchCode = branchDataMainBuyer?.branchNum
                val buyerBranchName = branchDataMainBuyer?.branchName

                // productList
                val sellerTinProduct = sellerTin

                liveList.observe(updateFragment.viewLifecycleOwner){ listProduct->
                    val listProducts = LinkedList<uz.einvoice.domain.models.createActModel.Product>()
                    listProduct.onEach { actProductEntity->
                        listProducts.add(
                            uz.einvoice.domain.models.createActModel.Product(
                                actProductEntity.catalogcode.toString(),
                                actProductEntity.catalogname.toString(),
                                actProductEntity.count,
                                actProductEntity.measureid,
                                actProductEntity.name,
                                actProductEntity.ordno.toInt(),
                                actProductEntity.summa,
                                BigDecimal(actProductEntity.totalsum)
                            )
                        )
                    }

                    // big class act save
                    

                    val productList = Productlist(actDocument?.data?.productlist?.actproductid.toString(),listProducts,sellerTinProduct.toString())

                    val createActModel = CreateActModel(actDoc, actDocument?.data?._id.toString(),buyerBranchCode.toString(),
                        buyerBranchName.toString(),buyerNameData.toString(),
                        buyerTinOrPinfl.toString(),contractDoc, productList,sellerBranchCode.toString(),
                        sellerBranchName.toString(),sellerName.toString(),sellerTin.toString(),actDocument?.data?.stateid)
                       actViewModel.updateAct(getLanguage(mainActivity),createActModel)
                       updateFragment.lifecycleScope.launchWhenCreated {
                        actViewModel.updateAct.collect { result->
                            when(result){
                                is ResponseState.Loading->{
                                    binding.btnUpdate.enabledFalse()
                                    mainActivity.loadingSave(true)
                                }
                                is ResponseState.Success->{
                                    mainActivity.containerViewModel.updateDocument.postValue(true)
                                    loadingButton(false)
                                    mainActivity.loadingSave(false)
                                    binding.btnUpdate.enabled()
                                    clearUiActCreate()
                                }
                                is ResponseState.Error->{
                                    loadingButton(false)
                                    mainActivity.loadingSave(false)
                                    binding.btnUpdate.enabled()
                                    if (result.exception.localizedMessage.isNotEmptyOrNull()){
                                        val errorAuth = JsonParser.parseString(result.exception.localizedMessage).asJsonObject
                                        mainActivity.containerApplication.dialogData(AppConstant.MENU_ERROR,errorAuth){ clickType->
                                            if(clickType==1) updateActData(actDocument,isUpdate)
                                        }
                                    } else {
                                        mainActivity.containerApplication.dialogData(AppConstant.NO_INTERNET,null){ clickType ->
                                            if(clickType==1) updateActData(actDocument,isUpdate)
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

    private fun clearUiActCreate(){
        binding.apply {
            editActno.text.clear()
            tvActDate.text = ""
            editContractno.text.clear()
            tvContractDate.text = ""
            editBuyerTinOrPinfl.text.clear()
            editBuyerName.clear()
            editActText.text.clear()
            mainActivity.navHostFragment.navController.popBackStack()
        }
    }

    private fun loadingButton(isLoading:Boolean){
        binding.apply {
            if (isLoading){
                updateFragment.bindProgressButton(btnUpdate)
                // (Optional) Enable fade In / Fade out animations
                btnUpdate.attachTextChangeAnimator()
                // Show progress with "Loading" text
                btnUpdate.showProgress {
                    buttonTextRes = R.string.loading_text
                    progressColor = Color.WHITE
                }
            }else{
                // Hide progress and show "Submit" text instead
                btnUpdate.hideProgress(R.string.save)
            }
        }
    }



    // buyerData
    private fun buyerData(tinOrPinfl:String?){
        updateFragment.lifecycleScope.launchWhenCreated {
            actViewModel.buyerData.collect { result->
                when(result){
                    is ResponseState.Loading->{
                        binding.progressBuyerName.visible()
                        binding.progressBuyerTin.visible()
                    }
                    is ResponseState.Success->{
                        binding.progressBuyerName.gone()
                        binding.progressBuyerTin.gone()

                        if (tinOrPinfl.toString().trim().length == 9){
                            val juridicOrPhysical = tinOrPinfl.toString().getJuridicOrPhysical()

                            if (juridicOrPhysical){
                                // agar 4,5,6 bulsa Jismoniy Physical
                                val physical = result.data[0]?.parseClass(Physical::class.java)
                                binding.editBuyerName.setText(physical?.data?.fullName)
                                if (physical?.data?.personalNum.isNotEmptyOrNull()){
                                    binding.editBuyerTinOrPinfl.setText(physical?.data?.personalNum)
                                }
                                buyerName = physical?.data?.fullName
                                descriptionText()
                            } else {
                                // agar text 4,5,6 dan boshqalarida bulsa yuridic legal
                                val legal = result.data[0]?.parseClass(LegalModel::class.java)
                                binding.editBuyerName.setText(legal?.data?.shortName)
                                buyerName = legal?.data?.shortName
                                descriptionText()
                            }
                        } else if (tinOrPinfl.toString().trim().length == 14){
                            // agar pinfl bulsa
                            val pinflModel = result.data[0]?.parseClass(PinflModel::class.java)
                            binding.editBuyerName.setText(pinflModel?.data?.fullName)
                            buyerName = pinflModel?.data?.fullName
                            descriptionText()
                        }

                        val branches = result.data[1]
                        if (branches!=null){
                            val branchModel = branches.parseClass(BranchModel::class.java)
                            if(branchModel.data.isNotEmpty()){
                                binding.layoutBuyerBranches.visible()
                                binding.listBuyerBranches.setOnClickListener {
                                    mainActivity.containerApplication.applicationDialog(0, branchModel.data){ data ->
                                        branchDataMainBuyer =  data
                                        binding.listBuyerBranches.text = data.branchName
                                        binding.cancelBuyerBranch.visible()
                                        descriptionText()
                                        binding.cancelBuyerBranch.setOnClickListener {
                                            binding.cancelBuyerBranch.gone()
                                            binding.listBuyerBranches.text = mainActivity.getString(R.string.choose)
                                            branchDataMainBuyer =  null
                                            descriptionText()
                                        }
                                    }
                                }
                            }
                        }
                    }


                    is ResponseState.Error->{
                        binding.progressBuyerName.gone()
                        binding.progressBuyerTin.gone()
                    }
                }
            }
        }
    }
    // seller branch
    private fun sellerBranch(actDocument: ActDocument?){
        binding.apply {
            actViewModel.getUserCompany(getLanguage(mainActivity), actDocument?.data?.sellertin.toString())
            updateFragment.lifecycleScope.launchWhenCreated {
                actViewModel.companyInfo.collect { result->
                    when(result){
                        is ResponseState.Success->{
                            if(result.data.isNotEmpty() && result.data[0]!=null){
                                var companyInfo = result.data[0]?.parseClass(CompanyInfo::class.java)
                                editSellerTin.setText(companyInfo?.data?.tin)
                                editSellerName.setText(companyInfo?.data?.shortName.toString())
                                companyInfoMain = companyInfo
                            }

                            val branchModel = result.data[1]?.parseClass(BranchModel::class.java)
                            if (branchModel?.data?.isNotEmpty() == true){
                                val listBranch = LinkedList<Data>()
                                if (actDocument?.data?.sellerbranchcode!=null){
                                    branchModel.data.onEach { data ->
                                        if (actDocument.data.sellerbranchcode == data.branchNum){
                                            listBranch.add(data)
                                        }
                                    }
                                    if (listBranch.isNotEmpty()){
                                        layoutSellerBranches.visible()
                                        listSellerBranches.setOnClickListener {
                                            if (listBranch.isNotEmpty() && branchModel.data.size > 1){
                                                mainActivity.containerApplication.applicationDialog(0, branchModel.data){ data ->
                                                    branchDataMainSeller =  data
                                                    listSellerBranches.text = data.branchName
                                                    cancelBranchSeller.visible()
                                                    descriptionText()
                                                    cancelBranchSeller.setOnClickListener {
                                                        listSellerBranches.text = mainActivity.getString(
                                                            R.string.choose)
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
                                if (actDocument?.data?.sellerbranchname!=null)
                                    listSellerBranches.text = actDocument.data.sellerbranchname
                            }
                        }
                        is ResponseState.Error->{
                            val errorData = JsonParser.parseString(result.exception.localizedMessage).asJsonObject
                            mainActivity.containerApplication.dialogData(ERROR_BRANCH_CODE,errorData){ clickType ->
                                if (clickType==1) sellerBranch(actDocument)
                            }
                        }
                        is ResponseState.Loading->{

                        }
                    }
                }
            }
        }
    }
    // product list
    private val genericProductAdapter:GenericRvAdapter<Product> by lazy {
        GenericRvAdapter(R.layout.recycler_act_product_item){ data, position, clickType ->
            when(clickType){
                EDITE_CLICK ->{
                    addProduct(true,data,position)
                }
                DELETE_CLICK ->{
                    val listProduct = LinkedList<Product>()
                    listProduct.addAll(liveList.value?: emptyList())
                    listProduct.remove(data)
                    liveList.postValue(listProduct)
                }
            }
        }
    }
    private fun productList(actDocument: ActDocument?){
        if (actDocument?.data?.productlist?.products?.isNotEmpty() == true){
            liveList.postValue(actDocument.data.productlist.products)
            binding.recyclerViewProducts.adapter = genericProductAdapter
        }
    }



    @SuppressLint("SetTextI18n")
    private fun addProduct(isUpdate:Boolean, product: Product?,position:Int?){
        val bottomSheetDialog = BottomSheetDialog(mainActivity)
        val addProductBinding = AddProductActBinding.inflate(mainActivity.layoutInflater)
        addProductBinding.apply {
            actViewModel.productData(getLanguage(mainActivity))
            updateFragment.lifecycleScope.launchWhenCreated {
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
                                addProductBinding.listCatalogCode.text = "${if (isUpdate) product?.catalogcode else mainActivity.getString(R.string.checked_text)} ${if (isUpdate) product?.catalogname else ""}"

                                this@DocumentUpdateController.tasnifProduct = null
                                if (tasnifProduct.data.size > 1){
                                    if (isUpdate){
                                        tasnifProduct.data.map {  data ->
                                            if (data.mxikCode == product?.catalogcode && data.mxikFullName == product.catalogname){
                                                this@DocumentUpdateController.tasnifProduct = data
                                            }
                                        }
                                    }

                                    addProductBinding.listCatalogCode.setOnClickListener {
                                        mainActivity.containerApplication.applicationDialog(1, tasnifProduct.data){ data ->
                                            addProductBinding.listCatalogCode.text = "${data.mxikCode} ${data.mxikFullName}"
                                            this@DocumentUpdateController.tasnifProduct = data
                                        }
                                    }
                                }
                            } else {
                                addProductBinding.listCatalogCode.text = mainActivity.getString(R.string.no_data)
                            }
                            if (actViewModel.getMeasure().isNotEmpty()){
                                if (isUpdate){
                                    actViewModel.getMeasure().map { measureEntity ->
                                        if (measureEntity.measureId == product?.measureid){
                                            addProductBinding.listMeasureId.text = measureEntity.name
                                            this@DocumentUpdateController.measureData = measureEntity
                                        }
                                    }
                                }else {
                                    addProductBinding.listMeasureId.text = actViewModel.getMeasure()[0].name
                                    this@DocumentUpdateController.measureData = actViewModel.getMeasure()[0]
                                }
                            }
                            addProductBinding.listMeasureId.setOnClickListener {
                                mainActivity.containerApplication.applicationDialog(2,  actViewModel.getMeasure()){ data ->
                                    addProductBinding.listMeasureId.text = data.name
                                     this@DocumentUpdateController.measureData = data
                                }
                            }
                            if (isUpdate) {
                                addProductBinding.editCount.setText(product?.count.toString())
                                addProductBinding.editName.setText(product?.name.toString())
                                addProductBinding.editSumma.setText(product?.summa.toString())
                                addProductBinding.editTotalSum.setText(product?.totalsum.toString())
                                addProductBinding.btnSaveProduct.text = mainActivity.getString(R.string.update)
                                addProductBinding.btnAdded.text = mainActivity.getString(R.string.close)
                            } else {
                                addProductBinding.btnSaveProduct.text = mainActivity.getString(R.string.close)
                                addProductBinding.btnAdded.text = mainActivity.getString(R.string.added)
                            }
                            addProductBinding.editCount.doAfterTextChanged { countApp->
                                val priceOne = addProductBinding.editSumma.text.toString()
                                if (countApp.toString().isNotEmptyOrNull() && priceOne.isNotEmptyOrNull()){
                                    val price = addProductBinding.editSumma.text.toString()
                                    val priceAll = formatterApp(formatterApp(price).multiply(
                                        BigDecimal(countApp.toString())
                                    ).toPlainString())
                                    addProductBinding.editTotalSum.setText(priceAll.toPlainString())
                                }
                            }



                            addProductBinding.editSumma.filters = arrayOf<InputFilter>(DecimalDigitsInputFilter(30, 2))

                            addProductBinding.editSumma.addTextChangedListener(object:TextWatcher{
                                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                                var timer = Timer()
                                val DELAY: Long = 1000 // Milliseconds
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
                                                            updateFragment.lifecycleScope.launchWhenCreated {
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

                            updateFragment.lifecycleScope.launchWhenCreated {
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
                                if (isUpdate){
                                    productAdded(addProductBinding,isUpdate, product, position, bottomSheetDialog)
                                } else {
                                    bottomSheetDialog.dismiss()
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
            this.btnAdded.setOnClickListener {
                if (!isUpdate){
                    productAdded(addProductBinding,isUpdate, product, position, bottomSheetDialog)
                } else {
                    bottomSheetDialog.dismiss()
                }
            }
        }
        bottomSheetDialog.setContentView(addProductBinding.root)
        bottomSheetDialog.show()
    }

    private fun productAdded(addProductBinding:AddProductActBinding,isUpdate: Boolean,product: Product?,position: Int?,bottomSheetDialog: BottomSheetDialog){
        val listProduct = LinkedList<Product>()
        listProduct.addAll(liveList.value?: emptyList())
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
                val catalogCode = this@DocumentUpdateController.tasnifProduct?.mxikCode
                val catalogName = this@DocumentUpdateController.tasnifProduct?.mxikFullName
                logData("catalogCode-> $catalogCode \n catalogName->${catalogName}")
                val measureId = measureData?.measureId
                if (isUpdate){
                    product?.catalogcode = catalogCode.toString()
                    product?.catalogname = catalogName.toString()
                    product?.name = name
                    product?.measureid = measureId?:0
                    product?.count = count
                    product?.summa = summa
                    product?.totalsum = totalSumma
                    listProduct[position?:0] = product!!
                    liveList.postValue(listProduct)
                    mainActivity.motionAnimation("success",mainActivity.getString(R.string.success_update_product))
                    bottomSheetDialog.dismiss()
                } else {
                    val productData = Product(catalogCode,catalogName,count,measureId?:0,name,orderNumberProduct.toString(),summa,totalSumma)
                    listProduct.add(productData)
                    liveList.postValue(listProduct)
                    mainActivity.motionAnimation("success",mainActivity.getString(R.string.success_add_product))
                }
            }
        }
    }


    @SuppressLint("SetTextI18n")
    private fun descriptionText(){
        if(companyInfoMain?.data?.shortName.isNotEmptyOrNull() && buyerName.isNotEmptyOrNull()){
            binding.editActText.setText("${mainActivity.getText(R.string.act_description1)} ${companyInfoMain?.data?.shortName} ${if (branchDataMainSeller!=null) "-"+branchDataMainSeller?.branchName else ""}${mainActivity.getText(R.string.act_description2)} $buyerName  ${if (branchDataMainBuyer != null) "-"+ branchDataMainBuyer?.branchName else ""}, ${mainActivity.getText(R.string.act_description3)}")
        } else {
            binding.editActText.text.clear()
        }
    }

    private fun datePicker(textView: TextView, type:Int){
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
}