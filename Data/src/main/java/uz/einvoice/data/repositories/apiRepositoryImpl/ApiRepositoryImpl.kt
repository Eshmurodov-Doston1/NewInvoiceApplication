package uz.einvoice.data.repositories.apiRepositoryImpl

import uz.einvoice.data.network.apiService.ApiService
import uz.einvoice.domain.repositories.apiRepository.ApiRepository
import javax.inject.Inject

class ApiRepositoryImpl @Inject constructor(
    private val apiService: ApiService
):ApiRepository {
    override suspend fun methodeGet(url: String, queryMap: HashMap<String, String>)
    = apiService.methodeGET(url,queryMap)

    override suspend fun methodePost(url: String, body: Any, queryMap: HashMap<String, String>)
    = apiService.methodePOST(url,body,queryMap)

    override suspend fun methodePut(url: String, body: Any, queryMap: HashMap<String, String>)
    = apiService.methodePUT(url,body,queryMap)

    override suspend fun methodeDelete(url: String, body: Any, queryMap: HashMap<String, String>)
    = apiService.methodeDELETE(url,body,queryMap)

}