package uz.idea.domain.database.errorModel

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ErrorEntity(
    @PrimaryKey(autoGenerate = true)
    val id:Int?=null,
    val error:String?=null,
    val errorCode:Int
)