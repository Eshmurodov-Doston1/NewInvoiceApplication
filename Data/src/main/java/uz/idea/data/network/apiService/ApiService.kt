package uz.idea.data.network.apiService

import com.google.gson.JsonElement
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @GET
    suspend fun methodeGET(
        @Url url:String,
        @QueryMap queryMap: HashMap<String,String>
    ):Response<JsonElement>

    @POST
    suspend fun methodePOST(
        @Url url:String,
        @Body body:Any,
        @QueryMap queryMap: HashMap<String,String>
    ):Response<JsonElement>

    @PUT
    suspend fun methodePUT(
        @Url url:String,
        @Body body:Any,
        @QueryMap queryMap: HashMap<String,String>
    ):Response<JsonElement>

    @DELETE
    suspend fun methodeDELETE(
        @Url url:String,
        @Body body:Any,
        @QueryMap queryMap: HashMap<String,String>
    ):Response<JsonElement>
}