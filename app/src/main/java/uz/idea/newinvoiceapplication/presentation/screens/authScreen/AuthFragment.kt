package uz.idea.newinvoiceapplication.presentation.screens.authScreen

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.viewbinding.library.fragment.viewBinding
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.github.razir.progressbutton.attachTextChangeAnimator
import com.github.razir.progressbutton.bindProgressButton
import com.github.razir.progressbutton.hideProgress
import com.github.razir.progressbutton.showProgress
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import org.json.JSONObject
import uz.idea.domain.models.authModel.reqAuth.ReqAuthModel
import uz.idea.domain.models.authModel.resAuth.ResAuthModel
import uz.idea.domain.models.errors.authError.AuthError
import uz.idea.domain.utils.loadState.ResponseState
import uz.idea.newinvoiceapplication.R
import uz.idea.newinvoiceapplication.databinding.DialogBinding
import uz.idea.newinvoiceapplication.databinding.FragmentAuthBinding
import uz.idea.newinvoiceapplication.presentation.activities.AuthActivity
import uz.idea.newinvoiceapplication.presentation.activities.MainActivity
import uz.idea.newinvoiceapplication.presentation.screens.baseFragment.BaseFragment
import uz.idea.newinvoiceapplication.utils.extension.*
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
          //login button click
          loginBtn.setOnClickListener {
              val phoneNumber = phoneNumber.rawText.toString().trim()
              val password = password.text.toString().trim()
              val reqAuthModel = ReqAuthModel(password,"998$phoneNumber")
              loginApplication(reqAuthModel)
          }
      }
    }
    private fun loginApplication(reqAuthModel: ReqAuthModel){
        authViewModel.loginApplication(getLanguage(requireContext()),reqAuthModel)
        lifecycleScope.launchWhenCreated {
            authViewModel.login.collect { result->
                when(result){
                    is ResponseState.Loading->{
                        loadingButton(true)
                    }
                    is ResponseState.Success->{
                        loadingButton(false)
                        val resAuth = result.data?.parseClass(ResAuthModel::class.java)
                        authViewModel.saveAuthResponse(resAuth)
                        authActivity.startNewActivity(MainActivity::class.java)
                        authActivity.finish()
                    }
                    is ResponseState.Error->{
                        loadingButton(false)
                        val errorAuth = JsonParser.parseString(result.exception.localizedMessage).asJsonObject
                       errorDialog(errorAuth,reqAuthModel)
                    }
                }
            }
        }
    }

    private val create by lazy { AlertDialog.Builder(requireContext()).create() }
    private fun errorDialog(errorAuth:JsonElement,reqAuthModel: ReqAuthModel){
        val dialogBinding = DialogBinding.inflate(LayoutInflater.from(context),null,false)
        create.setView(dialogBinding.root)
        var errorMessage = ""
        if (errorAuth.asJsonObject.has("errors")){
            val authError = errorAuth.parseClass(AuthError::class.java)
            if (authError.errors!=null){
                if (authError.errors?.phone!=null){
                    errorMessage +=  authError.errors?.phone!![0] + "\n"
                }
                if (authError.errors?.password!=null){
                    errorMessage += authError.errors?.password!![0]
                }
                dialogBinding.message.text = errorMessage
            }
        }
        dialogBinding.okBtn.setOnClickListener {
            loginApplication(reqAuthModel)
            create.dismiss()
        }
        dialogBinding.cancel.setOnClickListener {
            create.dismiss()
        }
        if (!create.isShowing){
            create.show()
        }
        create.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
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

    private fun loadingButton(isLoading:Boolean){
        if (isLoading){
            bindProgressButton(binding.loginBtn)
            // (Optional) Enable fade In / Fade out animations
            binding.loginBtn.attachTextChangeAnimator()
            // Show progress with "Loading" text
            binding.loginBtn.showProgress {
                buttonTextRes = R.string.loading_text
                progressColor = Color.WHITE
            }
        }else{
            // Hide progress and show "Submit" text instead
            binding.loginBtn.hideProgress(R.string.login_text)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        authActivity = activity as AuthActivity
    }
}