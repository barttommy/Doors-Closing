package com.tommybart.chicagotraintracker

import android.app.Application
import android.content.Context
import androidx.preference.PreferenceManager
import com.google.android.gms.location.LocationServices
import com.jakewharton.threetenabp.AndroidThreeTen
import com.tommybart.chicagotraintracker.data.db.DoorsClosingDatabase
import com.tommybart.chicagotraintracker.data.network.ConnectivityInterceptor
import com.tommybart.chicagotraintracker.data.network.ConnectivityInterceptorImpl
import com.tommybart.chicagotraintracker.data.network.chicagodataportal.SodaApiService
import com.tommybart.chicagotraintracker.data.network.chicagodataportal.StationNetworkDataSource
import com.tommybart.chicagotraintracker.data.network.chicagodataportal.StationNetworkDataSourceImpl
import com.tommybart.chicagotraintracker.data.network.chicagotransitauthority.CtaApiService
import com.tommybart.chicagotraintracker.data.network.chicagotransitauthority.RouteArrivalsNetworkDataSource
import com.tommybart.chicagotraintracker.data.network.chicagotransitauthority.RouteArrivalsNetworkDataSourceImpl
import com.tommybart.chicagotraintracker.data.provider.*
import com.tommybart.chicagotraintracker.data.repository.RouteArrivalsRepository
import com.tommybart.chicagotraintracker.data.repository.RouteArrivalsRepositoryImpl
import com.tommybart.chicagotraintracker.data.repository.StationRepository
import com.tommybart.chicagotraintracker.data.repository.StationRepositoryImpl
import com.tommybart.chicagotraintracker.ui.activities.main.arrivals.ArrivalsViewModelFactory
import com.tommybart.chicagotraintracker.ui.activities.search.SearchViewModelFactory
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.LazyKodein
import org.kodein.di.android.x.androidXModule
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider
import org.kodein.di.generic.singleton

@Suppress("unused")
class DoorsClosingApplication : Application(), KodeinAware {
    override val kodein: LazyKodein = Kodein.lazy {
        import(androidXModule(this@DoorsClosingApplication))

        // Database
        bind() from singleton { DoorsClosingDatabase(instance()) }
        bind() from singleton { instance<DoorsClosingDatabase>().stationDao() }
        bind() from singleton { instance<DoorsClosingDatabase>().stationInfoDao() }
        bind() from singleton { instance<DoorsClosingDatabase>().routeArrivalsDao() }
        bind() from singleton { instance<DoorsClosingDatabase>().routeArrivalsInfoDao() }
        bind() from singleton { instance<DoorsClosingDatabase>().routeArrivalsRequestDao() }

        // API Services
        bind<ConnectivityInterceptor>() with singleton { ConnectivityInterceptorImpl(instance()) }
        bind() from singleton { SodaApiService(instance(), instance()) }
        bind() from singleton { CtaApiService(instance(), instance()) }

        // Location
        bind() from provider { LocationServices.getFusedLocationProviderClient(instance<Context>()) }

        // Providers
        bind<PreferenceProvider>() with singleton { PreferenceProviderImpl(instance()) }
        bind<NearbyStationsProvider>() with singleton { NearbyStationsProviderImpl(instance()) }
        bind<RequestedStationsProvider>() with singleton {
            RequestedStationsProviderImpl(
                instance(),
                instance(),
                instance(),
                instance()
            )
        }

        // Station
        bind<StationNetworkDataSource>() with singleton { StationNetworkDataSourceImpl(instance()) }
        bind<StationRepository>() with singleton {
            StationRepositoryImpl(
                instance(),
                instance(),
                instance()
            )
        }

        // RouteArrivals
        bind<RouteArrivalsNetworkDataSource>() with singleton {
            RouteArrivalsNetworkDataSourceImpl(
                instance()
            )
        }
        bind<RouteArrivalsRepository>() with singleton {
            RouteArrivalsRepositoryImpl(
                instance(),
                instance(),
                instance(),
                instance(),
                instance()
            )
        }

        // ArrivalsFragment ViewModelFactory
        bind() from provider { ArrivalsViewModelFactory(instance(), instance()) }

        // SearchActivity ViewModelFactory
        bind() from provider { SearchViewModelFactory(instance()) }
    }

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false)
    }
}