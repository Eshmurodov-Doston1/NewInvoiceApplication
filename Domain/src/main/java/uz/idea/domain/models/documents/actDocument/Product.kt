package uz.idea.domain.models.documents.actDocument

data class Product(
    val catalogcode: String,
    val catalogname: String,
    val count: String,
    val measureid: Int,
    val name: String,
    val ordno: String,
    val summa: String,
    val totalsum: String
)