package uz.idea.newinvoiceapplication.interceptor

import android.content.Context
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpHeaders
import com.google.gson.Gson
import com.google.gson.JsonParser
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.*
import org.json.JSONObject
import uz.idea.domain.models.authModel.resAuth.ResAuthModel
import uz.idea.domain.repositories.dataBaseRepository.actProductRepo.ActProductRepo
import uz.idea.newinvoiceapplication.BuildConfig.BASE_URL
import uz.idea.newinvoiceapplication.utils.appConstant.AppConstant.APPLICATION_JSON
import uz.idea.newinvoiceapplication.utils.extension.getLanguage
import uz.idea.newinvoiceapplication.utils.extension.logData
import uz.idea.newinvoiceapplication.utils.myshared.MySharedPreferences
import java.net.HttpURLConnection
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.acos

@Singleton
class TokenInterceptor @Inject constructor(
    private val preferenceHelper: MySharedPreferences,
    @ApplicationContext private val context: Context,
    private val productRepo: ActProductRepo
) : Interceptor {
    @Throws(Exception::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val oldRequest: Request = newRequestWithAccessToken(chain.request(), preferenceHelper.accessToken.toString())
        var oldResponse = chain.proceed(oldRequest)
        if (oldResponse.code == HttpURLConnection.HTTP_UNAUTHORIZED){
            val client = OkHttpClient()
            val params = JSONObject()
            synchronized(this){
                params.put("refresh_token", preferenceHelper.refreshToken ?: "")
                val body: RequestBody = RequestBody.create(oldResponse.body?.contentType(),params.toString())
                val nRequest = Request.Builder()
                    .url("${BASE_URL}/api/${getLanguage(context)}/refresh/token")
                    .post(body)
                    .build()

                val responseRefresh = client.newCall(nRequest).execute()
                logData(responseRefresh.body?.string().toString())
                logData(responseRefresh.code.toString())
                if (responseRefresh.code == HttpURLConnection.HTTP_OK){
                    val asJsonObject = JsonParser.parseString(responseRefresh.body?.string().toString()).asJsonObject
                    val gson = Gson()
                    val resAuth: ResAuthModel = gson.fromJson(asJsonObject, ResAuthModel::class.java)
                    preferenceHelper.accessToken = resAuth.access_token
                    preferenceHelper.refreshToken = resAuth.refresh_token
                    preferenceHelper.tokenType = resAuth.token_type
                    oldResponse.close()
                    return chain.proceed(newRequestWithAccessToken(oldRequest,preferenceHelper.accessToken.toString()))
                } else {
                    preferenceHelper.clearAll()
                    productRepo.deleteTableActProduct()
                }
            }
        }
        return oldResponse
    }


    private fun newRequestWithAccessToken(request: Request,accessToken: String): Request {
        return request.newBuilder()
            .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
            .header(HttpHeaders.ACCEPT,APPLICATION_JSON)
            .header(HttpHeaders.CONTENT_TYPE,APPLICATION_JSON)
            .build()
    }
}
