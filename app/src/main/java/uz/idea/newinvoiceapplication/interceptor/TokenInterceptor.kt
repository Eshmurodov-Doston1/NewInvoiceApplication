package uz.idea.newinvoiceapplication.interceptor

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.*
import org.json.JSONObject
import uz.idea.newinvoiceapplication.utils.myshared.MySharedPreferences
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenInterceptor @Inject constructor(
    private val preferenceHelper: MySharedPreferences,
    @ApplicationContext private val context: Context
    ) : Interceptor {
    @Throws(Exception::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val oldRequest = chain.request()

        val oldResponse = chain.proceed(oldRequest)
        val responseBody = oldResponse.body
        if (oldResponse.code == 401) {
            try {
                var modifiedRequest: Request?
                val client = OkHttpClient()
                val params = JSONObject()
                params.put("refresh_token", preferenceHelper.refreshToken ?: "")
                val body: RequestBody = RequestBody.create(responseBody?.contentType(),params.toString())
                val nRequest = Request.Builder()
                    .post(body)
                    .url("${"preferenceHelper.urlData"}api/auth/refresh_token")
                    .build()
                val response = client.newCall(nRequest).execute()

                Log.e("Url",  "${"preferenceHelper.urlData"}api/auth/refresh_token")
                if (response.code == 200) {
                    // Get response
                    val jsonData = response.body?.string() ?: ""
                    val gson = Gson()
//                    val resAuth: ResAuth = gson.fromJson(jsonData, ResAuth::class.java)
//                    preferenceHelper.accessToken = resAuth.access_token
//                    preferenceHelper.refreshToken = resAuth.refresh_token
//                    preferenceHelper.tokenType = resAuth.token_type
                    oldResponse.close()
                    modifiedRequest = oldRequest.newBuilder()
//                        .header(HttpHeaders.ACCEPT,JSON_DATA)
//                        .header(HttpHeaders.AUTHORIZATION, "${preferenceHelper.tokenType} ${preferenceHelper.accessToken}")
                        .build()
                    return chain.proceed(modifiedRequest)
                }
            }catch (e:Exception){
                e.printStackTrace()
            }
        }
        return oldResponse
    }
}