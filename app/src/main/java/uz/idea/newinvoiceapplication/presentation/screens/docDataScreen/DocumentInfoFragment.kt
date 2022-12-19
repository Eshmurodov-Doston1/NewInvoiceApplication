package uz.idea.newinvoiceapplication.presentation.screens.docDataScreen

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import uz.idea.newinvoiceapplication.R
import uz.idea.newinvoiceapplication.databinding.FragmentDocumentInfoBinding
import uz.idea.newinvoiceapplication.presentation.screens.baseFragment.BaseFragment
import uz.idea.newinvoiceapplication.utils.appConstant.AppConstant.DOCUMENT_ID
import uz.idea.newinvoiceapplication.utils.appConstant.AppConstant.DOC_STATUS
import uz.idea.newinvoiceapplication.vm.documnetVm.DocumentViewModel

@AndroidEntryPoint
class DocumentInfoFragment : BaseFragment<FragmentDocumentInfoBinding>() {
    private var docId:String?=null
    private var docStatus:Int?=0
    private val documentViewModel:DocumentViewModel by viewModels()
    override fun inflateViewBinding(inflater: LayoutInflater, container: ViewGroup?)
    = FragmentDocumentInfoBinding.inflate(inflater,container,false)
    // buttons


    override fun onStart() {
        super.onStart()
        arguments.let {
           docId = it?.getString(DOCUMENT_ID)
           docStatus = it?.getInt(DOC_STATUS)
        }
    }

    override fun init() {
        binding.apply {
            when(docStatus){
                1->{

                }
            }
        }
    }
}