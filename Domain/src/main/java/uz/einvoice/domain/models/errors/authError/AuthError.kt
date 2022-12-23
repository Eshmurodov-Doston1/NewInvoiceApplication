package uz.einvoice.domain.models.errors.authError

data class AuthError(
    val errors: Errors?=null,
    val message: String
)