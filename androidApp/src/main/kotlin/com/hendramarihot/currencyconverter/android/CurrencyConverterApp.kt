package com.hendramarihot.currencyconverter.android

import android.app.Application
import com.hendramarihot.currencyconverter.android.di.androidModule
import com.hendramarihot.currencyconverter.di.sharedModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class CurrencyConverterApp : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@CurrencyConverterApp)
            modules(sharedModule, androidModule)
        }
    }
}
