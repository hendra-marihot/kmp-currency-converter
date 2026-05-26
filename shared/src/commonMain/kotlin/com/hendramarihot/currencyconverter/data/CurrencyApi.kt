package com.hendramarihot.currencyconverter.data

import com.hendramarihot.currencyconverter.data.model.ExchangeRateResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

interface CurrencyApi {
    suspend fun getExchangeRates(baseCurrency: String): ExchangeRateResponse
}

class CurrencyApiImpl(private val httpClient: HttpClient) : CurrencyApi {

    override suspend fun getExchangeRates(baseCurrency: String): ExchangeRateResponse {
        return httpClient
            .get("https://open.er-api.com/v6/latest/$baseCurrency")
            .body()
    }
}
