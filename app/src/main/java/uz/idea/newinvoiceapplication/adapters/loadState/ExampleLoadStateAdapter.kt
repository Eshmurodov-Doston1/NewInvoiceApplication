package uz.idea.newinvoiceapplication.adapters.loadState

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter

class ExampleLoadStateAdapter(
    @LayoutRes private val layoutRes:Int,
    private val retry: () -> Unit
): LoadStateAdapter<LoadStateViewHolder>()  {
    override fun onBindViewHolder(holder: LoadStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoadStateViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(layoutRes, parent, false)
        return LoadStateViewHolder(parent, view,retry)
    }
}