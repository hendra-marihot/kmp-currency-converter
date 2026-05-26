package com.hendramarihot.currencyconverter.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ExchangeRateResponse(
    val result: String,
    @SerialName("base_code") val baseCode: String,
    @SerialName("conversion_rates") val conversionRates: Map<String, Double>,
)

data class ExchangeRate(
    val baseCurrency: String,
    val targetCurrency: String,
    val rate: Double,
    val timestamp: Long,
)
