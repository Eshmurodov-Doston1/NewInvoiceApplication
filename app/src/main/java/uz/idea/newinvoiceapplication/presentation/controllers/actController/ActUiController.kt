package uz.idea.newinvoiceapplication.presentation.controllers.actController

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.NestedScrollView
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.flow.collect
import uz.idea.domain.database.actProductEntity.ActProductEntity
import uz.idea.domain.database.measure.MeasureEntity
import uz.idea.domain.models.branchModel.BranchModel
import uz.idea.domain.models.branchModel.Data
import uz.idea.domain.models.companyInfo.CompanyInfo
import uz.idea.domain.models.tasNifProduct.TasnifProduct
import uz.idea.domain.models.tinOrPinfl.pinfl.PinflModel
import uz.idea.domain.models.tinOrPinfl.tin456.Physical
import uz.idea.domain.models.tinOrPinfl.tinJuridic.LegalModel
import uz.idea.domain.usesCase.apiUsesCase.parseClass
import uz.idea.domain.utils.loadState.ResponseState
import uz.idea.newinvoiceapplication.R
import uz.idea.newinvoiceapplication.adapters.genericRvAdapter.GenericRvAdapter
import uz.idea.newinvoiceapplication.databinding.ActCreateViewBinding
import uz.idea.newinvoiceapplication.databinding.AddProductActBinding
import uz.idea.newinvoiceapplication.presentation.activities.MainActivity
import uz.idea.newinvoiceapplication.presentation.screens.homeScreen.HomeFragment
import uz.idea.newinvoiceapplication.utils.container.ContainerApplication
import uz.idea.newinvoiceapplication.utils.extension.*
import uz.idea.newinvoiceapplication.vm.actVm.ActViewModel
import uz.idea.newinvoiceapplication.vm.containerVm.ContainerViewModel
import java.math.BigDecimal
import java.util.*
import kotlin.math.abs

class ActUiController(
    private val mainActivity: MainActivity,
    private val actCreateViewBinding: ActCreateViewBinding,
    private val containerApplication:ContainerApplication,
    private val containerViewModel: ContainerViewModel,
    private val actViewModel: ActViewModel,
    private val homeFragment: HomeFragment
):ActController{

    // save act data
    var branchDataMain:Data?=null
    var companyInfoMain:CompanyInfo?=null


    var tasnifProduct:uz.idea.domain.models.tasNifProduct.Data?=null
    var orderNumber:Long = 0
    var measureData:MeasureEntity?=null
    // save act data

    override fun createAct() {
        actCreateViewBinding.nested.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            if (scrollY < oldScrollY) {
                mainActivity.bottomBarView(true)
            }
            if (scrollY == abs( v.measuredHeight - v.getChildAt(0).measuredHeight)) {
                mainActivity.bottomBarView(false)
            }
        })

        // date picker
        actCreateViewBinding.apply {
            // act date
            tvActDate.setOnClickListener {
               datePicker(tvActDate)
            }

            // dagavor date
            tvContractDate.setOnClickListener {
                datePicker(tvContractDate)
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
                            if (result.data[1]!=null && result.data.isNotEmpty()){
                                val branchModel = result.data[1]?.parseClass(BranchModel::class.java)
                                if (branchModel?.data?.isNotEmpty() == true){
                                    val listBranch = LinkedList<Data>()
                                    if (branchModel.data[0] !=null){
                                        branchDataMain = branchModel.data[0]
                                    }
                                    branchModel.data.onEach { data ->
                                        if (userData?.data?.branch?.branchNum.equals(data.branchNum)){
                                            listBranch.add(data)
                                        }
                                    }
                                    if (listBranch.isNotEmpty()){
                                        layoutSellerBranches.visible()
                                        listSellerBranches.text = listBranch[0].branchName
                                        listSellerBranches.setOnClickListener {
                                            if (listBranch.isNotEmpty() && listBranch.size > 1){
                                                containerApplication.applicationDialog(0,branchModel?.data?: emptyList()){ data ->
                                                    branchDataMain =  data
                                                    listSellerBranches.text = data.branchName
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
            editBuyerTinOrPinfl.addTextChangedListener { tinOrPinfl->
                if(tinOrPinfl.toString().trim().isNotEmptyOrNull()){
                    if (tinOrPinfl.toString().trim().length == 9 || tinOrPinfl.toString().trim().length == 14) {
                        actViewModel.buyerData(getLanguage(mainActivity), tinOrPinfl.toString().trim())
                        buyerData(tinOrPinfl.toString())
                    }
                }
            }

            // product add button
            btnAddProduct.setOnClickListener {
                addProduct()
            }

            // product rv
            val productAdapter:GenericRvAdapter<ActProductEntity> by lazy {
                GenericRvAdapter(R.layout.recycler_act_product_item){ data, position, clickType ->

                }
            }
            actViewModel.getAllActProduct().observe(homeFragment.viewLifecycleOwner){ listActProduct->
                productAdapter.submitList(listActProduct)
                actCreateViewBinding.recyclerViewProducts.adapter = productAdapter
            }

        }
    }

    @SuppressLint("SetTextI18n")
    private fun descriptionText(textSeller:String?, textBuyer:String?){
        logData("TextSeller:=>${textSeller.toString()}")
        logData("TextBuyer:=>${textBuyer.toString()}")
        if(textSeller.isNotEmptyOrNull() && textBuyer.isNotEmptyOrNull()){
            actCreateViewBinding.editActText.setText("${mainActivity.getText(R.string.act_description1)} $textSeller ${mainActivity.getText(R.string.act_description2)} $textBuyer ${mainActivity.getText(R.string.act_description3)}")
        } else {
            actCreateViewBinding.editActText.text.clear()
        }
    }

    private fun buyerData(tinOrPinfl:String?){
        homeFragment.lifecycleScope.launchWhenCreated {
            actViewModel.buyerData.collect { result->
                when(result){
                    is ResponseState.Loading->{
                        actCreateViewBinding.progressBuyerName.visible()
                        actCreateViewBinding.progressBuyerTin.visible()
                    }
                    is ResponseState.Success->{
                        actCreateViewBinding.progressBuyerName.gone()
                        actCreateViewBinding.progressBuyerTin.gone()

                        if (tinOrPinfl.toString().trim().length == 9){
                            val juridicOrPhysical = tinOrPinfl.toString().getJuridicOrPhysical()

                            if (juridicOrPhysical){
                                // agar 4,5,6 bulsa Jismoniy Physical
                                val physical = result.data[0]?.parseClass(Physical::class.java)
                                actCreateViewBinding.editBuyerName.setText(physical?.data?.fullName)
                                descriptionText(companyInfoMain?.data?.shortName,physical?.data?.fullName)
                            } else {
                                // agar text 4,5,6 dan boshqalarida bulsa yuridic legal
                                val legal = result.data[0]?.parseClass(LegalModel::class.java)
                                actCreateViewBinding.editBuyerName.setText(legal?.data?.shortName)
                                descriptionText(companyInfoMain?.data?.shortName,legal?.data?.shortName)
                            }
                        } else if (tinOrPinfl.toString().trim().length == 14){
                            // agar pinfl bulsa
                            val pinflModel = result.data[0]?.parseClass(PinflModel::class.java)
                            actCreateViewBinding.editBuyerName.setText(pinflModel?.data?.fullName)
                            descriptionText(companyInfoMain?.data?.shortName,pinflModel?.data?.fullName)
                        }

                        val branches = result.data[1]
                        if (branches!=null){
                            val branchModel = branches.parseClass(BranchModel::class.java)
                            if(branchModel.data.isNotEmpty()){
                                actCreateViewBinding.layoutBuyerBranches.visible()
                                actCreateViewBinding.listBuyerBranches.text = branchModel.data[1].branchName
                                containerApplication.applicationDialog(0, branchModel.data){ data ->
                                    branchDataMain =  data
                                    actCreateViewBinding.listBuyerBranches.text = data.branchName
                                }
                            }
                        }
                    }
                    is ResponseState.Error->{
                        actCreateViewBinding.progressBuyerName.gone()
                        actCreateViewBinding.progressBuyerTin.gone()
                    }
                }
            }
        }
    }



    @SuppressLint("SetTextI18n")
    private fun addProduct(){
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
                                addProductBinding.listCatalogCode.text = "${tasnifProduct.data[0].mxikCode} ${tasnifProduct.data[0].mxikFullName}"
                                this@ActUiController.tasnifProduct = tasnifProduct.data[0]
                                if (tasnifProduct.data.size > 1){
                                    addProductBinding.listCatalogCode.setOnClickListener {
                                        containerApplication.applicationDialog(1, tasnifProduct.data){ data ->
                                            addProductBinding.listCatalogCode.text = "${data.mxikCode} ${data.mxikFullName}"
                                            this@ActUiController.tasnifProduct = data
                                        }
                                    }
                                }
                            } else {
                                addProductBinding.listCatalogCode.text = mainActivity.getString(R.string.no_data)
                            }
                            if (actViewModel.getMeasure().isNotEmpty()){
                                addProductBinding.listMeasureId.text = actViewModel.getMeasure()[0].name
                                this@ActUiController.measureData = actViewModel.getMeasure()[0]
                            }
                            addProductBinding.listMeasureId.setOnClickListener {
                                containerApplication.applicationDialog(2,  actViewModel.getMeasure()){ data ->
                                    addProductBinding.listMeasureId.text = data.name
                                    this@ActUiController.measureData = data
                                }
                            }

                            addProductBinding.editCount.doAfterTextChanged { countApp->
                                val priceOne = addProductBinding.editSumma.text.toString()
                                if (countApp.toString().isNotEmptyOrNull() && priceOne.isNotEmptyOrNull()){
                                    val price = addProductBinding.editSumma.getNumericValue()
                                    val countSingle = countApp.toString().toDouble()
                                    val priceAll = BigDecimal(countSingle*price)
                                    addProductBinding.editTotalSum.setText(priceAll.toPlainString())
                                }
                            }

                            addProductBinding.editSumma.doAfterTextChanged { price->
                                val countApp = addProductBinding.editCount.text.toString()
                                if (price.toString().isNotEmptyOrNull() && countApp.isNotEmptyOrNull()){
                                    val price = addProductBinding.editSumma.getNumericValue()
                                    val count = countApp.toDouble()
                                    val priceAll = BigDecimal(count*price)
                                    addProductBinding.editTotalSum.setText(priceAll.toPlainString())
                                }
                            }

                            actViewModel.getAllActProduct().observe(homeFragment.viewLifecycleOwner){ listActProduct->
                                  if (listActProduct.isNotEmpty()){
                                    addProductBinding.textCount.visible()
                                      orderNumber = listActProduct.size.toLong()
                                } else {
                                      orderNumber = 0
                                      addProductBinding.textCount.gone()
                                }
                                addProductBinding.textCount.text = if (listActProduct.size>100) "99+" else listActProduct.size.toString()
                            }

                            // save product
                            addProductBinding.btnSaveProduct.setOnClickListener {
                                val name = addProductBinding.editName.text.toString()
                                val count = addProductBinding.editCount.text.toString()
                                val summa = addProductBinding.editSumma.text.toString()
                                val totalSumma = addProductBinding.editTotalSum.text.toString()
                                if (!name.isNotEmptyOrNull()){
                                    mainActivity.motionAnimation("error",mainActivity.getString(R.string.no_act_product_name))
                                } else if (!count.isNotEmptyOrNull() || count.toLong()==0L){
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
                                        val actProductEntity = ActProductEntity(ordno = orderNumberProduct.toInt(), catalogcode = catalogCode.toString(),
                                        catalogname = catalogName.toString(), name = name, measureid = measureId?:0, count = count.toLong(), summa = summa, totalSumma = totalSumma)
                                        actViewModel.saveActProduct(actProductEntity)
                                        mainActivity.motionAnimation("success",mainActivity.getString(R.string.success_add_product))
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



    private fun datePicker(textView:TextView){
        containerApplication.datePicker {
            textView.text = it
        }
    }

    override fun incomingAct() {

    }

    override fun outgoingAct() {

    }

    override fun draftAct() {

    }

    override fun processSendingAct() {

    }

}

