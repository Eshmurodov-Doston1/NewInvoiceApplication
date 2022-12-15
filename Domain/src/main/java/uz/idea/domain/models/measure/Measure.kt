package uz.idea.domain.models.measure

import uz.idea.domain.database.measure.MeasureEntity
import java.util.*

data class Measure(
    val `data`: List<Data>
){
    fun getMeasureList():List<MeasureEntity>{
        val listMeasure = LinkedList<MeasureEntity>()
        this.data.onEach {  data->
            val measureEntity = MeasureEntity(measureId = data.measureId, name = data.name)
            listMeasure.add(measureEntity)
        }
        return listMeasure
    }
}