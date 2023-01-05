package uz.einvoice.domain.usesCase.pagingUsesCase.actPaging

import com.google.gson.JsonElement
import uz.einvoice.domain.models.act.actDocument.actDocumentData.ActDocumentModel
import uz.einvoice.domain.models.act.actDocument.actDocumentFilter.ActDocumentFilter
import uz.einvoice.domain.repositories.apiRepository.ApiRepository
import uz.einvoice.domain.repositories.pagingRepository.createPager
import uz.einvoice.domain.usesCase.apiUsesCase.parseClass
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DraftPagingUsesCase @Inject constructor(
 private val apiRepository: ApiRepository
) {
    fun <T> getDocumentAct(url:String,body:T,queryMap:HashMap<String,String>) = createPager { page->
        var response:retrofit2.Response<JsonElement>?=null
        if (body is ActDocumentFilter){
            body.limit = 10
            body.page = page
            response = apiRepository.methodePost(url,body,queryMap)
        }
        if (response?.isSuccessful == true) {
            response.body()?.parseClass(ActDocumentModel::class.java)?.dataActDocument?: emptyList()
        } else {
            emptyList()
        }
    }.flow
}