package uz.idea.newinvoiceapplication.presentation.screens.settingsScreen

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.qamar.curvedbottomnaviagtion.log
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import uz.idea.domain.utils.loadState.ResponseState
import uz.idea.newinvoiceapplication.R
import uz.idea.newinvoiceapplication.databinding.FragmentSettingsBinding
import uz.idea.newinvoiceapplication.presentation.activities.AuthActivity
import uz.idea.newinvoiceapplication.presentation.screens.baseFragment.BaseFragment
import uz.idea.newinvoiceapplication.utils.extension.getLanguage
import uz.idea.newinvoiceapplication.vm.settingsViewModel.SettingsViewModel

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