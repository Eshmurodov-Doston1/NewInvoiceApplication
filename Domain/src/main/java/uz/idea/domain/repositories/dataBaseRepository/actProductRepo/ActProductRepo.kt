package uz.idea.domain.repositories.dataBaseRepository.actProductRepo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.flow.Flow
import uz.idea.domain.database.actProductEntity.ActProductEntity

interface ActProductRepo {

    fun deleteTableActProduct()

    fun saveProduct(productEntity: ActProductEntity)

    fun deleteProduct(productEntity: ActProductEntity)
    fun updateProduct(productEntity: ActProductEntity)

    fun getAllProductEntity():Flow<List<ActProductEntity>>
}