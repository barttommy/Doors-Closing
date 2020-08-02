package com.tommybart.chicagotraintracker.data.network.cta

import android.content.Context
import android.util.Log
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.tommybart.chicagotraintracker.R
import com.tommybart.chicagotraintracker.data.network.cta.response.CtaApiResponse
import com.tommybart.chicagotraintracker.internal.extensions.TAG
import kotlinx.coroutines.Deferred
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

//http://lapi.transitchicago.com/api/1.0/ttarrivals.aspx?outputType=JSON&key=API_KEY&mapid=40530

const val BASE_URL = "http://lapi.transitchicago.com/api/1.0/"

interface CtaApiService {

    @GET("ttarrivals.aspx?outputType=JSON")
    fun getArrivalsAsync(
        @Query("mapid") mapIds: List<String>
    ) : Deferred<CtaApiResponse>

    companion object {
        operator fun invoke(
            context: Context
            //interceptor: Interceptor
        ) : CtaApiService {

            val requestInterceptor = Interceptor { chain ->
                val url = chain.request()
                    .url()
                    .newBuilder()
                    .addQueryParameter("key", context.getString(R.string.cta_api_key))
                    .build()

                Log.d(TAG, url.toString())

                val request = chain.request()
                    .newBuilder()
                    .url(url)
                    .build()
                return@Interceptor chain.proceed(request)
            }

            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(requestInterceptor)
                //.addInterceptor(interceptor)
                .build()

            val converterFactory = GsonConverterFactory.create(
                GsonBuilder()
                    .registerTypeAdapter(CtaApiResponse::class.java, CtaDeserializer())
                    .create()
            )

            return Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(BASE_URL)
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .addConverterFactory(converterFactory)
                .build()
                .create(CtaApiService::class.java)
        }
    }
}