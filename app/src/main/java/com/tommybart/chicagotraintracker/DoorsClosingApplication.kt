package com.tommybart.chicagotraintracker

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen
import com.tommybart.chicagotraintracker.data.db.DoorsClosingDatabase
import com.tommybart.chicagotraintracker.data.network.*
import com.tommybart.chicagotraintracker.data.network.chicagodataportal.ChicagoDataPortalApiService
import com.tommybart.chicagotraintracker.data.network.chicagodataportal.StationNetworkDataSource
import com.tommybart.chicagotraintracker.data.network.chicagodataportal.StationNetworkDataSourceImpl
import com.tommybart.chicagotraintracker.data.network.cta.RouteArrivalsNetworkDataSource
import com.tommybart.chicagotraintracker.data.network.cta.RouteArrivalsNetworkDataSourceImpl
import com.tommybart.chicagotraintracker.data.network.cta.CtaApiService
import com.tommybart.chicagotraintracker.data.repository.RouteArrivalsRepository
import com.tommybart.chicagotraintracker.data.repository.RouteArrivalsRepositoryImpl
import com.tommybart.chicagotraintracker.data.repository.StationRepository
import com.tommybart.chicagotraintracker.data.repository.StationRepositoryImpl
import com.tommybart.chicagotraintracker.ui.arrivals.ArrivalsViewModelFactory
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

        bind() from singleton { DoorsClosingDatabase(instance()) }
        bind() from singleton { instance<DoorsClosingDatabase>().stationDao() }
        bind() from singleton { instance<DoorsClosingDatabase>().responseInfoDao() }
        bind() from singleton { instance<DoorsClosingDatabase>().routeDao() }

        bind<ConnectivityInterceptor>() with singleton { ConnectivityInterceptorImpl(instance()) }
        bind() from singleton { ChicagoDataPortalApiService(instance(), instance()) }
        bind() from singleton { CtaApiService(instance(), instance())}

        bind<StationNetworkDataSource>() with singleton { StationNetworkDataSourceImpl(instance()) }
        bind<StationRepository>() with singleton { StationRepositoryImpl(instance(), instance()) }

        bind<RouteArrivalsNetworkDataSource>() with singleton { RouteArrivalsNetworkDataSourceImpl(instance()) }
        bind<RouteArrivalsRepository>() with singleton { RouteArrivalsRepositoryImpl(instance(), instance(), instance()) }

        bind() from provider { ArrivalsViewModelFactory(instance(), instance()) }
    }

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
    }
}