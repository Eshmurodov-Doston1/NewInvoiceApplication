package uz.einvoice.android.presentation.screens.authScreen

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
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
import uz.einvoice.domain.models.authModel.reqAuth.ReqAuthModel
import uz.einvoice.domain.models.authModel.resAuth.ResAuthModel
import uz.einvoice.domain.models.errors.authError.AuthError
import uz.einvoice.domain.utils.loadState.ResponseState
import uz.einvoice.android.R
import uz.einvoice.android.databinding.DialogBinding
import uz.einvoice.android.databinding.FragmentAuthBinding
import uz.einvoice.android.presentation.activities.MainActivity
import uz.einvoice.android.presentation.screens.baseFragment.BaseFragment
import uz.einvoice.android.utils.extension.*
import uz.einvoice.android.vm.authVm.AuthViewModel

@AndroidEntryPoint
class AuthFragment : BaseFragment<FragmentAuthBinding>() {
    private val authViewModel:AuthViewModel by viewModels()
    private var isPhone:Boolean?=false
    private var isPassword:Boolean?=false
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
                        if (result.exception.localizedMessage.isNotEmptyOrNull()){
                            val errorAuth = JsonParser.parseString(result.exception.localizedMessage).asJsonObject
                            errorDialog(errorAuth,reqAuthModel)
                        }else{
                            noInternetDialog(reqAuthModel)
                        }
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
            if (errorAuth.asJsonObject.has("message")){
                dialogBinding.message.text = errorAuth.asJsonObject.get("message").toString()
            } else {
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

    private fun noInternetDialog(reqAuthModel:ReqAuthModel){
        val create = AlertDialog.Builder(requireContext()).create()
        val dialogBinding = DialogBinding.inflate(LayoutInflater.from(context),null,false)
        create.setView(dialogBinding.root)
        dialogBinding.apply {
            lottie.setAnimation(R.raw.no_internet)
            message.text = getString(R.string.no_internet)
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


    override fun inflateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentAuthBinding.inflate(inflater,container,false)
}