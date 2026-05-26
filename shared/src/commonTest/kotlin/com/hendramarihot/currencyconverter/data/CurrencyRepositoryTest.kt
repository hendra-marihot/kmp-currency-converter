package com.hendramarihot.currencyconverter.data

import com.hendramarihot.currencyconverter.data.model.supportedCurrencies
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class CurrencyRepositoryTest {

    private val fakeApi = FakeCurrencyApi().apply {
        setRates("USD", mapOf("EUR" to 0.85, "GBP" to 0.73, "JPY" to 110.0))
        setRates("EUR", mapOf("USD" to 1.18, "GBP" to 0.86))
    }
    private val repository = CurrencyRepository(fakeApi)

    @Test
    fun getExchangeRate_returnsCorrectRate() = runTest {
        val rate = repository.getExchangeRate("USD", "EUR")

        assertEquals("USD", rate.baseCurrency)
        assertEquals("EUR", rate.targetCurrency)
        assertEquals(0.85, rate.rate)
    }

    @Test
    fun getExchangeRate_throwsForUnknownTargetCurrency() = runTest {
        assertFailsWith<IllegalArgumentException> {
            repository.getExchangeRate("USD", "XYZ")
        }
    }

    @Test
    fun getExchangeRate_cacheHitDoesNotCallApiAgain() = runTest {
        repository.getExchangeRate("USD", "EUR")
        assertEquals(1, fakeApi.callCount)

        repository.getExchangeRate("USD", "GBP")
        assertEquals(1, fakeApi.callCount)
    }

    @Test
    fun getExchangeRate_differentBaseCurrencyTriggersNewFetch() = runTest {
        repository.getExchangeRate("USD", "EUR")
        assertEquals(1, fakeApi.callCount)

        repository.getExchangeRate("EUR", "USD")
        assertEquals(2, fakeApi.callCount)
    }

    @Test
    fun getExchangeRate_concurrentFetchesForSameBaseOnlyCallApiOnce() = runTest {
        fakeApi.delayMillis = 100

        val results = (1..5).map {
            async { repository.getExchangeRate("USD", "EUR") }
        }.awaitAll()

        assertEquals(1, fakeApi.callCount)
        results.forEach { assertEquals(0.85, it.rate) }
    }

    @Test
    fun getSupportedCurrencies_returnsPredefinedList() {
        assertEquals(supportedCurrencies, repository.getSupportedCurrencies())
    }
}
