package com.tommybart.chicagotraintracker.data.network.chicagotransitauthority

import android.content.Context
import androidx.lifecycle.LiveData
import com.tommybart.chicagotraintracker.R
import com.tommybart.chicagotraintracker.data.network.ApiResponse
import com.tommybart.chicagotraintracker.data.network.LiveDataCallAdapterFactory
import com.tommybart.chicagotraintracker.data.network.chicagotransitauthority.response.CtaApiResponse
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

//http://lapi.transitchicago.com/api/1.0/ttarrivals.aspx?outputType=JSON&key=API_KEY&mapid=40530

const val BASE_URL = "http://lapi.transitchicago.com/api/1.0/"
const val CTA_FETCH_DELAY_MINUTES: Long = 1

interface CtaApiService {

    @GET("ttarrivals.aspx?outputType=JSON")
    fun getArrivals(
        @Query("mapid") requestedStationMapIds: List<Int>
    ): LiveData<ApiResponse<CtaApiResponse>>

    companion object {
        operator fun invoke(
            context: Context,
            interceptor: Interceptor
        ): CtaApiService {

            val requestInterceptor = Interceptor { chain ->
                val url = chain.request()
                    .url()
                    .newBuilder()
                    .addQueryParameter("key", context.getString(R.string.cta_api_key))
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
                .addCallAdapterFactory(LiveDataCallAdapterFactory())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(CtaApiService::class.java)
        }
    }
}