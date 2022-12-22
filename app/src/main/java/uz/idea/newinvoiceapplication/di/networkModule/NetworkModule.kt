package uz.idea.newinvoiceapplication.di.networkModule

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import uz.idea.data.network.apiService.ApiService
import uz.idea.newinvoiceapplication.BuildConfig
import uz.idea.newinvoiceapplication.BuildConfig.BASE_URL
import uz.idea.newinvoiceapplication.app.App
import uz.idea.newinvoiceapplication.interceptor.TokenInterceptor
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

private val READ_TIMEOUT = 300
private val WRITE_TIMEOUT = 300
private val CONNECTION_TIMEOUT = 100

@Module
@Suppress("unused")
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Singleton
    @Provides
    fun provideApplication(@ApplicationContext app: Context): App {
        return app as App
    }

    @Singleton
    @Provides
    fun providesBaseUrl():String{
        return if (BuildConfig.DEBUG)  BASE_URL
        else  BASE_URL
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(@ApplicationContext context: Context, tokenInterceptor: TokenInterceptor): OkHttpClient {
        val okHttpClient = OkHttpClient().newBuilder().connectTimeout(CONNECTION_TIMEOUT.toLong(), TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT.toLong(), TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIMEOUT.toLong(), TimeUnit.SECONDS)
            .addInterceptor(tokenInterceptor)
            if (BuildConfig.DEBUG){
                okHttpClient.addInterceptor(ChuckerInterceptor(context))
            }
        return okHttpClient.build()
    }


    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun providesApiService(retrofit: Retrofit):ApiService = retrofit.create(ApiService::class.java)
}