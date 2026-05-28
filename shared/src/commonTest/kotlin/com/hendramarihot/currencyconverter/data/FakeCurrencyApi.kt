package com.hendramarihot.currencyconverter.data

import com.hendramarihot.currencyconverter.data.model.ExchangeRateResponse
import kotlinx.coroutines.delay

class FakeCurrencyApi : CurrencyApi {

    var callCount = 0
        private set

    var delayMillis: Long = 0
    var shouldThrow: Exception? = null
    var errorResponse: ExchangeRateResponse? = null

    private val rates = mutableMapOf<String, Map<String, Double>>()

    fun setRates(base: String, rates: Map<String, Double>) {
        this.rates[base] = rates
    }

    override suspend fun getExchangeRates(baseCurrency: String): ExchangeRateResponse {
        callCount++
        if (delayMillis > 0) delay(delayMillis)
        shouldThrow?.let { throw it }
        errorResponse?.let { return it }
        val conversionRates = rates[baseCurrency]
            ?: throw IllegalArgumentException("No rates configured for $baseCurrency")
        return ExchangeRateResponse(
            result = "success",
            baseCode = baseCurrency,
            conversionRates = conversionRates,
        )
    }
}
