package uz.einvoice.domain.models.act.actSend.actSendData

data class Data(
    val _id: String,
    val actdoc: Actdoc,
    val buyername: String,
    val buyertin: Long,
    val contractdoc: Contractdoc,
    val payabletotal: Double,
    val sellername: String,
    val sellertin: Int,
    val stateid: Int,
    val statetext: Statetext
)