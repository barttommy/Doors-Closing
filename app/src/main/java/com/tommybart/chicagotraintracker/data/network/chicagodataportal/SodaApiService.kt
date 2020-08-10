package com.tommybart.chicagotraintracker.data.network.chicagodataportal

import android.content.Context
import android.util.Log
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.tommybart.chicagotraintracker.R
import com.tommybart.chicagotraintracker.internal.extensions.TAG
import kotlinx.coroutines.Deferred
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

//https://data.cityofchicago.org/resource/8pix-ypme.json?$$app_token=APP_TOKEN_GOES_HERE

const val BASE_URL = "https://data.cityofchicago.org/resource/"
const val CHECK_FOR_UPDATES_DELAY_DAYS: Long = 1

interface SodaApiService {

    @GET("8pix-ypme.json")
    fun getStationDataAsync(): Deferred<SodaApiResponse>

    /*
     * To see if the data set has changed, we have to query with :updated_at to see any new entries
     * since our last request. However, we can't update our existing data with these results because
     * it doesn't account for when stations are obsoleted/deleted from the db (so updating would
     * result in obsolete data in the db). We don't want this because the CTA API
     * would throw an error when these stations are requested because their old location is nearby
     * the user.
     *
     * We also can't update according the the Last-Modified field in the header. This information
     * is supposed to be included in the header for version 2.1 APIs, but parsing the header
     * shows that it isn't there. So the best way seems to be to check for updates with the query,
     * then replace the old data with the new data if necessary.
     *
     * https://dev.socrata.com/docs/response-codes.html
     * https://dev.socrata.com/docs/system-fields.html
     */

    @GET("8pix-ypme.json")
    fun getStationsWhereAsync(
        @Query("\$where") whereQuery: String
    ): Deferred<SodaApiResponse>

    companion object {
        operator fun invoke(
            context: Context,
            interceptor: Interceptor
        ) : SodaApiService {

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

            val converterFactory = GsonConverterFactory.create(
                GsonBuilder()
                    .registerTypeAdapter(SodaApiResponse::class.java, SodaResponseDeserializer())
                    .create()
            )

            return Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(BASE_URL)
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .addConverterFactory(converterFactory)
                .build()
                .create(SodaApiService::class.java)
        }
    }
}