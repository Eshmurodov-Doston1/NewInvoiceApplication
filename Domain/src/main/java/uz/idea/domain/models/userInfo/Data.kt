package uz.idea.domain.models.userInfo

data class Data(
    val activeCompany: Int,
    val activeCompanyInfo: ActiveCompanyInfo,
    val balance: Balance,
    val branch: Branch,
    val full_name: String,
    val phone: String,
    val tariff: Tariff,
    val tin: Long
)