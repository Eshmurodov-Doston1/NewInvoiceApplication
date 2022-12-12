package uz.idea.domain.repositories.apiRepository

import com.google.gson.JsonElement
import retrofit2.Response

interface ApiRepository {
    suspend fun methodeGet(url:String,queryMap:HashMap<String,String>):Response<JsonElement>
    suspend fun methodePost(url:String,body:Any,queryMap:HashMap<String,String>):Response<JsonElement>
    suspend fun methodePut(url:String,body:Any,queryMap:HashMap<String,String>):Response<JsonElement>
    suspend fun methodeDelete(url:String,body:Any,queryMap:HashMap<String,String>):Response<JsonElement>
}