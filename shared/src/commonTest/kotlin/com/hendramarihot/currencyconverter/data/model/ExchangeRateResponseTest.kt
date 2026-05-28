package com.hendramarihot.currencyconverter.data.model

import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class ExchangeRateResponseTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun deserialize_successPayload_mapsRatesField() {
        val payload = """
            {
              "result": "success",
              "provider": "https://www.exchangerate-api.com",
              "base_code": "USD",
              "time_last_update_unix": 1700000000,
              "rates": { "USD": 1, "EUR": 0.92, "IDR": 16000.0 }
            }
        """.trimIndent()

        val response = json.decodeFromString<ExchangeRateResponse>(payload)

        assertEquals("success", response.result)
        assertEquals("USD", response.baseCode)
        val rates = assertNotNull(response.conversionRates)
        assertEquals(1.0, rates["USD"])
        assertEquals(0.92, rates["EUR"])
    }

    @Test
    fun deserialize_errorPayload_hasNullRatesAndErrorType() {
        val payload = """{"result":"error","error-type":"unsupported-code"}"""

        val response = json.decodeFromString<ExchangeRateResponse>(payload)

        assertEquals("error", response.result)
        assertEquals("unsupported-code", response.errorType)
        assertNull(response.conversionRates)
    }
}
