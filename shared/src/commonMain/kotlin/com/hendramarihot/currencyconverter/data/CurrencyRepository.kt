package com.hendramarihot.currencyconverter.data

import com.hendramarihot.currencyconverter.data.model.Currency
import com.hendramarihot.currencyconverter.data.model.ExchangeRate
import com.hendramarihot.currencyconverter.data.model.supportedCurrencies
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.datetime.Clock

class CurrencyRepository(private val api: CurrencyApi) {

    private val cachedRates = MutableStateFlow<Map<String, Map<String, Double>>>(emptyMap())
    private val fetchMutex = Mutex()

    fun getSupportedCurrencies(): List<Currency> = supportedCurrencies

    suspend fun getExchangeRate(from: String, to: String): ExchangeRate {
        val rate = if (from == to) {
            1.0
        } else {
            val rates = cachedRates.value[from] ?: fetchAndCacheRates(from)
            rates[to] ?: throw IllegalArgumentException("Rate not found for $to")
        }

        return ExchangeRate(
            baseCurrency = from,
            targetCurrency = to,
            rate = rate,
            timestamp = Clock.System.now().toEpochMilliseconds(),
        )
    }

    private suspend fun fetchAndCacheRates(baseCurrency: String): Map<String, Double> {
        fetchMutex.withLock {
            cachedRates.value[baseCurrency]?.let { return it }

            val response = api.getExchangeRates(baseCurrency)
            val rates = response.conversionRates
            if (response.result != "success" || rates == null) {
                throw IllegalStateException(
                    "Failed to fetch rates for $baseCurrency: ${response.errorType ?: response.result}",
                )
            }
            cachedRates.update { it + (baseCurrency to rates) }
            return rates
        }
    }
}
