package uz.einvoice.domain.models.localeClass.table

data class ActCreateTable(
    val actNumber:String,
    val actService:String,
    val name:String,
    val measure:String,
    val count:String,
    val productSumma:String,
    val totalSumma:String)
