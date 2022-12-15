package uz.idea.domain.models.companyInfo

data class Data(
    val account: String,
    val accountant: String,
    val address: String,
    val director: String,
    val directorPinfl: Long,
    val directorTin: String,
    val isBudget: Int,
    val mfo: String,
    val na1Code: Int,
    val na1Name: String,
    val name: String,
    val ns10Code: Int,
    val ns11Code: Int,
    val oked: String,
    val shortName: String,
    val statusCode: Int,
    val statusName: String,
    val tin: String
)