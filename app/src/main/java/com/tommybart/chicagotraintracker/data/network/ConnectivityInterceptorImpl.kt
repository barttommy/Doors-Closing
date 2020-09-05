package com.tommybart.chicagotraintracker.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.tommybart.chicagotraintracker.internal.NoNetworkConnectionException
import okhttp3.Interceptor
import okhttp3.Response

class ConnectivityInterceptorImpl(
    context: Context
) : ConnectivityInterceptor {

    private val appContext: Context = context.applicationContext

    override fun intercept(chain: Interceptor.Chain): Response {
        if (connectedToNetwork()) {
            return chain.proceed(chain.request())
        } else {
            throw NoNetworkConnectionException()
        }
    }

    private fun connectedToNetwork(): Boolean {
        val connectivityManager =
            appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val networkCapabilities =
            connectivityManager.getNetworkCapabilities(network) ?: return false
        return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}