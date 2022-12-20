package uz.idea.domain.models.act.actIncoming.incomingFilterModule

data class IncomingFilterModel(
    val actdate_end: Any?=null,
    val actdate_start: Any?=null,
    val actno: Any?=null,
    val buyer_branchcode: Any?=null,
    val buyertin: Any?=null,
    val contractdate_end: Any?=null,
    val contractdate_start: Any?=null,
    val contractno: Any?=null,
    var limit: Int?=null,
    var page: Int?=null,
    val seller_branchcode: Any?=null,
    val sellertin: Any?=null,
    val stateid: Any?=null
)