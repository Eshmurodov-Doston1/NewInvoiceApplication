package uz.idea.domain.usesCase.actUsesCase

import android.content.Context
import com.google.gson.JsonElement
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.HttpException
import uz.idea.domain.R
import uz.idea.domain.models.measure.Measure
import uz.idea.domain.models.tinOrPinfl.pinfl.PinflModel
import uz.idea.domain.models.tinOrPinfl.tin456.Physical
import uz.idea.domain.models.tinOrPinfl.tinJuridic.LegalModel
import uz.idea.domain.repositories.apiRepository.ApiRepository
import uz.idea.domain.usesCase.apiUsesCase.parseClass
import uz.idea.domain.utils.AuthenticationException
import uz.idea.domain.utils.NetworkErrorException
import uz.idea.domain.utils.loadState.ResponseState
import uz.idea.domain.utils.resPonseFetcher.ResponseFetcher
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.LinkedList
import javax.inject.Inject

class ActUsesCase @Inject constructor(
    private val apiRepository: ApiRepository,
    @ApplicationContext private val context: Context
) {
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
