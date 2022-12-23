package uz.einvoice.domain.models.createActModel

data class Product(
    val catalogcode: String,
    val catalogname: String,
    val count: String,
    val measureid: Int,
    val name: String,
    val ordno: Int,
    val summa: String,
    val totalsum: String
)