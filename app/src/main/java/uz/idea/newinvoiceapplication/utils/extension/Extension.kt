package uz.idea.newinvoiceapplication.utils.extension

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.navigation.NavOptions
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.JsonElement
import uz.idea.newinvoiceapplication.R
import uz.idea.newinvoiceapplication.utils.appConstant.AppConstant.EN
import uz.idea.newinvoiceapplication.utils.appConstant.AppConstant.RU
import uz.idea.newinvoiceapplication.utils.appConstant.AppConstant.UZ
import uz.idea.newinvoiceapplication.utils.language.LocaleManager

fun logData(message:String) =  Log.e("E-Invoice Log->", message)

fun <A: Activity> Activity.startNewActivity(activity: Class<A>){
    Intent(this,activity).also {
        it.flags = Intent.FLAG_ACTIVITY_NEW_TASK.or(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(it)
    }
}

fun ImageView.imageData(url:String, context:Context){
    val circularProgressDrawable = CircularProgressDrawable(context)
    circularProgressDrawable.strokeWidth = 5f
    circularProgressDrawable.centerRadius = 30f
    circularProgressDrawable.start()
    circularProgressDrawable.setColorSchemeColors(ContextCompat.getColor(context, R.color.primary_color))
    Glide.with(context)
        .load(url)
        .placeholder(circularProgressDrawable)
        .into(this)
}


fun String?.isNotEmptyOrNull():Boolean{
    return this != null && this.isNotEmpty() && this != ""
}


fun animationViewCreateRight(): NavOptions {
    return NavOptions.Builder()
        .setEnterAnim(R.anim.slide_in)
        .setExitAnim(R.anim.fade_out)
        .setPopEnterAnim(R.anim.fade_in)
        .setPopExitAnim(R.anim.slide_out)
        .build()
}
fun <T> JsonElement.parseClass(classData:Class<T>):T{
    val gson = Gson()
    return gson.fromJson(this,classData)
}
fun getLanguage(context: Context):String{
    val language = LocaleManager.getLanguage(context)
    return when(language){
        UZ-> UZ
        RU-> RU
        EN-> "uzk"
        else -> RU
    }
}

fun View.visible(){
    this.isVisible = true
}
fun View.gone(){
    this.isVisible = false
}

fun View.enabled(){
    this.isEnabled = true
}
fun View.enabledFalse(){
    this.isEnabled = false
}

