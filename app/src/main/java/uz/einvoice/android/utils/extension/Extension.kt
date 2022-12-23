package uz.einvoice.android.utils.extension

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.navigation.NavOptions
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.JsonElement
import uz.einvoice.domain.models.menuModel.Children
import uz.einvoice.android.R
import uz.einvoice.android.utils.appConstant.AppConstant.EN
import uz.einvoice.android.utils.appConstant.AppConstant.RU
import uz.einvoice.android.utils.appConstant.AppConstant.UZ
import uz.einvoice.android.utils.language.LocaleManager
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*

fun logData(message:String) =  Log.e("E_Invoice_Log->", message)

fun <A: Activity> Activity.startNewActivity(activity: Class<A>){
    Intent(this,activity).also {
        it.flags = Intent.FLAG_ACTIVITY_NEW_TASK.or(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(it)
    }
}

fun PackageManager.getPackageInfoCompat(packageName: String, flags: Int = 0): PackageInfo =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(flags.toLong()))
    } else {
        @Suppress("DEPRECATION") getPackageInfo(packageName, flags)
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


fun <T> EditText.fetchTextChanges(
    dataField: T?,
    changed: (T?) -> Unit
) {
    if (dataField != null) {
        setText(dataField.toString()) //if data is not empty
    } else {
        clear()
    }
    addTextChangedListener { editableText ->
        if (editableText != null || editableText.toString() != "") {
            changed.invoke(editableText.toString() as T)
        } else {
            changed.invoke(null)
        }
    }
}
fun EditText.clear() {
    this.setText("")
}

fun formatterApp(a: String?): BigDecimal {
    return BigDecimal(a).setScale(2, RoundingMode.DOWN)
}



fun String?.isNotEmptyOrNull():Boolean{
    return this != null && this.isNotEmpty() && this != ""
}

fun String.getJuridicOrPhysical():Boolean{
    return this.substring(0,1) == "4" || this.substring(0,1) == "5" || this.substring(0,1) == "6"
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

fun <T> String.parseClass(classData:Class<T>):T{
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

fun slideDown(view: View) {
    view.animate()
        .translationY(view.height.toFloat())
        .alpha(0f)
        .setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                // superfluous restoration
                view.visibility = View.GONE
                view.alpha = 1f
                view.translationY = 0f
            }
        })
}


fun getStatus(status:String,context: Context):Int{
    logData(status)
    return when(status.trim().lowercase()){
        context.getString(R.string.all_status).trim().lowercase() ->{
            -1
        }
        context.getString(R.string.created).trim().lowercase() ->{
            0
        }
        context.getString(R.string.partner_signature).trim().lowercase() ->{
            15
        }
        context.getString(R.string.cancelled_by_send).trim().lowercase()->{
            17
        }
        context.getString(R.string.rejected_by_partner).trim().lowercase() ->{
            20
        }
        context.getString(R.string.accepted_by_partner).trim().lowercase()->{
            30
        }
        context.getString(R.string.process_of_sending).trim().lowercase()->{
            1
        }
        context.getString(R.string.error_while_settings).trim().lowercase()->{
            2
        }
        else ->{
            -1
        }
    }
}

/**
 *
 * const list = {
draft: [
{
stateid: 0,
label: 'Создано'
}
],
send_receive: [
{
stateid: 15,
label: 'ожидает подписи партнёра'
},
{
stateid: 17,
label: 'отменено отправителем'
},
{
stateid: 20,
label: 'отклонено партнёром'
},
{
stateid: 30,
label: 'принято партнёром'
}
],
queue: [
{
stateid: 1,
label: 'в процесс отправки'
},
{
stateid: 2,
label: 'ошибка при отправки'
}
]
}

 * */


fun Double.numberFormatter():String{
    val fmt = DecimalFormat()
    val fmts = DecimalFormatSymbols()
    fmts.groupingSeparator = ' '
    fmt.decimalFormatSymbols = fmts
    return fmt.format(this)
}

fun getDateFormat(str:String,context: Context):String{
    var monthTv = context.getString(R.string.january)
    val month = str.substring(5, 7).toInt()
    when(month){
        1-> monthTv = context.getString(R.string.january)
        2-> monthTv = context.getString(R.string.february)
        3-> monthTv = context.getString(R.string.march)
        4-> monthTv = context.getString(R.string.april)
        5-> monthTv = context.getString(R.string.may)
        6-> monthTv = context.getString(R.string.june)
        7-> monthTv = context.getString(R.string.july)
        8-> monthTv = context.getString(R.string.august)
        9-> monthTv = context.getString(R.string.september)
        10-> monthTv = context.getString(R.string.october)
        11-> monthTv = context.getString(R.string.november)
        12-> monthTv = context.getString(R.string.december)
    }
    val year = str.substring(0, 4)
    val day = str.substring(8)
    return "$day $monthTv $year"
}


fun <T> TextView.textData(lang:String,data:T){
    if (data is Children){
        this.text = when(lang){
            UZ->{
                data.title_uz
            }
            RU->{
                data.title
            }
            EN->{
                data.title_uz
            }
            else->   data.title
        }
    }
}


fun slideUp(view: View) {
    view.visibility = View.VISIBLE
    view.alpha = 0f
    if (view.height > 0) {
        slideUpNow(view)
    } else {
        // wait till height is measured
        view.post { slideUpNow(view) }
    }
}

private fun slideUpNow(view: View) {
    view.translationY = view.height.toFloat()
    view.animate()
        .translationY(0f)
        .alpha(1f)
        .setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                view.visibility = View.VISIBLE
                view.alpha = 1f
            }
        })
}

