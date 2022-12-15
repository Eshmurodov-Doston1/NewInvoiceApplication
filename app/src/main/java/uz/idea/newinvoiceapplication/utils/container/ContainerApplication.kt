package uz.idea.newinvoiceapplication.utils.container

import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.gson.JsonElement
import uz.idea.newinvoiceapplication.presentation.activities.MainActivity
import uz.idea.newinvoiceapplication.utils.dialogHelper.DialogHelper
import uz.idea.newinvoiceapplication.utils.extension.logData
import uz.idea.newinvoiceapplication.utils.screenNavigate.ScreenNavigate
import java.text.SimpleDateFormat
import java.util.*

class ContainerApplication(
    private val owner: LifecycleOwner,
    private val activityMain: MainActivity,
    private val navController: NavController
) {
    private val dialogHelper by lazy {
        DialogHelper(activityMain)
    }

    val screenNavigate by lazy {
        ScreenNavigate(navController)
    }

    fun dialogData(
        status:Int,
        message:JsonElement?,
        onClick:(clickType:Int)->Unit
    ){
        dialogHelper.dialog(status,message,onClick)
    }

    fun <T> applicationDialog(
        status: Int,
        listData:List<T>,
        onClick:(data:T)->Unit
    ){
        dialogHelper.dialogHelper(status,listData,onClick)
    }

    fun datePicker(onClick: (time:String) -> Unit){
        val calendar = Calendar.getInstance(TimeZone.getDefault())
        calendar.clear()
        val today = MaterialDatePicker.todayInUtcMilliseconds()
        val datePicker = MaterialDatePicker.Builder.datePicker()
        datePicker.setTitleText("Date picker")
        datePicker.setSelection(today)
        val materialDatePicker = datePicker.build()
        materialDatePicker.show(activityMain.supportFragmentManager,"DATE_PICKER")
        materialDatePicker.addOnPositiveButtonClickListener {
            calendar.timeInMillis = it
            val simpleDateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
            onClick.invoke(simpleDateFormat.format(calendar.time))
        }
    }
}