package com.hendramarihot.currencyconverter.di

import com.hendramarihot.currencyconverter.data.CurrencyApi
import com.hendramarihot.currencyconverter.data.CurrencyApiImpl
import com.hendramarihot.currencyconverter.data.CurrencyRepository
import com.hendramarihot.currencyconverter.domain.ConvertCurrencyUseCase
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.dsl.module

val sharedModule = module {
    single {
        HttpClient {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
            install(HttpTimeout) {
                requestTimeoutMillis = 10_000
                connectTimeoutMillis = 10_000
            }
        }
    }
    single<CurrencyApi> { CurrencyApiImpl(get()) }
    single { CurrencyRepository(get()) }
    single { ConvertCurrencyUseCase(get()) }
}
