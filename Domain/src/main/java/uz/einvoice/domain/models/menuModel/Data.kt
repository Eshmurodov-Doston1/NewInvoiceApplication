package uz.einvoice.domain.models.menuModel

data class Data(
    val api_path: Any?=null,
    val children: List<Children>?=null,
    val icon_path: String?=null,
    val id: Int?=null,
    val menu_id: Int?=null,
    val ordering: Int?=null,
    val parent_id: Int?=null,
    val path: Any?=null,
    val title: String?=null,
    val title_uz: String?=null,
    var isClick:Boolean = false
)