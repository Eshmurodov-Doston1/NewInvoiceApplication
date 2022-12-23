package uz.einvoice.domain.database.measure

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class MeasureEntity(
    @PrimaryKey
    val measureId: Int,
    val name: String
)