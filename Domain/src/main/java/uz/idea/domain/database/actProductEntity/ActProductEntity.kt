package uz.idea.domain.database.actProductEntity

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity
data class ActProductEntity(
    @PrimaryKey(autoGenerate = true)
    val id:Long?=null,
    val ordno:Int,
    val catalogcode:String,
    val catalogname:String,
    val name:String,
    val measureid:Int,
    val count:Long,
    val summa:String,
    val totalSumma:String
)