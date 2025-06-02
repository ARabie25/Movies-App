package com.example.moviesapp.app

import android.app.Application
import com.example.moviesapp.di.appModule
import com.example.moviesapp.di.networkModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MoviesApp: Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MoviesApp)
            modules(appModule, networkModule)
        }
    }

}