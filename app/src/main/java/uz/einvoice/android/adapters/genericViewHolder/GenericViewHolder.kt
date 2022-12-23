package uz.einvoice.android.adapters.genericViewHolder

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import uz.einvoice.domain.database.actProductEntity.ActProductEntity
import uz.einvoice.domain.database.measure.MeasureEntity
import uz.einvoice.domain.models.documents.actDocument.Product
import uz.einvoice.domain.models.localeClass.table.ActCreateTable
import uz.einvoice.domain.models.menuModel.Children
import uz.einvoice.domain.models.menuModel.Data
import uz.einvoice.android.BuildConfig.BASE_URL
import uz.einvoice.android.R
import uz.einvoice.android.databinding.ItemActDocBinding
import uz.einvoice.android.databinding.ItemBottomsheetDataBinding
import uz.einvoice.android.databinding.ItemIdBinding
import uz.einvoice.android.databinding.ItemMenuBinding
import uz.einvoice.android.databinding.RecyclerActProductItemBinding
import uz.einvoice.android.databinding.SpinnerDoubleItemBinding
import uz.einvoice.android.databinding.SpinnerItemBinding
import uz.einvoice.android.utils.appConstant.AppConstant.DEFAULT_CLICK_TYPE
import uz.einvoice.android.utils.appConstant.AppConstant.DELETE_CLICK
import uz.einvoice.android.utils.appConstant.AppConstant.EDITE_CLICK
import uz.einvoice.android.utils.extension.getLanguage
import uz.einvoice.android.utils.extension.gone

class GenericViewHolder<T>(private val itemView:View):RecyclerView.ViewHolder(itemView),Holder<T>{
    override fun onBind(
        layoutRes: Int,
        data: T,
        position: Int,
        clickPos:Int,
        onClick: (data: T, position: Int, clickType: Int) -> Unit
    ) {
        when(layoutRes){
            R.layout.item_menu->{
                itemMenu(itemView,data,position,clickPos,onClick)
            }
            R.layout.item_bottomsheet_data->{
                bottomSheetItem(itemView,data,position,onClick)
            }
            R.layout.spinner_double_item->{
                spinnerDouble(itemView,data,position,onClick)
            }
            R.layout.recycler_act_product_item->{
                recyclerActProductItem(itemView,data,position,onClick)
            }
            R.layout.item_act_doc->{
                itemActDoc(itemView,data,position,onClick)
            }
            R.layout.spinner_item->{
                spinnerItem(itemView,data,position,onClick)
            }
            R.layout.item_id->{
                itemId(itemView,data,position,onClick)
            }
        }
    }

    private fun itemId(itemView:View, data: T, position: Int, onClick: (data: T, position: Int, clickType: Int) -> Unit){
        val binding = ItemIdBinding.bind(itemView)
        if (data is String){
            binding.textId.text = data
        }
        itemView.setOnClickListener {
            onClick.invoke(data,position, DEFAULT_CLICK_TYPE)
        }
    }

    // spinner_item
    private fun spinnerItem(itemView:View, data: T, position: Int, onClick: (data: T, position: Int, clickType: Int) -> Unit){
        val binding = SpinnerItemBinding.bind(itemView)
        if (data is String){
            binding.textItem.text = data
        }
        itemView.setOnClickListener {
            onClick.invoke(data,position, DEFAULT_CLICK_TYPE)
        }
    }

    // item_act_doc
    private fun itemActDoc(itemView:View, data: T, position: Int, onClick: (data: T, position: Int, clickType: Int) -> Unit){
        val binding = ItemActDocBinding.bind(itemView)
        if (data is ActCreateTable){
            binding.tvService.text = data.actService
            binding.tvName.text = data.name
            binding.tvMeasurement.text = data.measure
            binding.tvCount.text = data.count
            binding.tvSumma.text = data.productSumma
            binding.tvTotalSumma.text = data.totalSumma
        }
    }

    // recycler_act_product_item
    private fun <T> recyclerActProductItem(itemView:View, data: T, position: Int, onClick: (data: T, position: Int, clickType: Int) -> Unit){
        val binding = RecyclerActProductItemBinding.bind(itemView)
        if (data is ActProductEntity){
            binding.tvName.text = data.name
            binding.tvCount.text = data.count
            binding.tvCost.text = data.totalSumma
        } else if (data is Product){
            binding.tvName.text = data.name
            binding.tvCount.text = data.count
            binding.tvCost.text = data.totalsum
        }
        binding.editCard.setOnClickListener {
            onClick.invoke(data,position, EDITE_CLICK)
        }
        binding.deleteCard.setOnClickListener {
            onClick.invoke(data,position, DELETE_CLICK)
        }
    }

    // spinner_double_item
    @SuppressLint("SetTextI18n")
    private fun <T> spinnerDouble(itemView:View, data: T, position: Int, onClick: (data: T, position: Int, clickType: Int) -> Unit){
        val binding = SpinnerDoubleItemBinding.bind(itemView)
        if (data is uz.einvoice.domain.models.branchModel.Data){
            binding.tvFirst.text = data.branchName
            binding.tvSecond.text = data.branchNum
        } else if (data is uz.einvoice.domain.models.tasNifProduct.Data){
            binding.tvFirst.text = data.mxikFullName
            binding.tvSecond.text = data.mxikCode
        } else if (data is MeasureEntity){
            binding.tvFirst.text = data.name
            binding.tvSecond.gone()
        }
        itemView.setOnClickListener {
            onClick.invoke(data,position, DEFAULT_CLICK_TYPE)
        }
    }

    // bottomSheet item
    private fun <T> bottomSheetItem(itemView:View,data: T,position: Int,onClick: (data: T, position: Int, clickType: Int) -> Unit){
        val binding = ItemBottomsheetDataBinding.bind(itemView)
        if (data is Children){
            binding.text.text = when(getLanguage(itemView.context)){
                "uz"->data.title_uz
                "uzk"->data.title_uz
                "ru"-> data.title
                else -> data.title
            }
            itemView.setOnClickListener {
                onClick.invoke(data,position, DEFAULT_CLICK_TYPE)
            }
        }
    }
    // item menu
    private fun <T> itemMenu(itemView:View,data: T,position: Int,clickPos: Int,onClick: (data: T, position: Int, clickType: Int) -> Unit){
        val binding = ItemMenuBinding.bind(itemView)
        if (data is Data){
            binding.titleName.text = when(getLanguage(itemView.context)){
                "uz"->data.title_uz
                "uzk"->data.title_uz
                "ru"-> data.title
                else -> data.title
            }
            if (position==clickPos){
                binding.cardClick.setCardBackgroundColor(ContextCompat.getColor(itemView.context,R.color.secondary_color))
                binding.titleName.setTextColor(Color.WHITE)
            } else {
                binding.cardClick.setCardBackgroundColor(ContextCompat.getColor(itemView.context,R.color.white))
                binding.titleName.setTextColor(Color.BLACK)
            }
            binding.cardClick.setOnClickListener{
                onClick.invoke(data,position,DEFAULT_CLICK_TYPE)
            }

            val circularProgressDrawable = CircularProgressDrawable(itemView.context)
            circularProgressDrawable.strokeWidth = 4f
            circularProgressDrawable.centerRadius = 15f
            circularProgressDrawable.start()
            circularProgressDrawable.setColorSchemeColors(ContextCompat.getColor(itemView.context, R.color.primary_color))
            Glide.with(itemView.context)
                .load("$BASE_URL${data.icon_path}")
                .placeholder(circularProgressDrawable)
                .into(binding.imageMenu)
        }
    }
}