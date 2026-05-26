package com.hendramarihot.currencyconverter.domain

import com.hendramarihot.currencyconverter.data.CurrencyRepository
import com.hendramarihot.currencyconverter.data.FakeCurrencyApi
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ConvertCurrencyUseCaseTest {

    private val fakeApi = FakeCurrencyApi().apply {
        setRates("USD", mapOf("EUR" to 0.85, "JPY" to 110.0))
    }
    private val repository = CurrencyRepository(fakeApi)
    private val useCase = ConvertCurrencyUseCase(repository)

    @Test
    fun invoke_returnsCorrectConversion() = runTest {
        val result = useCase(fromCode = "USD", toCode = "EUR", amount = 100.0)

        assertEquals("USD", result.fromCurrency.code)
        assertEquals("EUR", result.toCurrency.code)
        assertEquals(100.0, result.fromAmount)
        assertEquals(85.0, result.toAmount)
        assertEquals(0.85, result.rate)
    }

    @Test
    fun invoke_withOneUnit() = runTest {
        val result = useCase(fromCode = "USD", toCode = "JPY", amount = 1.0)

        assertEquals(110.0, result.toAmount)
    }

    @Test
    fun invoke_withZeroAmount() = runTest {
        val result = useCase(fromCode = "USD", toCode = "EUR", amount = 0.0)

        assertEquals(0.0, result.toAmount)
    }

    @Test
    fun invoke_throwsForUnknownTargetCurrency() = runTest {
        assertFailsWith<IllegalArgumentException> {
            useCase(fromCode = "USD", toCode = "XYZ", amount = 100.0)
        }
    }

    @Test
    fun invoke_throwsForUnknownFromCurrency() = runTest {
        assertFailsWith<Exception> {
            useCase(fromCode = "XYZ", toCode = "EUR", amount = 100.0)
        }
    }

    @Test
    fun invoke_populatesCorrectCurrencyObjects() = runTest {
        val result = useCase(fromCode = "USD", toCode = "EUR", amount = 50.0)

        assertEquals("US Dollar", result.fromCurrency.name)
        assertEquals("$", result.fromCurrency.symbol)
        assertEquals("Euro", result.toCurrency.name)
        assertEquals("€", result.toCurrency.symbol)
    }
}
