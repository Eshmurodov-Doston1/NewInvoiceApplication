package uz.idea.domain.utils.resPonseFetcher

import android.content.Context
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.HttpException
import retrofit2.Response
import uz.idea.domain.R
import uz.idea.domain.utils.AuthenticationException
import uz.idea.domain.utils.NetworkErrorException
import uz.idea.domain.utils.loadState.ResponseState
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject

interface ResponseFetcher{
    fun <T> getFlowResponseState(response: Response<T>?): Flow<ResponseState<T?>>

    class Base @Inject constructor(
        @ApplicationContext private val context: Context
    ): ResponseFetcher {
        override fun <T> getFlowResponseState(response: Response<T>?) = flow {
            var flow = try {
                coroutineScope {
                    if (response?.isSuccessful == true) ResponseState.Success(response.body())
                    else throw java.lang.Exception(response?.errorBody()?.string())
                }
            } catch (e:Exception){
                var error = e
                Log.e("ErrorResponseFetcher", e.message.toString())
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

}