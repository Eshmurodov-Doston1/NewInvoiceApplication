package uz.idea.domain.models.authModel.resAuth

data class ResAuthModel(
    val access_token: String,
    val expires_in: Int,
    val refresh_token: String,
    val token_type: String
)