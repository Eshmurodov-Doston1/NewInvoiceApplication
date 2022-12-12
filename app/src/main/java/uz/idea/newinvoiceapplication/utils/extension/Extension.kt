package uz.idea.newinvoiceapplication.utils.extension

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.navigation.NavOptions
import com.google.gson.Gson
import com.google.gson.JsonElement
import org.json.JSONObject
import uz.idea.newinvoiceapplication.R
import uz.idea.newinvoiceapplication.utils.AppConstant.EN
import uz.idea.newinvoiceapplication.utils.AppConstant.RU
import uz.idea.newinvoiceapplication.utils.AppConstant.UZ
import uz.idea.newinvoiceapplication.utils.language.LocaleManager

fun logData(message:String) =  Log.e("E-Invoice Log->", message)

fun <A: Activity> Activity.startNewActivity(activity: Class<A>){
    Intent(this,activity).also {
        it.flags = Intent.FLAG_ACTIVITY_NEW_TASK.or(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(it)
    }
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

