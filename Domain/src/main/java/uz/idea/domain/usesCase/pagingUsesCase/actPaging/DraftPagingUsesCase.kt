package uz.idea.domain.usesCase.pagingUsesCase.actPaging

import android.util.Log
import com.google.gson.JsonElement
import okhttp3.Response
import uz.idea.domain.models.act.actDraftModel.actDraft.ActDraftmodel
import uz.idea.domain.models.act.actDraftModel.actDraft.Data
import uz.idea.domain.models.act.actDraftModel.actDraftFilter.ActDraftFilter
import uz.idea.domain.models.act.actIncoming.actIn.ActIncomingModel
import uz.idea.domain.models.act.actIncoming.incomingFilterModule.IncomingFilterModel
import uz.idea.domain.models.act.actSend.actSendData.ActSendData
import uz.idea.domain.models.act.actSend.actSendFilter.ActSendFilter
import uz.idea.domain.repositories.apiRepository.ApiRepository
import uz.idea.domain.repositories.pagingRepository.createPager
import uz.idea.domain.usesCase.apiUsesCase.parseClass
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
            response.body()?.parseClass(ActSendData::class.java)?.data?: emptyList()
        } else {
            emptyList()
        }
    }.flow
}