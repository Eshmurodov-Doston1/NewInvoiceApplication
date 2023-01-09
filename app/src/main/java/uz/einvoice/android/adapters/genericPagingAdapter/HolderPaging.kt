package uz.einvoice.android.adapters.genericPagingAdapter

import androidx.annotation.LayoutRes
import androidx.viewbinding.ViewBinding

interface HolderPaging {
    fun <T> onBind(
        data:T,
        position:Int,
        @LayoutRes layoutRes:Int,
        type:String,
        onClick:(data:T,position:Int,clickType:Int,viewBinding:ViewBinding,type:String)->Unit
    )
}