package uz.idea.newinvoiceapplication.presentation.screens.settingsScreen

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import uz.idea.newinvoiceapplication.R
import uz.idea.newinvoiceapplication.databinding.FragmentSettingsBinding
import uz.idea.newinvoiceapplication.presentation.screens.baseFragment.BaseFragment


class SettingsFragment : BaseFragment<FragmentSettingsBinding>() {
    override fun init() {
     binding.apply {

     }
    }

    override fun inflateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentSettingsBinding.inflate(inflater,container,false)
}