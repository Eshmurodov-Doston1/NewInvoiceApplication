package uz.einvoice.domain.models.createActModel

data class CreateActModel(
    val actdoc: Actdoc,
    val actid: String?,
    val buyerbranchcode: String,
    val buyerbranchname: String,
    val buyername: String,
    val buyertin: String,
    val contractdoc: Contractdoc,
    val productlist: Productlist,
    val sellerbranchcode: String,
    val sellerbranchname: String,
    val sellername: String,
    val sellertin: String,
    val stateid:Int?=null
)