package uz.idea.newinvoiceapplication.presentation.screens.homeScreen

import android.view.LayoutInflater
import android.view.ViewGroup
import dagger.hilt.android.AndroidEntryPoint
import uz.idea.newinvoiceapplication.databinding.FragmentHomeBinding
import uz.idea.newinvoiceapplication.presentation.screens.baseFragment.BaseFragment
import uz.idea.newinvoiceapplication.utils.appConstant.AppConstant.EN
import uz.idea.newinvoiceapplication.utils.appConstant.AppConstant.RU
import uz.idea.newinvoiceapplication.utils.appConstant.AppConstant.UZ
import uz.idea.newinvoiceapplication.utils.language.LocaleManager

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>() {
    override fun init() {
        binding.apply {
            mainActivity.clickMenu().observe(viewLifecycleOwner){ data->
               mainActivity.supportActionBar?.title = when(LocaleManager.getLanguage(requireContext())){
                   UZ->data.title_uz
                   RU->data.title
                   EN->data.title_uz
                   else -> data.title
               }
            }
        }
    }

    override fun inflateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentHomeBinding.inflate(inflater,container,false)
}