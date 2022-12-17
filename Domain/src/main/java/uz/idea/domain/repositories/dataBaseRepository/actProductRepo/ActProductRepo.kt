package uz.idea.domain.repositories.dataBaseRepository.actProductRepo

import androidx.lifecycle.LiveData
import uz.idea.domain.database.actProductEntity.ActProductEntity

interface ActProductRepo {

    fun deleteTableActProduct()

    fun saveProduct(productEntity: ActProductEntity)

    fun deleteProduct(productEntity: ActProductEntity)

    fun getAllProductEntity():LiveData<List<ActProductEntity>>
}