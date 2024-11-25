package com.lenincompany.mychat

import android.app.Application
import com.lenincompany.mychat.data.DataRepository
import com.lenincompany.mychat.data.TokenManager
import com.lenincompany.mychat.network.TokenRefresher
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication


class MainApplication : DaggerApplication(){

    lateinit var appComponent: AppComponent
    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.builder()
            .context(this)
            .build()

        appComponent.inject(this)
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerAppComponent.builder()
            .context(applicationContext)
            .build()
    }
}