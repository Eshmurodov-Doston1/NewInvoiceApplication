package uz.idea.domain.usesCase.apiUsesCase

import android.content.Context
import com.google.gson.Gson
import com.google.gson.JsonElement
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.HttpException
import uz.idea.domain.R
import uz.idea.domain.models.measure.Measure
import uz.idea.domain.repositories.apiRepository.ApiRepository
import uz.idea.domain.repositories.dataBaseRepository.measureRepo.MeasureRepo
import uz.idea.domain.utils.AuthenticationException
import uz.idea.domain.utils.NetworkErrorException
import uz.idea.domain.utils.loadState.ResponseState
import uz.idea.domain.utils.resPonseFetcher.ResponseFetcher
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject

class ApiUsesCase @Inject constructor(
    private val apiRepository: ApiRepository,
    private val responseFetcher: ResponseFetcher.Base,
    private val measureRepo: MeasureRepo,
    @ApplicationContext private val context: Context
) {
   suspend fun methodeGet(url:String,queryMap: HashMap<String,String>)
   = responseFetcher.getFlowResponseState(apiRepository.methodeGet(url, queryMap))

    suspend fun methodePOST(url:String,body:Any,queryMap: HashMap<String,String>)
    = responseFetcher.getFlowResponseState(apiRepository.methodePost(url,body, queryMap))

    suspend fun methodePUT(url:String,body:Any,queryMap: HashMap<String,String>)
    = responseFetcher.getFlowResponseState(apiRepository.methodePut(url,body, queryMap))

    suspend fun methodeDELETE(url:String,body:Any,queryMap: HashMap<String,String>)
    = responseFetcher.getFlowResponseState(apiRepository.methodeDelete(url,body, queryMap))

    suspend fun getMeasureList(url:String,queryMap: HashMap<String,String>) = flow {
        val flow = try {
            coroutineScope {
                val measureResponse = apiRepository.methodeGet(url, queryMap)
                if (measureResponse.isSuccessful){
                    val measure = measureResponse.body()?.parseClass(Measure::class.java)
                    measureRepo.saveMeasureEntity(measure?.getMeasureList()?: emptyList())
                    ResponseState.Success(measureRepo.getAllMeasureEntity())
                } else throw java.lang.Exception(measureResponse.errorBody()?.string())
            }
        } catch (e:Exception){
            var error = e
            when (e) {
                is SocketTimeoutException -> {
                    error = NetworkErrorException(errorMessage = "connection error!")
                }
                is ConnectException -> {
                    error = NetworkErrorException(errorMessage = context.getString(R.string.no_internet))
                }
                is UnknownHostException -> {
                    error = NetworkErrorException(errorMessage = context.getString(R.string.no_internet))
                }
            }
            if(e is HttpException){
                when(e.code()){
                    in 500..599 -> {
                        error = NetworkErrorException(e.code(), context.getString(R.string.server_error))
                    }
                    401 -> {
                        throw AuthenticationException("authentication error!",e.code())
                    }
                    400 -> {
                        error = NetworkErrorException.parseException(e)
                    }
                    in 402..499 -> {
                        error = NetworkErrorException.parseException(e)
                    }
                }
            }
            ResponseState.Error(error)
        }
        emit(flow)
    }.flowOn(Dispatchers.IO)


}

fun <T> JsonElement.parseClass(classData:Class<T>):T{
    val gson = Gson()
    return gson.fromJson(this,classData)
}