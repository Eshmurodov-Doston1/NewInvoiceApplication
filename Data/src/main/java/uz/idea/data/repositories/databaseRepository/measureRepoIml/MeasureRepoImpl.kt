package uz.idea.data.repositories.databaseRepository.measureRepoIml

import uz.idea.domain.database.measure.MeasureDao
import uz.idea.domain.database.measure.MeasureEntity
import uz.idea.domain.repositories.dataBaseRepository.measureRepo.MeasureRepo
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MeasureRepoImpl @Inject constructor(
    private val measureDao: MeasureDao
):MeasureRepo {
    override fun saveMeasureEntity(listMeasureEntity: List<MeasureEntity>) {
        measureDao.saveMeasure(listMeasureEntity)
    }

    override fun getAllMeasureEntity() = measureDao.getAllMeasure()
}