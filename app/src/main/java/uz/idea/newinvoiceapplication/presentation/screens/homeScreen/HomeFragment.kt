package uz.idea.newinvoiceapplication.presentation.screens.homeScreen

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import uz.idea.newinvoiceapplication.databinding.FragmentHomeBinding
import uz.idea.newinvoiceapplication.presentation.controllers.actController.ActUiController
import uz.idea.newinvoiceapplication.presentation.screens.baseFragment.BaseFragment
import uz.idea.newinvoiceapplication.utils.extension.getLanguage
import uz.idea.newinvoiceapplication.vm.actVm.ActViewModel
import uz.idea.newinvoiceapplication.vm.mainVM.MainViewModel

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>() {
    // act controller
    private lateinit var actUIController: ActUiController
    // act view Model
    private val actViewModel:ActViewModel by viewModels()
    // main view model
    private val mainViewModel:MainViewModel by viewModels()

    override fun init() {
        binding.apply {
            mainViewModel.getUserData(getLanguage(requireContext()))

             lifecycleScope.launchWhenCreated {
                 mainActivity.containerViewModel.children.collect { children->
                     when(children?.path){
                         "/acts/add"->{
                             actUiController()
                         }
                     }
                 }
             }
        }
    }

    private fun actUiController(){
        actUIController = ActUiController(
            mainActivity,
            binding.includeActCreate,
            mainActivity.containerApplication,
            mainActivity.containerViewModel,
            actViewModel,
            this)
        actUIController.createAct()
    }



    override fun inflateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentHomeBinding.inflate(inflater,container,false)
}