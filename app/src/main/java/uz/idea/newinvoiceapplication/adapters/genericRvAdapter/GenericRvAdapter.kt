package uz.idea.newinvoiceapplication.adapters.genericRvAdapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.ListAdapter
import uz.idea.newinvoiceapplication.adapters.GenericDiffUtil
import uz.idea.newinvoiceapplication.adapters.genericViewHolder.GenericViewHolder

class GenericRvAdapter<T>(
    @LayoutRes private val layoutRes:Int,
    private val onClick:(data:T,position:Int,clickType:Int)->Unit
):ListAdapter<T,GenericViewHolder<T>>(GenericDiffUtil<T>()){
    private var clickPosition:Int = 0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenericViewHolder<T> {
        val view = LayoutInflater.from(parent.context).inflate(layoutRes,parent,false)
        return GenericViewHolder(view)
    }

    override fun onBindViewHolder(holder: GenericViewHolder<T>, position: Int) {
        holder.onBind(layoutRes,getItem(position),position,clickPosition,onClick)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun itemClick(clickPos:Int){
     clickPosition = clickPos
     notifyDataSetChanged()
    }
}