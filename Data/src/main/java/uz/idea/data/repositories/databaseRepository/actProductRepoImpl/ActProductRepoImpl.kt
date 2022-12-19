package uz.idea.data.repositories.databaseRepository.actProductRepoImpl

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import kotlinx.coroutines.flow.Flow
import uz.idea.domain.database.actProductEntity.ActProductDao
import uz.idea.domain.database.actProductEntity.ActProductEntity
import uz.idea.domain.repositories.dataBaseRepository.actProductRepo.ActProductRepo
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