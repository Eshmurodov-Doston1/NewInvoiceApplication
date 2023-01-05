package uz.einvoice.domain.models.menuModel

import java.util.LinkedList

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
){
    fun getMenuList():List<Children>{
        val listMenu = ArrayList<Children>()
        when(title?.lowercase()){
            "Акт".lowercase()->{
               val actAdd = children?.get(0)
                val receive = children?.get(1)
                val send = children?.get(3)
                val draft = children?.get(2)
                val queue = children?.get(4)
                listMenu.addAll(listOf(actAdd!!,receive!!,send!!,draft!!,queue!!))
            }
            else-> listMenu.addAll(children?: emptyList())
        }
        return listMenu
    }
}

