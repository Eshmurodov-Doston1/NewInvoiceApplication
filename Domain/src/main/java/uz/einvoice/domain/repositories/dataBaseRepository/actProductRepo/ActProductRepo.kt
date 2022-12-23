package uz.einvoice.domain.repositories.dataBaseRepository.actProductRepo

import kotlinx.coroutines.flow.Flow
import uz.einvoice.domain.database.actProductEntity.ActProductEntity

interface ActProductRepo {

    fun deleteTableActProduct()

    fun saveProduct(productEntity: ActProductEntity)

    fun deleteProduct(productEntity: ActProductEntity)
    fun updateProduct(productEntity: ActProductEntity)

    fun getAllProductEntity():Flow<List<ActProductEntity>>
}