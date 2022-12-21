package uz.idea.newinvoiceapplication.presentation.screens.filterScreen

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import uz.idea.newinvoiceapplication.databinding.FragmentFilterBinding
import uz.idea.newinvoiceapplication.presentation.controllers.actController.ActFilterUiController
import uz.idea.newinvoiceapplication.presentation.screens.baseFragment.BaseFragment
import uz.idea.newinvoiceapplication.utils.extension.visible
import uz.idea.newinvoiceapplication.vm.actVm.ActViewModel
import kotlin.math.abs

@AndroidEntryPoint
class FilterFragment : BaseFragment<FragmentFilterBinding>() {
    private val actViewModel:ActViewModel by viewModels()
    override fun inflateViewBinding(inflater: LayoutInflater, container: ViewGroup?)
    = FragmentFilterBinding.inflate(inflater,container,false)
    private var isCreate = false
    private lateinit var actFilterUiController: ActFilterUiController
    override fun init() {
        binding.apply {
            lifecycleScope.launchWhenResumed {
                mainActivity.containerViewModel.children.collect { children->
                    when(children?.path){
                        "/acts/add"->{
                            if (!isCreate) actFilterUiController()
                        }
                        "/acts/draft"->{
                            if (!isCreate) actFilterUiController()
                            binding.incomingActFilter.consIncomingFilter.visible()
                            actFilterUiController.draftAct()
                            binding.incomingActFilter.nested.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
                                if (scrollY < oldScrollY) {
                                    mainActivity.bottomBarView(true)
                                }
                                if (scrollY == abs( v.measuredHeight - v.getChildAt(0).measuredHeight)) {
                                    mainActivity.bottomBarView(false)
                                }
                            })
                        }
                        "/acts/receive"->{
                            if (!isCreate) actFilterUiController()
                        }
                        "/acts/sent"->{
                            if (!isCreate) actFilterUiController()
                        }
                        "/acts/queue"->{
                            if (!isCreate) actFilterUiController()
                        }
                    }
                }
            }
        }
    }

    private fun actFilterUiController(){
        try {
            actFilterUiController = ActFilterUiController(
                mainActivity,
                binding,
                actViewModel,
                this)
            isCreate = true
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

}