package uz.idea.domain.repositories.dataBaseRepository.measureRepo

import uz.idea.domain.database.measure.MeasureEntity

interface MeasureRepo {
    fun saveMeasureEntity(listMeasureEntity:List<MeasureEntity>)

    fun getAllMeasureEntity():List<MeasureEntity>
}