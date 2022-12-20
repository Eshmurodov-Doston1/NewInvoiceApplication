package uz.idea.domain.models.act.actSend.actSendFilter

data class ActSendFilter(
    val actdate_end: String?=null,
    val actdate_start: String?=null,
    val actno: String?=null,
    val buyer_branchcode: String?=null,
    val buyertin: Int?=null,
    val contractdate_end: String?=null,
    val contractdate_start: String?=null,
    val contractno: String?=null,
    var limit: Int?=null,
    var page: Int?=null,
    val seller_branchcode: String?=null,
    val sellertin: Int?=null,
    val stateid: String?=null
)