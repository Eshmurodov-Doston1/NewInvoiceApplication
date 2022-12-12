package uz.idea.newinvoiceapplication.presentation.screens.authScreen

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.viewbinding.library.fragment.viewBinding
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import uz.idea.newinvoiceapplication.R
import uz.idea.newinvoiceapplication.databinding.FragmentAuthBinding
import uz.idea.newinvoiceapplication.presentation.activities.AuthActivity
import uz.idea.newinvoiceapplication.presentation.screens.baseFragment.BaseFragment
import uz.idea.newinvoiceapplication.utils.extension.enabled
import uz.idea.newinvoiceapplication.utils.extension.enabledFalse
import uz.idea.newinvoiceapplication.utils.extension.logData
import uz.idea.newinvoiceapplication.vm.authVm.AuthViewModel

@AndroidEntryPoint
class AuthFragment : BaseFragment<FragmentAuthBinding>() {
    private val authViewModel:AuthViewModel by viewModels()
    private var isPhone:Boolean?=false
    private var isPassword:Boolean?=false
    private lateinit var authActivity: AuthActivity
    override fun init() {
      binding.apply {
          // status bar color
          authActivity.statusBarColor(R.color.white)
          // navigaiton bar color
          authActivity.navigationBarColor(R.color.white)

          phoneNumber.doAfterTextChanged {
              logData(phoneNumber.rawText.length.toString())
              if (phoneNumber.rawText.length==9){
                  password.requestFocus()
                  isPhone = true
              }else{
                  isPhone = false
              }
              buttonEnabled(isPhone,isPassword)
          }

          password.addTextChangedListener {
              isPassword = it.toString().trim().length>=6
              buttonEnabled(isPhone,isPassword)
          }
      }
    }

    private fun buttonEnabled(isPhone:Boolean?,isPassword:Boolean?){
        if (isPhone == true && isPassword == true){
            authActivity.drawableColorUpdate(binding.loginBtn,R.color.primary_color)
            binding.loginBtn.enabled()
        }else{
            authActivity.drawableColorUpdate(binding.loginBtn,R.color.primary_color_hint)
            binding.loginBtn.enabledFalse()
        }
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        authActivity = activity as AuthActivity
    }
}