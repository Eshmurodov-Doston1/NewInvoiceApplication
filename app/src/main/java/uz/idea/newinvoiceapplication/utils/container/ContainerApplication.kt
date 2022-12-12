package uz.idea.newinvoiceapplication.utils.container

import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import com.google.gson.JsonElement
import uz.idea.newinvoiceapplication.presentation.activities.MainActivity
import uz.idea.newinvoiceapplication.utils.dialogHelper.DialogHelper
import uz.idea.newinvoiceapplication.utils.screenNavigate.ScreenNavigate

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
}