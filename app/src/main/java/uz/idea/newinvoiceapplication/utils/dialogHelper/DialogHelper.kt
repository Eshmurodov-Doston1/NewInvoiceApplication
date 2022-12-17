package uz.idea.newinvoiceapplication.utils.dialogHelper

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doAfterTextChanged
import com.google.gson.JsonElement
import uz.idea.domain.database.measure.MeasureEntity
import uz.idea.domain.models.branchModel.Data
import uz.idea.newinvoiceapplication.R
import uz.idea.newinvoiceapplication.adapters.genericRvAdapter.GenericRvAdapter
import uz.idea.newinvoiceapplication.databinding.DialogBinding
import uz.idea.newinvoiceapplication.databinding.DialogSpinnerBinding
import uz.idea.newinvoiceapplication.presentation.activities.MainActivity
import uz.idea.newinvoiceapplication.utils.appConstant.AppConstant.NO_INTERNET
import uz.idea.newinvoiceapplication.utils.extension.isNotEmptyOrNull
import uz.idea.newinvoiceapplication.utils.extension.logData
import java.util.LinkedList

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
        binding.okBtn.setOnClickListener {
            onClick(1)
            create.dismiss()
        }
        binding.cancel.setOnClickListener {
            onClick(0)
            create.dismiss()}
        if (!create.isShowing){
            create.show()
        }
        create.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }



    fun <T> dialogHelper(
        statusDialog:Int,
        listData:List<T>,
        onClick: (data: T) -> Unit
    ){
        val alertDialog = AlertDialog.Builder(activity)
        val create1 = alertDialog.create()
       when(statusDialog){
           in 0..2->{
               val binding = DialogSpinnerBinding.inflate(activity.layoutInflater)
               create1.setView(binding.root)
               binding.apply {
                   val spinnerAdapter = GenericRvAdapter<T>(R.layout.spinner_double_item){ data, position, clickType ->
                       onClick.invoke(data)
                       create1.dismiss()
                   }
                   spinnerAdapter.submitList(listData)
                   binding.recyclerForSpinner.adapter = spinnerAdapter
                   spinnerAdapter.submitList(listData)
                   search.doAfterTextChanged { textSearch ->
                     spinnerAdapter.submitList(isSearch(textSearch.toString(), textSearch.toString().isNotEmptyOrNull(),listData))
                   }
               }
           }
       }
        create1.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        if (!create1.isShowing){
            create1.show()
        }
    }


    fun <T> isSearch(textSearch:String?,isSearch:Boolean,listData:List<T>):List<T>{
        val listBranchFilter = LinkedList<T>()
        if (isSearch){
            listData.forEach { data->
                if (data is Data){
                    if (data.branchName.lowercase().contains(textSearch?.trim()?.lowercase().toString()) ||
                        data.branchNum.lowercase().contains(textSearch?.trim()?.lowercase().toString())){
                        listBranchFilter.add(data)
                    }
                } else if (data is uz.idea.domain.models.tasNifProduct.Data){
                    if (data.mxikFullName.lowercase().contains(textSearch?.lowercase() ?: "") ||
                        data.mxikCode.lowercase().contains(textSearch?.lowercase().toString())){
                        listBranchFilter.add(data)
                    }
                } else if (data is MeasureEntity){
                    if (data.name.lowercase().contains(textSearch.toString())) {
                        listBranchFilter.add(data)
                    }
                }
            }
            return listBranchFilter
        }
        return listData
    }
}