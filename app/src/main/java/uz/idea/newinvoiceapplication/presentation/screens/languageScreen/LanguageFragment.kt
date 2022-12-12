package uz.idea.newinvoiceapplication.presentation.screens.languageScreen

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import uz.idea.newinvoiceapplication.R
import uz.idea.newinvoiceapplication.databinding.FragmentLanguageBinding
import uz.idea.newinvoiceapplication.presentation.activities.AuthActivity
import uz.idea.newinvoiceapplication.presentation.screens.baseFragment.BaseFragment
import uz.idea.newinvoiceapplication.utils.extension.animationViewCreateRight
import uz.idea.newinvoiceapplication.utils.extension.logData
import uz.idea.newinvoiceapplication.utils.language.Language
import uz.idea.newinvoiceapplication.utils.language.LocaleManager
import java.util.Locale

class LanguageFragment : BaseFragment<FragmentLanguageBinding>() {
    private lateinit var authActivity:AuthActivity
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

    override fun onAttach(context: Context) {
        super.onAttach(context)
        authActivity = activity as AuthActivity
    }

    override fun inflateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentLanguageBinding.inflate(inflater,container,false)
}