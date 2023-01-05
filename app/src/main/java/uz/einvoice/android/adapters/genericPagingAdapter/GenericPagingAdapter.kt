package uz.einvoice.android.adapters.genericPagingAdapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.paging.PagingDataAdapter
import androidx.viewbinding.ViewBinding
import uz.einvoice.android.adapters.GenericDiffUtil

class  GenericPagingAdapter<T:Any>(
    @LayoutRes private val layoutRes:Int,
    private val type:String,
    private val onClick:(data:T?,position:Int,clickType:Int,viewBinding: ViewBinding)->Unit
):PagingDataAdapter<T,PagingViewHolder>(GenericDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagingViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(layoutRes,parent,false)
        return PagingViewHolder(view)
    }

    override fun onBindViewHolder(holder: PagingViewHolder, position: Int) {
        holder.onBind(getItem(position),position,layoutRes,type,onClick)
    }

}