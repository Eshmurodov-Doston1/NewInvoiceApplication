package uz.einvoice.data.repositories.databaseRepository.measureRepoIml

import uz.einvoice.domain.database.measure.MeasureDao
import uz.einvoice.domain.database.measure.MeasureEntity
import uz.einvoice.domain.repositories.dataBaseRepository.measureRepo.MeasureRepo
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
    override fun getMeasureEntity(id: Int) = measureDao.getMeasure(id)
}