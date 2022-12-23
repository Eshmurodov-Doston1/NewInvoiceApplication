package uz.einvoice.android.adapters.genericViewHolder

import androidx.annotation.LayoutRes

interface  Holder<T> {
    fun onBind(
        @LayoutRes layoutRes:Int,
        data:T,
        position:Int,
        clickPos:Int,
        onClick:(data:T,position:Int,clickType:Int)->Unit
    )
}