package uz.einvoice.android.presentation.screens.languageScreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import uz.einvoice.android.R
import uz.einvoice.android.databinding.FragmentLanguageBinding
import uz.einvoice.android.presentation.screens.baseFragment.BaseFragment
import uz.einvoice.android.utils.extension.animationViewCreateRight
import uz.einvoice.android.utils.extension.logData
import uz.einvoice.android.utils.language.Language
import uz.einvoice.android.utils.language.LocaleManager

class LanguageFragment : BaseFragment<FragmentLanguageBinding>() {
    override fun init() {
     binding.apply {
         // status bar color
         authActivity.statusBarColor(R.color.secondary_color)
         // navigaiton bar color
         authActivity.navigationBarColor(R.color.primary_color)

         logData(LocaleManager.getLanguage(requireContext()))
         // ru card click
         ruCard.setOnClickListener {
             LocaleManager.setNewLocale(requireContext(),Language.RU.value)
             createLoginScreen()
         }

         // uz card click
         uzCard.setOnClickListener {
             LocaleManager.setNewLocale(requireContext(),Language.UZ.value)
             createLoginScreen()
         }

         // uzc card click
         uzcCard.setOnClickListener {
             LocaleManager.setNewLocale(requireContext(),Language.EN.value)
             createLoginScreen()
         }
     }
    }

    private fun createLoginScreen(){
        findNavController().navigate(R.id.action_languageFragment_to_authFragment,
            Bundle(),
            animationViewCreateRight())
    }

    override fun inflateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentLanguageBinding.inflate(inflater,container,false)
}