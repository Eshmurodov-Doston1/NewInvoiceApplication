package uz.idea.domain.usesCase.apiUsesCase

import uz.idea.domain.repositories.apiRepository.ApiRepository
import uz.idea.domain.utils.resPonseFetcher.ResponseFetcher
import javax.inject.Inject

class ApiUsesCase @Inject constructor(
    private val apiRepository: ApiRepository,
    private val responseFetcher: ResponseFetcher.Base
) {
   suspend fun methodeGet(url:String,queryMap: HashMap<String,String>)
   = responseFetcher.getFlowResponseState(apiRepository.methodeGet(url, queryMap))

    suspend fun methodePOST(url:String,body:Any,queryMap: HashMap<String,String>)
    = responseFetcher.getFlowResponseState(apiRepository.methodePost(url,body, queryMap))

    suspend fun methodePUT(url:String,body:Any,queryMap: HashMap<String,String>)
    = responseFetcher.getFlowResponseState(apiRepository.methodePut(url,body, queryMap))

    suspend fun methodeDELETE(url:String,body:Any,queryMap: HashMap<String,String>)
    = responseFetcher.getFlowResponseState(apiRepository.methodeDelete(url,body, queryMap))
}