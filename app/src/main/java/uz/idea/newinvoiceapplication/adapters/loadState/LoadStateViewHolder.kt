package uz.idea.newinvoiceapplication.adapters.loadState

import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import uz.idea.newinvoiceapplication.databinding.LoadStateDraftBinding

class LoadStateViewHolder(parent: ViewGroup, itemView: View, retry: () -> Unit): RecyclerView.ViewHolder(itemView) {
    private val binding = LoadStateDraftBinding.bind(itemView)
    private val progress = binding.shimmer
    private val error = binding.errorLinear
    private val retry = binding.btnRetry.also {
        it.setOnClickListener { retry() }
    }

    fun bind(loadState: LoadState) {
        if (loadState is LoadState.Error) {
            binding.errorText.text = loadState.error.localizedMessage
        }

        progress.isVisible = loadState is LoadState.Loading
        retry.isVisible = loadState is LoadState.Error
        binding.errorLinear.isVisible = loadState is LoadState.Error
    }
}