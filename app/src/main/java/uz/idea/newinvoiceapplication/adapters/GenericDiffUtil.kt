package uz.idea.newinvoiceapplication.adapters

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil

class GenericDiffUtil<T>:DiffUtil.ItemCallback<T>() {
    override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
        return oldItem.toString() == newItem.toString()
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
        return oldItem.toString() === newItem.toString()
    }
}