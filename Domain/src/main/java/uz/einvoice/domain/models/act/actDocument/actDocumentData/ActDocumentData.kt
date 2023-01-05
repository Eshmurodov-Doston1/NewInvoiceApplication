package uz.einvoice.domain.models.act.actDocument.actDocumentData

data class ActDocumentData(
    val _id: String,
    val actdoc: Actdoc,
    val buyername: String,
    val buyertin: Int,
    val contractdoc: Contractdoc,
    val payabletotal: Double,
    val sellername: String,
    val sellertin: Int,
    val stateid: Int,
    val statetext: Statetext
)