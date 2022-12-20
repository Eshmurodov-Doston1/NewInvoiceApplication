package uz.idea.newinvoiceapplication.adapters.genericPagingAdapter

import android.view.View
import androidx.annotation.LayoutRes
import androidx.viewbinding.ViewBinding

interface HolderPaging {
    fun <T> onBind(
        data:T,
        position:Int,
        @LayoutRes layoutRes:Int,
        onClick:(data:T,position:Int,clickType:Int,viewBinding:ViewBinding)->Unit
    )
}