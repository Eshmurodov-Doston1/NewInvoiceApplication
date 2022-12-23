package uz.einvoice.android.presentation.screens.settingsScreen

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import uz.einvoice.domain.utils.loadState.ResponseState
import uz.einvoice.android.databinding.FragmentSettingsBinding
import uz.einvoice.android.presentation.activities.AuthActivity
import uz.einvoice.android.presentation.screens.baseFragment.BaseFragment
import uz.einvoice.android.utils.extension.getLanguage
import uz.einvoice.android.vm.settingsViewModel.SettingsViewModel

@AndroidEntryPoint
class SettingsFragment : BaseFragment<FragmentSettingsBinding>() {
    private val settingsViewModel:SettingsViewModel by viewModels()
    override fun init() {
     binding.apply {
         logOut.setOnClickListener {
             settingsViewModel.logOut(getLanguage(requireContext()))
             lifecycleScope.launchWhenCreated {
                 settingsViewModel.logOut.collect { result->
                     when(result){
                         is ResponseState.Loading->{

                         }
                         is ResponseState.Success->{
                             startActivity(Intent(requireActivity(),AuthActivity::class.java))
                             settingsViewModel.clearShared()
                             requireActivity().finish()
                         }
                         is ResponseState.Error->{

                         }
                     }
                 }
             }
         }
     }
    }

    override fun inflateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentSettingsBinding.inflate(inflater,container,false)
}