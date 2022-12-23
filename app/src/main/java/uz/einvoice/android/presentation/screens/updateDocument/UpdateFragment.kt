package uz.einvoice.android.presentation.screens.updateDocument

import android.content.Context
import android.os.Bundle
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import uz.einvoice.android.R
import uz.einvoice.android.databinding.FragmentUpdateBinding
import uz.einvoice.android.presentation.activities.MainActivity
import uz.einvoice.android.presentation.controllers.documentUpdateController.DocumentUpdateController
import uz.einvoice.android.utils.appConstant.AppConstant
import uz.einvoice.android.utils.appConstant.AppConstant.DOCUMENT_ID_APP
import uz.einvoice.android.vm.actVm.ActViewModel
import uz.einvoice.android.vm.documnetVm.DocumentViewModel

@AndroidEntryPoint
class UpdateFragment : BottomSheetDialogFragment(R.layout.fragment_update) {
    private var documentId:Int?=0
    private var docId:String?=""
    private val binding:FragmentUpdateBinding by viewBinding()
    // act viewModel
    private val actViewModel:ActViewModel by viewModels()
    // doc viewModel
    private val documentViewModel:DocumentViewModel by viewModels()
    private lateinit var documentUpdateController:DocumentUpdateController
    private lateinit var mainActivity: MainActivity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments.let {
            documentId = it?.getInt(DOCUMENT_ID_APP,0)
            docId = it?.getString(AppConstant.DOCUMENT_ID)
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            when(documentId){
                1->{
                    // act update
                    updateController()
                    documentUpdateController.actDocumentUpdate()
                }
            }
        }
    }


    private fun updateController(){
     documentUpdateController = DocumentUpdateController(mainActivity,binding,actViewModel,documentViewModel,this,docId)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = activity as MainActivity
    }



}