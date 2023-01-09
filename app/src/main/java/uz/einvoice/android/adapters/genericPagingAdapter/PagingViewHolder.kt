package uz.einvoice.android.adapters.genericPagingAdapter

import android.annotation.SuppressLint
import android.graphics.PorterDuff
import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import uz.einvoice.android.R
import uz.einvoice.android.databinding.ItemDocumentBinding
import uz.einvoice.android.utils.appConstant.AppConstant.ACT_DRAFT
import uz.einvoice.android.utils.appConstant.AppConstant.ACT_QUEUE
import uz.einvoice.android.utils.appConstant.AppConstant.ACT_RECEIVE
import uz.einvoice.android.utils.appConstant.AppConstant.ACT_SENT
import uz.einvoice.android.utils.appConstant.AppConstant.CLICK_TYPE_SIGNED
import uz.einvoice.android.utils.appConstant.AppConstant.DEFAULT_CLICK_TYPE
import uz.einvoice.android.utils.extension.getDateFormat
import uz.einvoice.android.utils.extension.gone
import uz.einvoice.android.utils.extension.numberFormatter
import uz.einvoice.domain.models.act.actDocument.actDocumentData.ActDocumentData
import java.util.*

class PagingViewHolder(var itemView:View):RecyclerView.ViewHolder(itemView),HolderPaging {
    @RequiresApi(Build.VERSION_CODES.P)
    override fun <T> onBind(
        data: T,
        position: Int,
        layoutRes: Int,
        type:String,
        onClick: (data: T, position: Int, clickType: Int, viewBinding: ViewBinding,type:String) -> Unit
    ) {
       when(layoutRes){
           R.layout.item_document->{
              itemDocument(data,position,type,onClick)
           }
       }
    }


    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.P)
    private fun <T> itemDocument(data:T,position:Int,type: String,onClick:(data: T, position: Int, clickType: Int, viewBinding: ViewBinding,type:String) -> Unit){
        val binding = ItemDocumentBinding.bind(itemView)
        if (data is ActDocumentData){

            when(type){
                ACT_DRAFT->{
                    if (data.stateid==0) {
                        colorItems(R.string.created,R.color.primary_color,binding,itemView)
                    }
                }
                ACT_RECEIVE->{
                        when(data.stateid){
                        15-> colorItems(R.string.partner_signature,R.color.primary_color,binding,itemView)
                        17-> {
                            colorItems(R.string.cancelled_by_send,R.color.partner_color,binding,itemView)
                            binding.linearSend.gone()
                        }
                        20-> {
                            colorItems(R.string.rejected_by_partner,R.color.delete_color,binding,itemView)
                            binding.linearSend.gone()
                        }
                        30-> {
                            colorItems(R.string.accepted_by_partner,R.color.send_color,binding,itemView)
                            binding.linearSend.gone()
                        }
                    }
                }
                ACT_SENT->{
                        when(data.stateid){
                        15-> {
                            binding.linearSend.gone()
                            colorItems(R.string.partner_signature,R.color.primary_color,binding,itemView)
                        }
                        17-> {
                            colorItems(R.string.cancelled_by_send,R.color.partner_color,binding,itemView)
                            binding.linearSend.gone()
                        }
                        20-> {
                            colorItems(R.string.rejected_by_partner,R.color.delete_color,binding,itemView)
                            binding.linearSend.gone()
                        }
                        30-> {
                            colorItems(R.string.accepted_by_partner,R.color.send_color,binding,itemView)
                            binding.linearSend.gone()
                        }
                    }
                }
                ACT_QUEUE->{
                    when(data.stateid){
                        1->{
                            colorItems(R.string.process_of_sending,R.color.primary_color,binding,itemView)
                        }
                        2->{
                            colorItems(R.string.error_while_settings,R.color.error_sending,binding,itemView)
                        }
                    }
                    binding.linearSend.gone()
                }
            }

            binding.actNumber.text = "№ " + data.actdoc.actno
            binding.actDate.text =  "${itemView.context.getString(R.string.from)} ${getDateFormat(data.actdoc.actdate,itemView.context)}"
            binding.contractDate.text = "${itemView.context.getString(R.string.from)} ${getDateFormat(data.contractdoc.contractdate,itemView.context)}"
            binding.contractNumber.text = "№ " + data.contractdoc.contractno
            binding.name.text = data.buyername
            binding.inn.text = "${itemView.context.getString(R.string.inn)}: ${data.buyertin}"
            binding.totalSumma.text = "${data.payabletotal.numberFormatter()} ${itemView.context.getString(R.string.money_type)}"
        }
        itemView.setOnClickListener {
            onClick.invoke(data,position,DEFAULT_CLICK_TYPE,binding,type)
        }
        binding.linearSend.setOnClickListener {
            onClick.invoke(data,position, CLICK_TYPE_SIGNED,binding,type)
        }
    }
    @RequiresApi(Build.VERSION_CODES.P)
    private fun colorItems(textId:Int, colorSecond:Int, binding: ItemDocumentBinding, itemView:View){
        binding.stateText.text = itemView.context.getString(textId)
        binding.card.outlineSpotShadowColor = ContextCompat.getColor(itemView.context,colorSecond)
        binding.card.outlineAmbientShadowColor = ContextCompat.getColor(itemView.context,colorSecond)
        binding.cardState.setCardBackgroundColor(ContextCompat.getColor(itemView.context,colorSecond))
        binding.signImage.setColorFilter(ContextCompat.getColor(itemView.context,colorSecond),PorterDuff.Mode.SRC_IN)
        binding.signTv.setTextColor(ContextCompat.getColor(itemView.context,colorSecond))
    }
}