package uz.idea.newinvoiceapplication.presentation.screens.homeScreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import uz.idea.newinvoiceapplication.databinding.FragmentHomeBinding
import uz.idea.newinvoiceapplication.presentation.controllers.actController.ActUiController
import uz.idea.newinvoiceapplication.presentation.screens.baseFragment.BaseFragment
import uz.idea.newinvoiceapplication.utils.appConstant.AppConstant
import uz.idea.newinvoiceapplication.utils.extension.getLanguage
import uz.idea.newinvoiceapplication.utils.extension.gone
import uz.idea.newinvoiceapplication.utils.extension.logData
import uz.idea.newinvoiceapplication.utils.extension.visible
import uz.idea.newinvoiceapplication.utils.language.LocaleManager
import uz.idea.newinvoiceapplication.vm.actVm.ActViewModel
import uz.idea.newinvoiceapplication.vm.mainVM.MainViewModel
import kotlin.math.abs

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>() {
    // act controller
    private lateinit var actUIController: ActUiController
    // act view Model
    private val actViewModel:ActViewModel by viewModels()
    // main view model
    private val mainViewModel:MainViewModel by viewModels()
    private var isCreate = false


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
                    logData(children?.path.toString())
                    when(children?.path){
                        "/acts/add"->{
                            if (!isCreate)  actUiController()
                            binding.includeActCreate.nested.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
                                if (scrollY < oldScrollY) {
                                    mainActivity.bottomBarView(true)
                                }
                                if (scrollY == abs( v.measuredHeight - v.getChildAt(0).measuredHeight)) {
                                    mainActivity.bottomBarView(false)
                                }
                            })
                            actUIController.createAct()
                            if (binding.includeActDraft.consDraft.isVisible) {
                                binding.includeActDraft.consDraft.gone()
                            } else if (binding.includeActIncoming.consIncoming.isVisible){
                                binding.includeActIncoming.consIncoming.gone()
                            } else if (binding.includeActSent.consActSend.isVisible){
                                binding.includeActSent.consActSend.gone()
                            }
                            binding.includeActCreate.consActCreate.visible()
                        }
                        "/acts/draft"->{
                            if (!isCreate)  actUiController()
                            if (binding.includeActCreate.consActCreate.isVisible) {
                                binding.includeActCreate.consActCreate.gone()
                            } else if(binding.includeActIncoming.consIncoming.isVisible){
                                binding.includeActIncoming.consIncoming.gone()
                            } else if (binding.includeActSent.consActSend.isVisible){
                                binding.includeActSent.consActSend.gone()
                            }
                            binding.includeActDraft.consDraft.visible()
                            actUIController.draftAct()
                        }
                        "/acts/receive"->{
                            if (!isCreate) actUiController()
                            if (binding.includeActCreate.consActCreate.isVisible) {
                                binding.includeActCreate.consActCreate.gone()
                            } else if (binding.includeActDraft.consDraft.isVisible){
                                binding.includeActDraft.consDraft.gone()
                            } else if (binding.includeActSent.consActSend.isVisible){
                                binding.includeActSent.consActSend.gone()
                            }
                            binding.includeActIncoming.consIncoming.visible()
                            actUIController.incomingAct()
                        }
                        "/acts/sent"->{
                            if (!isCreate) actUiController()
                            if (binding.includeActCreate.consActCreate.isVisible) {
                                binding.includeActCreate.consActCreate.gone()
                            } else if (binding.includeActDraft.consDraft.isVisible){
                                binding.includeActDraft.consDraft.gone()
                            } else if (binding.includeActIncoming.consIncoming.isVisible){
                                binding.includeActIncoming.consIncoming.gone()
                            }
                            binding.includeActSent.consActSend.visible()
                            actUIController.outgoingAct()
                        }
                    }
                }
            }
        }
    }




    private fun actUiController(){
      try {
          actUIController = ActUiController(
              mainActivity,
              binding,
              actViewModel,
              this)
          isCreate = true
      }catch (e:Exception){
          e.printStackTrace()
      }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        isCreate = false
    }



    override fun inflateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentHomeBinding.inflate(inflater,container,false)
}