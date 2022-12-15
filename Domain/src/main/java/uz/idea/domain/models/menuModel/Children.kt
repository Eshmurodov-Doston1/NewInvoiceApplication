package uz.idea.domain.models.menuModel

data class Children(
    val api_path: Any,
    val icon_path: Any,
    val id: Int,
    val menu_id: Int,
    val ordering: Int,
    val parent_id: Int,
    val path: String,
    val title: String,
    val title_uz: String
)