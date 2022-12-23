package uz.einvoice.domain.models.tinOrPinfl.tin456

data class Data(
    val address: String,
    val fullName: String,
    val isItd: Boolean,
    val ns10Code: Int,
    val ns11Code: Int,
    val passIssueDate: String,
    val passNumber: String,
    val passOrg: String,
    val passSeries: String,
    val personalNum: String,
    val tin: String
)