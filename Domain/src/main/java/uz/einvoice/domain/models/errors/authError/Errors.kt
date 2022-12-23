package uz.einvoice.domain.models.errors.authError

data class Errors(
    val password: List<String>?=null,
    val phone: List<String>?=null
)