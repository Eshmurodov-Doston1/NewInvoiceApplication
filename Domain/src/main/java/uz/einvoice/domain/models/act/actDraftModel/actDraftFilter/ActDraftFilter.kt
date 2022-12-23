package uz.einvoice.domain.models.act.actDraftModel.actDraftFilter

data class ActDraftFilter(
    val actdate_end: String?=null,
    val actdate_start: String?=null,
    val actno: String?=null,
    val buyer_branchcode: String?=null,
    val buyertin: Long?=null,
    val contractdate_end: String?=null,
    val contractdate_start: String?=null,
    val contractno: String?=null,
    var limit: Int?=null,
    var page: Int?=null,
    val seller_branchcode: String?=null,
    val sellertin: Long?=null,
    val stateid: Long?=null
)