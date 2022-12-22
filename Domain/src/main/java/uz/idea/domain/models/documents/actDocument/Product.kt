package uz.idea.domain.models.documents.actDocument

data class Product(
    var catalogcode: String?,
    var catalogname: String?,
    var count: String,
    var measureid: Int,
    var name: String,
    val ordno: String,
    var summa: String,
    var totalsum: String
)