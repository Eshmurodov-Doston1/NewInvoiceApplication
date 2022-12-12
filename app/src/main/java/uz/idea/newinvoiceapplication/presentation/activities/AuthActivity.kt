package uz.idea.newinvoiceapplication.presentation.activities

import android.content.Context
import android.graphics.drawable.GradientDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.viewbinding.library.activity.viewBinding
import androidx.core.content.ContextCompat
import dagger.hilt.android.AndroidEntryPoint
import uz.idea.newinvoiceapplication.R
import uz.idea.newinvoiceapplication.databinding.ActivityAuthBinding
import uz.idea.newinvoiceapplication.utils.language.LocaleManager
@AndroidEntryPoint
class AuthActivity : AppCompatActivity() {
    private val binding:ActivityAuthBinding by viewBinding()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
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