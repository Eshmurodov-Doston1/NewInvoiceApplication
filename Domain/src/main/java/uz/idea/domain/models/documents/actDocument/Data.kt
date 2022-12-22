package uz.idea.domain.models.documents.actDocument

data class Data(
    var _id: String,
    val actdoc: Actdoc,
    val buyerbranchcode: String,
    val buyerbranchname: String,
    val buyername: String,
    val buyertin: Int,
    val contractdoc: Contractdoc,
    val covotingstate: Int,
    val created_at: String,
    val filter: Filter,
    val isdraft: Int,
    val notes: Any,
    val payabletotal: String,
    val productlist: Productlist,
    val sellerbranchcode: String,
    val sellerbranchname: String?=null,
    val sellername: String,
    val sellertin: Long,
    val stateid: Int,
    val statetext: Statetext,
    val summatotal: Int
)