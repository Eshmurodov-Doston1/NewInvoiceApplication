package uz.einvoice.domain.usesCase.pagingUsesCase.actPaging

import android.util.Log
import com.google.gson.JsonElement
import uz.einvoice.domain.models.act.actDraftModel.actDraft.ActDraftmodel
import uz.einvoice.domain.models.act.actDraftModel.actDraftFilter.ActDraftFilter
import uz.einvoice.domain.models.act.actIncoming.actIn.ActIncomingModel
import uz.einvoice.domain.models.act.actIncoming.incomingFilterModule.IncomingFilterModel
import uz.einvoice.domain.models.act.actSend.actSendData.ActSendData
import uz.einvoice.domain.models.act.actSend.actSendFilter.ActSendFilter
import uz.einvoice.domain.repositories.apiRepository.ApiRepository
import uz.einvoice.domain.repositories.pagingRepository.createPager
import uz.einvoice.domain.usesCase.apiUsesCase.parseClass
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DraftPagingUsesCase @Inject constructor(
 private val apiRepository: ApiRepository
) {
    fun <T> getDraftData(url:String,body:T,queryMap:HashMap<String,String>) = createPager { page->
        var response:retrofit2.Response<JsonElement>?=null
        if (body is ActDraftFilter){
            body.limit = 10
            body.page = page
            response = apiRepository.methodePost(url,body,queryMap)
        }
        if (response?.isSuccessful == true) {
            response.body()?.parseClass(ActDraftmodel::class.java)?.data?: emptyList()
        } else {
            emptyList()
        }
    }.flow

    fun <T> getIncomingData(url:String,body:T,queryMap:HashMap<String,String>) = createPager { page->
        var response:retrofit2.Response<JsonElement>?=null
        if (body is IncomingFilterModel){
            body.limit = 10
            body.page = page
            response = apiRepository.methodePost(url,body,queryMap)
        }
        if (response?.isSuccessful == true) {
            response.body()?.parseClass(ActIncomingModel::class.java)?.data?: emptyList()
        } else {
            emptyList()
        }
    }.flow

    fun <T> getActSendData(url:String,body:T,queryMap:HashMap<String,String>) = createPager { page->
        var response:retrofit2.Response<JsonElement>?=null
        if (body is ActSendFilter){
            body.limit = 10
            body.page = page
            response = apiRepository.methodePost(url,body,queryMap)
        }
        if (response?.isSuccessful == true) {
            val sendList = response.body()?.parseClass(ActSendData::class.java)?.data
            sendList?:emptyList()
        } else {
            emptyList()
        }
    }.flow
}