package com.tommybart.chicagotraintracker

import android.app.Application
import androidx.preference.PreferenceManager
import com.jakewharton.threetenabp.AndroidThreeTen
import com.tommybart.chicagotraintracker.data.db.DoorsClosingDatabase
import com.tommybart.chicagotraintracker.data.network.*
import com.tommybart.chicagotraintracker.data.network.chicagodataportal.ChicagoDataPortalApiService
import com.tommybart.chicagotraintracker.data.network.chicagodataportal.StationNetworkDataSource
import com.tommybart.chicagotraintracker.data.network.chicagodataportal.StationNetworkDataSourceImpl
import com.tommybart.chicagotraintracker.data.network.cta.RouteArrivalsNetworkDataSource
import com.tommybart.chicagotraintracker.data.network.cta.RouteArrivalsNetworkDataSourceImpl
import com.tommybart.chicagotraintracker.data.network.cta.CtaApiService
import com.tommybart.chicagotraintracker.data.provider.RequestedStationsProvider
import com.tommybart.chicagotraintracker.data.provider.RequestedStationsProviderImpl
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

        // API Services
        bind<ConnectivityInterceptor>() with singleton { ConnectivityInterceptorImpl(instance()) }
        bind() from singleton { ChicagoDataPortalApiService(instance(), instance()) }
        bind() from singleton { CtaApiService(instance(), instance())}

        // Station
        bind<StationNetworkDataSource>() with singleton { StationNetworkDataSourceImpl(instance()) }
        bind<StationRepository>() with singleton { StationRepositoryImpl(instance(), instance(), instance()) }

        // RouteArrivals
        bind<RouteArrivalsNetworkDataSource>() with singleton { RouteArrivalsNetworkDataSourceImpl(instance()) }
        bind<RouteArrivalsRepository>() with singleton { RouteArrivalsRepositoryImpl(instance(), instance(), instance()) }

        // Arrivals ViewModelFactory
        bind<RequestedStationsProvider>() with singleton { RequestedStationsProviderImpl(instance()) }
        bind() from provider { ArrivalsViewModelFactory(instance(), instance()) }

        // Search ViewModelFactory
        bind() from provider { SearchViewModelFactory(instance())}
    }

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false)
    }
}