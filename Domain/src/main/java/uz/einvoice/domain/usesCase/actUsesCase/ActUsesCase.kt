package uz.einvoice.domain.usesCase.actUsesCase

import android.content.Context
import com.google.gson.JsonElement
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.HttpException
import uz.einvoice.domain.R
import uz.einvoice.domain.repositories.apiRepository.ApiRepository
import uz.einvoice.domain.utils.AuthenticationException
import uz.einvoice.domain.utils.NetworkErrorException
import uz.einvoice.domain.utils.loadState.ResponseState
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.*
import javax.inject.Inject

class ActUsesCase @Inject constructor(
    private val apiRepository: ApiRepository,
    @ApplicationContext private val context: Context
) {


    suspend fun getDoubleUniqueId(url:String,queryMap: HashMap<String, String>) = flow {
        val flow = try {
            coroutineScope {
                val listAct =  LinkedList<JsonElement?>()
                val uniqueFirst = apiRepository.methodeGet(url, queryMap)
                val uniqueSecond = apiRepository.methodeGet(url, queryMap)
                if (uniqueFirst.isSuccessful) listAct.add(uniqueFirst.body()) else
                    throw java.lang.Exception(uniqueFirst.errorBody()?.string())
                if (uniqueSecond.isSuccessful) listAct.add(uniqueSecond.body()) else
                    throw java.lang.Exception(uniqueSecond.errorBody()?.string())
                ResponseState.Success(listAct)
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


    suspend fun getActData(
        urlCompanyInfo:String,queryMapCompany: HashMap<String,String>,
        urlBranches:String,queryBranch: HashMap<String,String>
    ) = flow {
        val flow = try {
            coroutineScope {
                val listAct =  LinkedList<JsonElement?>()
                val companyInfo = apiRepository.methodeGet(urlCompanyInfo, queryMapCompany)
                val branches = apiRepository.methodeGet(urlBranches, queryBranch)
                if (companyInfo.isSuccessful) listAct.add(companyInfo.body()) else
                    throw java.lang.Exception(companyInfo.errorBody()?.string())
                if (branches.isSuccessful) listAct.add(branches.body()) else
                    throw java.lang.Exception(branches.errorBody()?.string())
                ResponseState.Success(listAct)
            }
        }
        catch (e:Exception){
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
