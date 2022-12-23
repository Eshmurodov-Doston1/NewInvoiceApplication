package uz.einvoice.android.presentation.screens.homeScreen

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import uz.einvoice.android.databinding.FragmentHomeBinding
import uz.einvoice.android.presentation.controllers.actController.ActUiController
import uz.einvoice.android.presentation.screens.baseFragment.BaseFragment
import uz.einvoice.android.utils.appConstant.AppConstant
import uz.einvoice.android.utils.extension.getLanguage
import uz.einvoice.android.utils.extension.gone
import uz.einvoice.android.utils.extension.logData
import uz.einvoice.android.utils.extension.visible
import uz.einvoice.android.utils.language.LocaleManager
import uz.einvoice.android.vm.actVm.ActViewModel
import uz.einvoice.android.vm.mainVM.MainViewModel
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