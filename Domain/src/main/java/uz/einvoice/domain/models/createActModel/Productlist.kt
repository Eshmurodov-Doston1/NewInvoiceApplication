package uz.einvoice.domain.models.createActModel

data class Productlist(
    val actproductid: String,
    val products: List<Product>,
    val tin: String
)