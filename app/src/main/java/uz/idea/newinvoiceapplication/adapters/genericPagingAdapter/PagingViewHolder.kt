package uz.idea.newinvoiceapplication.adapters.genericPagingAdapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.PorterDuff
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import uz.idea.domain.models.act.actDraftModel.actDraft.Data
import uz.idea.newinvoiceapplication.R
import uz.idea.newinvoiceapplication.databinding.ItemDraftBinding
import uz.idea.newinvoiceapplication.utils.appConstant.AppConstant.CLICK_TYPE_SIGNED
import uz.idea.newinvoiceapplication.utils.appConstant.AppConstant.DEFAULT_CLICK_TYPE
import uz.idea.newinvoiceapplication.utils.extension.getDateFormat
import uz.idea.newinvoiceapplication.utils.extension.numberFormatter
import java.util.*

class PagingViewHolder(var itemView:View):RecyclerView.ViewHolder(itemView),HolderPaging {
    @SuppressLint("SetTextI18n")
    override fun <T> onBind(
        data: T,
        position: Int,
        layoutRes: Int,
        onClick: (data: T, position: Int, clickType: Int, viewBinding: ViewBinding) -> Unit
    ) {
       when(layoutRes){
           R.layout.item_draft->{
               val binding = ItemDraftBinding.bind(itemView)
               if(data is Data){
                   binding.actNumber.text = "№ " + data.actdoc.actno
                   binding.actDate.text =  "${itemView.context.getString(R.string.from)} ${getDateFormat(data.actdoc.actdate,itemView.context)}"
                   binding.contractDate.text = "${itemView.context.getString(R.string.from)} ${getDateFormat(data.contractdoc.contractdate,itemView.context)}"
                   binding.contractNumber.text = "№ " + data.contractdoc.contractno
                   binding.name.text = data.buyername
                   binding.inn.text = "${itemView.context.getString(R.string.inn)}: ${data.buyertin}"
                   binding.totalSumma.text = "${data.payabletotal.numberFormatter()} ${itemView.context.getString(R.string.money_type)}"
                   if (data.stateid==0) {
                       colorItems(R.string.created,R.color.primary_color,binding,itemView)
                   }
               } else if (data is uz.idea.domain.models.act.actIncoming.actIn.Data){
                   binding.actNumber.text = "№ " + data.actdoc.actno
                   binding.actDate.text =  "${itemView.context.getString(R.string.from)} ${getDateFormat(data.actdoc.actdate,itemView.context)}"
                   binding.contractDate.text = "${itemView.context.getString(R.string.from)} ${getDateFormat(data.contractdoc.contractdate,itemView.context)}"
                   binding.contractNumber.text = "№ " + data.contractdoc.contractno
                   binding.name.text = data.buyername
                   binding.inn.text = "${itemView.context.getString(R.string.inn)}: ${data.buyertin}"
                   binding.totalSumma.text = "${data.payabletotal.numberFormatter()} ${itemView.context.getString(R.string.money_type)}"
                   when(data.stateid){
                       15-> colorItems(R.string.partner_signature,R.color.primary_color,binding,itemView)
                       17-> colorItems(R.string.cancelled_by_send,R.color.partner_color,binding,itemView)
                       20-> colorItems(R.string.rejected_by_partner,R.color.delete_color,binding,itemView)
                       30-> colorItems(R.string.accepted_by_partner,R.color.send_color,binding,itemView)
                   }
               } else if (data is uz.idea.domain.models.act.actSend.actSendData.Data){
                   binding.actNumber.text = "№ " + data.actdoc.actno
                   binding.actDate.text =  "${itemView.context.getString(R.string.from)} ${getDateFormat(data.actdoc.actdate,itemView.context)}"
                   binding.contractDate.text = "${itemView.context.getString(R.string.from)} ${getDateFormat(data.contractdoc.contractdate,itemView.context)}"
                   binding.contractNumber.text = "№ " + data.contractdoc.contractno
                   binding.name.text = data.buyername
                   binding.inn.text = "${itemView.context.getString(R.string.inn)}: ${data.buyertin}"
                   binding.totalSumma.text = "${data.payabletotal.numberFormatter()} ${itemView.context.getString(R.string.money_type)}"
                   when(data.stateid){
                       15-> colorItems(R.string.partner_signature,R.color.primary_color,binding,itemView)
                       17-> colorItems(R.string.cancelled_by_send,R.color.partner_color,binding,itemView)
                       20-> colorItems(R.string.rejected_by_partner,R.color.delete_color,binding,itemView)
                       30-> colorItems(R.string.accepted_by_partner,R.color.send_color,binding,itemView)
                   }
               }
               itemView.setOnClickListener {
                   onClick.invoke(data,position,DEFAULT_CLICK_TYPE,binding)
               }
               binding.linearSend.setOnClickListener {
                   onClick.invoke(data,position, CLICK_TYPE_SIGNED,binding)
               }
           }
       }
    }

    private fun colorItems(textId:Int,colorSecond:Int,binding: ItemDraftBinding,itemView:View){
        binding.stateText.text = itemView.context.getString(textId)
        binding.cardState.setCardBackgroundColor(ContextCompat.getColor(itemView.context,colorSecond))
        binding.signImage.setColorFilter(ContextCompat.getColor(itemView.context,colorSecond),PorterDuff.Mode.SRC_IN)
        binding.signTv.setTextColor(ContextCompat.getColor(itemView.context,colorSecond))
    }
}