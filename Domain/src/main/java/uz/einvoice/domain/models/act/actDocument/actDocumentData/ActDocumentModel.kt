package uz.einvoice.domain.models.act.actDocument.actDocumentData

import com.google.gson.annotations.SerializedName

data class ActDocumentModel(
    val current_page: Int,
    @SerializedName("data")
    val dataActDocument: List<ActDocumentData>,
    val first_page_url: String,
    val from: Int,
    val last_page: Int,
    val last_page_url: String,
    val links: List<Link>,
    val next_page_url: String,
    val path: String,
    val per_page: Int,
    val prev_page_url: Any,
    val to: Int,
    val total: Int
)