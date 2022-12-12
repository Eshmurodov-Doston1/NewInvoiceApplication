package uz.idea.data.network.apiService

import com.google.gson.JsonElement
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.QueryMap
import retrofit2.http.Url

interface ApiService {
    @GET
    suspend fun methodeGET(
        @Url url:String,
        @QueryMap queryMap: HashMap<String,String>
    ):Response<JsonElement>

    @GET
    suspend fun methodePOST(
        @Url url:String,
        @Body body:Any,
        @QueryMap queryMap: HashMap<String,String>
    ):Response<JsonElement>

    @GET
    suspend fun methodePUT(
        @Url url:String,
        @Body body:Any,
        @QueryMap queryMap: HashMap<String,String>
    ):Response<JsonElement>

    @GET
    suspend fun methodeDELETE(
        @Url url:String,
        @Body body:Any,
        @QueryMap queryMap: HashMap<String,String>
    ):Response<JsonElement>
}