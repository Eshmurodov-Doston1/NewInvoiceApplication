package uz.idea.domain.models.act.actDraftModel.actDraft

data class Data(
    val _id: String,
    val actdoc: Actdoc,
    val buyername: String,
    val buyertin: Long,
    val contractdoc: Contractdoc,
    val notes: Any,
    val payabletotal: Double,
    val sellername: String,
    val sellertin: Int,
    val stateid: Int,
    val statetext: Statetext
)