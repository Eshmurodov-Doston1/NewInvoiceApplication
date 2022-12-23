package uz.einvoice.android.presentation.activities

import android.content.Context
import android.graphics.drawable.GradientDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.viewbinding.library.activity.viewBinding
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import dagger.hilt.android.AndroidEntryPoint
import uz.einvoice.android.databinding.ActivityAuthBinding
import uz.einvoice.android.utils.extension.isNotEmptyOrNull
import uz.einvoice.android.utils.extension.startNewActivity
import uz.einvoice.android.utils.language.LocaleManager
import uz.einvoice.android.vm.authVm.AuthViewModel

@AndroidEntryPoint
class AuthActivity : AppCompatActivity() {
    private val binding:ActivityAuthBinding by viewBinding()
    private val authViewModel:AuthViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        if (authViewModel.getMySharedPreferences().accessToken.isNotEmptyOrNull()){
            startNewActivity(MainActivity::class.java)
            finish()
        }
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(LocaleManager.setLocale(newBase))
    }

    fun statusBarColor(color:Int){
        this.window.statusBarColor = ContextCompat.getColor(this,color)
    }
    fun navigationBarColor(color:Int){
        this.window.navigationBarColor = ContextCompat.getColor(this,color)
    }
    fun drawableColorUpdate(view: View, color:Int){
        val gradientDrawable = GradientDrawable()
        gradientDrawable.shape = GradientDrawable.RECTANGLE
        gradientDrawable.cornerRadius = 20f
        gradientDrawable.setColor(
            ContextCompat.getColor(this, color)
        )
        view.background = gradientDrawable
    }
}