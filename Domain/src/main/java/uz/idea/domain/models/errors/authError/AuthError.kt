package uz.idea.domain.models.errors.authError

data class AuthError(
    val errors: Errors?=null,
    val message: String
)