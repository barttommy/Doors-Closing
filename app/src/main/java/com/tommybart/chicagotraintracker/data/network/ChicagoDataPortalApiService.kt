package com.tommybart.chicagotraintracker.data.network

import android.content.Context
import android.util.Log
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
interface ChicagoDataPortalApiService {

    @GET("resource/8pix-ypme.json")
    fun getStationDataAsync(): Deferred<List<StationEntry>>

    companion object {
        operator fun invoke(
            context: Context,
            interceptor: Interceptor
        ): ChicagoDataPortalApiService {
            val requestInterceptor = Interceptor { chain ->
                val url = chain.request()
                        .url()
                        .newBuilder()
                        .addQueryParameter("\$\$app_token", context.getString(R.string.cdp_app_token))
                        .build()
                Log.d("TAG", url.toString())
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
                    .baseUrl("https://data.cityofchicago.org/")
                    .addCallAdapterFactory(CoroutineCallAdapterFactory())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(ChicagoDataPortalApiService::class.java)
        }
    }
}