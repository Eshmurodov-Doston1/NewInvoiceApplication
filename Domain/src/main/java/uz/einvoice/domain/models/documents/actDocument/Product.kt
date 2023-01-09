package uz.einvoice.domain.models.documents.actDocument

import java.math.BigDecimal

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