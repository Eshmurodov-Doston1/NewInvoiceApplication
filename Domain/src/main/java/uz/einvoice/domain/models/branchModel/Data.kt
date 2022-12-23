package uz.einvoice.domain.models.branchModel

data class Data(
    val address: String,
    val branchName: String,
    val branchNum: String,
    val clientIp: Any,
    val createdDate: String,
    val deletedDate: Any,
    val directorName: String,
    val directorPinfl: Long,
    val directorTin: String,
    val id: Int,
    val isDeleted: Int,
    val lang: Any,
    val latitude: String,
    val longitude: String,
    val name: String,
    val ns10Code: Int,
    val ns10Name: String,
    val ns11Code: Int,
    val ns11Name: String,
    val pinfl: Any,
    val source: Any,
    val tin: String,
    val url: Any
)