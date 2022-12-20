package uz.idea.newinvoiceapplication.presentation.screens.docDataScreen

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import uz.idea.domain.models.documents.actDocument.ActDocument
import uz.idea.domain.models.localeClass.table.ActCreateTable
import uz.idea.domain.utils.loadState.ResponseState
import uz.idea.newinvoiceapplication.R
import uz.idea.newinvoiceapplication.adapters.genericRvAdapter.GenericRvAdapter
import uz.idea.newinvoiceapplication.databinding.FragmentDocumentInfoBinding
import uz.idea.newinvoiceapplication.presentation.screens.baseFragment.BaseFragment
import uz.idea.newinvoiceapplication.utils.appConstant.AppConstant.DOCUMENT_ID
import uz.idea.newinvoiceapplication.utils.appConstant.AppConstant.DOC_STATUS
import uz.idea.newinvoiceapplication.utils.extension.*
import uz.idea.newinvoiceapplication.vm.documnetVm.DocumentViewModel
import java.math.BigDecimal
import java.util.*

@AndroidEntryPoint
class DocumentInfoFragment : BaseFragment<FragmentDocumentInfoBinding>() {
    private var docId:String?=null
    private var docStatus:Int?=-1
    private val documentViewModel:DocumentViewModel by viewModels()
    override fun inflateViewBinding(inflater: LayoutInflater, container: ViewGroup?)
    = FragmentDocumentInfoBinding.inflate(inflater,container,false)
    // buttons

    private val tableAdapter:GenericRvAdapter<ActCreateTable> by lazy {
        GenericRvAdapter(R.layout.item_act_doc){ data, position, clickType ->

        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments.let {
            docId = it?.getString(DOCUMENT_ID)
            docStatus = it?.getInt(DOC_STATUS,0)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun init() {
        binding.apply {
            when(docStatus){
                1->{
                    documentViewModel.getDocument(getLanguage(requireContext()),docId.toString())
                    lifecycleScope.launchWhenCreated {
                        documentViewModel.document.collect { result->
                            when(result){
                                is ResponseState.Loading->{
                                    progress.visible()
                                }
                                is ResponseState.Success->{
                                    progress.gone()
                                    logData(result.data.toString())
                                     val actDocument = result.data?.parseClass(ActDocument::class.java)
                                    includeActCreateDoc.idDoc.text = "ID: ${actDocument?.data?._id}"
                                    includeActCreateDoc.actNumber.text = "${getString(R.string.act)} № ${actDocument?.data?.actdoc?.actno} \n ${getString(R.string.from)} ${getDateFormat(actDocument?.data?.actdoc?.actdate.toString(),requireContext())}"
                                    includeActCreateDoc.contractNumber.text = "${getString(R.string.contract)} № ${actDocument?.data?.contractdoc?.contractno} \n ${getString(R.string.from)} ${getDateFormat(actDocument?.data?.contractdoc?.contractdate.toString(),requireContext())}"
                                    includeActCreateDoc.textAct.text = actDocument?.data?.actdoc?.acttext
                                    mainActivity.supportActionBar?.title = "${requireActivity().getString(R.string.act)} № ${actDocument?.data?.actdoc?.actno} ${requireActivity().getString(R.string.from)} ${actDocument?.data?.actdoc?.actdate}"
                                    tableList(actDocument)
                                }
                                is ResponseState.Error->{
                                    progress.gone()
                                }
                            }
                        }
                    }
                    includeActCreateDoc.rvData.adapter = tableAdapter
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun tableList(actDocument: ActDocument?):List<ActCreateTable>{
        val actCreateTableList = LinkedList<ActCreateTable>()
        actCreateTableList.add(ActCreateTable("№",getString(R.string.act_table_two_text),getString(R.string.name_works),getString(R.string.measure_table),
        getString(R.string.count_table),getString(R.string.summa_product_table),getString(R.string.summa_total_table)))
        var allSumma = BigDecimal(0)
        actDocument?.data?.productlist?.products?.onEach {  product->
            actCreateTableList.add(ActCreateTable(product.ordno,"${product.catalogcode?:""} - ${product.catalogname?:""}",product.name,documentViewModel.getMeasure(product.measureid).name, product.count.toDouble().toString(), product.summa.toDouble().numberFormatter(), product.totalsum.toDouble().numberFormatter()))
            allSumma +=  BigDecimal(product.totalsum.toDouble())
        }
        binding.includeActCreateDoc.totalSumma.text = allSumma.toPlainString()
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
        return actCreateTableList
    }



}