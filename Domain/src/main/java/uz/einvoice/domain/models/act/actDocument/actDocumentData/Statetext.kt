package uz.einvoice.domain.models.act.actDocument.actDocumentData

data class Statetext(
    val `class`: String,
    val state: Int,
    val status: String,
    val text: String
)