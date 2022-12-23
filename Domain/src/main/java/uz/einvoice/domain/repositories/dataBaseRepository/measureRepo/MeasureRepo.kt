package uz.einvoice.domain.repositories.dataBaseRepository.measureRepo

import uz.einvoice.domain.database.measure.MeasureEntity

interface MeasureRepo {
    fun saveMeasureEntity(listMeasureEntity:List<MeasureEntity>)

    fun getAllMeasureEntity():List<MeasureEntity>

    fun getMeasureEntity(id:Int):MeasureEntity
}