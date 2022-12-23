package uz.einvoice.data.repositories.databaseRepository.actProductRepoImpl

import androidx.lifecycle.asFlow
import uz.einvoice.domain.database.actProductEntity.ActProductDao
import uz.einvoice.domain.database.actProductEntity.ActProductEntity
import uz.einvoice.domain.repositories.dataBaseRepository.actProductRepo.ActProductRepo
import javax.inject.Inject

class ActProductRepoImpl @Inject constructor(
    private val actProductDao: ActProductDao
):ActProductRepo {
    override fun deleteTableActProduct() = actProductDao.deleteTableAct()

    override fun saveProduct(productEntity: ActProductEntity) =  actProductDao.saveProduct(productEntity)

    override fun deleteProduct(productEntity: ActProductEntity) =  actProductDao.deleteProduct(productEntity)

    override fun updateProduct(productEntity: ActProductEntity) =  actProductDao.updateProduct(productEntity)

    override fun getAllProductEntity() = actProductDao.getAllActProductEntity().asFlow()
}