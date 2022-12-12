package uz.idea.newinvoiceapplication.utils.dialogHelper

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AlertDialog
import com.google.gson.JsonElement
import uz.idea.newinvoiceapplication.R
import uz.idea.newinvoiceapplication.databinding.DialogBinding
import uz.idea.newinvoiceapplication.presentation.activities.MainActivity
import uz.idea.newinvoiceapplication.utils.appConstant.AppConstant.NO_INTERNET

class DialogHelper(
    private val activity: MainActivity
) {
    private val alertDialog = AlertDialog.Builder(activity)
    private val create = alertDialog.create()
    fun dialog(
        status:Int,
        message: JsonElement?,
        onClick:(clickType:Int)->Unit
    ){
        val binding = DialogBinding.inflate(activity.layoutInflater)
        create.setView(binding.root)
        when(status){
            NO_INTERNET->{
                binding.message.text = activity.getString(R.string.no_internet)
                binding.lottie.setAnimation(R.raw.no_internet)
            }
            else->{
                binding.message.text = message.toString()
            }
        }
        binding.okBtn.setOnClickListener { onClick(1) }
        binding.cancel.setOnClickListener { onClick(0) }
        if (!create.isShowing){
            create.show()
        }
        create.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }
}