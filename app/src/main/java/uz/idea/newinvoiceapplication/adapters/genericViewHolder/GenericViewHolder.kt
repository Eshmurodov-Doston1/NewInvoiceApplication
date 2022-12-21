package uz.idea.newinvoiceapplication.adapters.genericViewHolder

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import uz.idea.domain.database.actProductEntity.ActProductEntity
import uz.idea.domain.database.measure.MeasureEntity
import uz.idea.domain.models.localeClass.table.ActCreateTable
import uz.idea.domain.models.menuModel.Children
import uz.idea.domain.models.menuModel.Data
import uz.idea.newinvoiceapplication.BuildConfig.BASE_URL
import uz.idea.newinvoiceapplication.R
import uz.idea.newinvoiceapplication.databinding.ItemActDocBinding
import uz.idea.newinvoiceapplication.databinding.ItemBottomsheetDataBinding
import uz.idea.newinvoiceapplication.databinding.ItemMenuBinding
import uz.idea.newinvoiceapplication.databinding.RecyclerActProductItemBinding
import uz.idea.newinvoiceapplication.databinding.SpinnerDoubleItemBinding
import uz.idea.newinvoiceapplication.databinding.SpinnerItemBinding
import uz.idea.newinvoiceapplication.utils.appConstant.AppConstant.DEFAULT_CLICK_TYPE
import uz.idea.newinvoiceapplication.utils.appConstant.AppConstant.DELETE_CLICK
import uz.idea.newinvoiceapplication.utils.appConstant.AppConstant.EDITE_CLICK
import uz.idea.newinvoiceapplication.utils.extension.getLanguage
import uz.idea.newinvoiceapplication.utils.extension.gone
import uz.idea.newinvoiceapplication.utils.extension.logData
import uz.idea.newinvoiceapplication.utils.extension.visible

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
            binding.idTv.text = data.actNumber
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
            binding.tvCount.text = data.count.toString()
            binding.tvCost.text = data.totalSumma
            binding.editCard.setOnClickListener {
                onClick.invoke(data,position, EDITE_CLICK)
            }
            binding.deleteCard.setOnClickListener {
                onClick.invoke(data,position, DELETE_CLICK)
            }
        }
    }

    // spinner_double_item
    @SuppressLint("SetTextI18n")
    private fun <T> spinnerDouble(itemView:View, data: T, position: Int, onClick: (data: T, position: Int, clickType: Int) -> Unit){
        val binding = SpinnerDoubleItemBinding.bind(itemView)
        if (data is uz.idea.domain.models.branchModel.Data){
            binding.tvFirst.text = data.branchName
            binding.tvSecond.text = data.branchNum
        } else if (data is uz.idea.domain.models.tasNifProduct.Data){
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