package uz.idea.newinvoiceapplication.utils.extension

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.navigation.NavOptions
import uz.idea.newinvoiceapplication.R

fun logData(message:String) =  Log.e("E-Invoice Log->", message)

fun <A: Activity> Activity.startNewActivity(activity: Class<A>){
    Intent(this,activity).also {
        it.flags = Intent.FLAG_ACTIVITY_NEW_TASK.or(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(it)
    }
}

fun animationViewCreateRight(): NavOptions {
    return NavOptions.Builder()
        .setEnterAnim(R.anim.slide_in)
        .setExitAnim(R.anim.fade_out)
        .setPopEnterAnim(R.anim.fade_in)
        .setPopExitAnim(R.anim.slide_out)
        .build()
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

