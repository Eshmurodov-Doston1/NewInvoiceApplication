package uz.idea.domain.models.tinOrPinfl.pinfl

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