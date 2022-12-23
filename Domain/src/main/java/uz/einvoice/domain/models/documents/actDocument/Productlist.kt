package uz.einvoice.domain.models.documents.actDocument

data class Productlist(
    val actproductid: String,
    val products: List<Product>,
    val tin: Int
)