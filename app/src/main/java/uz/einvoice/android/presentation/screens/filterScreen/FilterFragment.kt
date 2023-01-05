package uz.einvoice.android.presentation.screens.filterScreen

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import uz.einvoice.android.R
import uz.einvoice.android.databinding.FragmentFilterBinding
import uz.einvoice.android.presentation.controllers.actController.ActFilterUiController
import uz.einvoice.android.presentation.screens.baseFragment.BaseFragment
import uz.einvoice.android.utils.appConstant.AppConstant
import uz.einvoice.android.utils.appConstant.AppConstant.ACT_ADD
import uz.einvoice.android.utils.extension.gone
import uz.einvoice.android.utils.extension.visible
import uz.einvoice.android.utils.language.LocaleManager
import uz.einvoice.android.vm.actVm.ActViewModel
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

            lifecycleScope.launchWhenCreated {
                mainActivity.containerViewModel.children.collect { children->
                    mainActivity.liveData.observe(viewLifecycleOwner) { data ->
                        when(LocaleManager.getLanguage(requireContext())){
                            AppConstant.UZ -> {
                                mainActivity.supportActionBar?.title = data.title_uz + " | ${children?.title_uz} | ${getString(R.string.search_doc)}"
                            }
                            AppConstant.RU ->{
                                mainActivity.supportActionBar?.title = data.title + " | ${children?.title} | ${getString(R.string.search_doc)}"
                            }
                            AppConstant.EN ->{
                                mainActivity.supportActionBar?.title = data.title_uz + " | ${children?.title_uz} | ${getString(R.string.search_doc)}"
                            }
                            else->{
                                mainActivity.supportActionBar?.title = data.title + " | ${children?.title} | ${getString(R.string.search_doc)}"
                            }
                        }
                    }
                }
            }


            lifecycleScope.launchWhenCreated {
                mainActivity.containerViewModel.children.collect { children->
                    when(children?.path){
                        ACT_ADD->{
                            if (!isCreate) actFilterUiController()
                            binding.documentActFilter.consIncomingFilter.gone()
                           actFilterUiController.createAct()
                        }
                        else ->{
                            if (!isCreate) actFilterUiController()
                            binding.documentActFilter.consIncomingFilter.visible()
                            actFilterUiController.documentViews(children?.path.toString())
                            scrollFilterAct()
                        }
                    }
                }
            }
        }
    }

    private fun scrollFilterAct(){
        binding.documentActFilter.nested.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            if (scrollY < oldScrollY) {
                mainActivity.bottomBarView(true)
            }
            if (scrollY == abs( v.measuredHeight - v.getChildAt(0).measuredHeight)) {
                mainActivity.bottomBarView(false)
            }
        })
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