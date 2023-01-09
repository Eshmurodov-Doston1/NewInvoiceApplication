package uz.einvoice.android.utils.dialogHelper

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doAfterTextChanged
import com.google.gson.JsonElement
import uz.einvoice.domain.database.measure.MeasureEntity
import uz.einvoice.domain.models.branchModel.Data
import uz.einvoice.android.R
import uz.einvoice.android.adapters.genericRvAdapter.GenericRvAdapter
import uz.einvoice.android.databinding.DialogBinding
import uz.einvoice.android.databinding.DialogSpinnerBinding
import uz.einvoice.android.databinding.SpinnerDialogBinding
import uz.einvoice.android.presentation.activities.MainActivity
import uz.einvoice.android.utils.appConstant.AppConstant
import uz.einvoice.android.utils.appConstant.AppConstant.DELETE_TYPE
import uz.einvoice.android.utils.appConstant.AppConstant.NO_INTERNET
import uz.einvoice.android.utils.extension.isNotEmptyOrNull
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
                   search.doAfterTextChanged { textSearch ->
                     spinnerAdapter.submitList(isSearch(textSearch.toString(), textSearch.toString().isNotEmptyOrNull(),listData))
                   }
               }
           }
           3->{
               val binding = SpinnerDialogBinding.inflate(activity.layoutInflater)
               create1.setView(binding.root)
               binding.apply {
                   val spinnerAdapter = GenericRvAdapter<T>(R.layout.spinner_item){ data, position, clickType ->
                       onClick.invoke(data)
                       create1.dismiss()
                   }
                   spinnerAdapter.submitList(listData)
                   binding.recyclerForSpinner.adapter = spinnerAdapter
               }
           }
       }
        create1.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        if (!create1.isShowing){
            create1.show()
        }
    }

    fun statusDialog(
        statusDialog: Int,
        message:String,
        onClick: (clickType: Int) -> Unit
    ){
        val binding = DialogBinding.inflate(activity.layoutInflater)
        create.setView(binding.root)
        when(statusDialog){
            0->{
                binding.lottie.setAnimation(R.raw.no_application)
                binding.okBtn.text = activity.getString(R.string.install_e_signed)
            }
            DELETE_TYPE->{
                binding.lottie.setAnimation(R.raw.delete)
                binding.okBtn.text = activity.getString(R.string.delete)
            }
            4->{
                binding.lottie.setAnimation(R.raw.info)
                binding.okBtn.text = activity.getString(R.string.accept)
            }
        }
        binding.message.text = message

        binding.okBtn.setOnClickListener {
            onClick.invoke(1)
            create.dismiss()
        }
        binding.cancel.setOnClickListener {
            onClick.invoke(0)
            create.dismiss()
        }
        create.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        if (!create.isShowing){
            create.show()
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
                } else if (data is uz.einvoice.domain.models.tasNifProduct.Data){
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