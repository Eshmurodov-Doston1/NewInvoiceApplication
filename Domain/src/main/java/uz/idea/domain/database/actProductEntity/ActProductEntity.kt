package uz.idea.domain.database.actProductEntity

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity
data class ActProductEntity(
    @PrimaryKey(autoGenerate = true)
    val id:Long?=null,
    val ordno:Int,
    var catalogcode:String,
    var catalogname:String,
    var name:String,
    var measureid:Int,
    var count:String,
    var summa:String,
    var totalSumma:String
)