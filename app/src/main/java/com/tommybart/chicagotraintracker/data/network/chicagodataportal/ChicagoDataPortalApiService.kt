package com.tommybart.chicagotraintracker.data.network.chicagodataportal

import android.content.Context
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.tommybart.chicagotraintracker.R
import com.tommybart.chicagotraintracker.data.db.entity.StationEntry
import kotlinx.coroutines.Deferred
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

//https://data.cityofchicago.org/resource/8pix-ypme.json?$$app_token=APP_TOKEN_GOES_HERE

const val BASE_URL = "https://data.cityofchicago.org/resource/"

interface ChicagoDataPortalApiService {

    @GET("8pix-ypme.json")
    fun getStationDataAsync(): Deferred<List<StationEntry>>

    companion object {
        operator fun invoke(
            context: Context,
            interceptor: Interceptor
        ) : ChicagoDataPortalApiService {
            val requestInterceptor = Interceptor { chain ->
                val url = chain.request()
                        .url()
                        .newBuilder()
                        .addQueryParameter("\$\$app_token", context.getString(R.string.cdp_app_token))
                        .build()
                val request = chain.request()
                        .newBuilder()
                        .url(url)
                        .build()
                return@Interceptor chain.proceed(request)
            }

            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(requestInterceptor)
                .addInterceptor(interceptor)
                .build()

            return Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(BASE_URL)
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ChicagoDataPortalApiService::class.java)
        }
    }
}