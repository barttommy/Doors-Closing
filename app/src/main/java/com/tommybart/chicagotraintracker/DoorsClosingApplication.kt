package com.tommybart.chicagotraintracker

import android.app.Application
import com.tommybart.chicagotraintracker.data.db.DoorsClosingDatabase
import com.tommybart.chicagotraintracker.data.network.*
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

class DoorsClosingApplication : Application(), KodeinAware {
    override val kodein: LazyKodein = Kodein.lazy {
        import(androidXModule(this@DoorsClosingApplication))

        bind() from singleton { DoorsClosingDatabase(instance()) }
        bind() from singleton { instance<DoorsClosingDatabase>().stationDao() }
        bind<ConnectivityInterceptor>() with singleton { ConnectivityInterceptorImpl(instance()) }
        bind() from singleton { ChicagoDataPortalApiService(instance(), instance()) }
        bind<StationNetworkDataSource>() with singleton { StationNetworkDataSourceImpl(instance()) }
        bind<StationRepository>() with singleton { StationRepositoryImpl(instance(), instance()) }
        bind() from provider { ArrivalsViewModelFactory(instance()) }
    }
}