package com.hendramarihot.currencyconverter.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ExchangeRateResponse(
    val result: String,
    @SerialName("error-type") val errorType: String? = null,
    @SerialName("base_code") val baseCode: String? = null,
    @SerialName("rates") val conversionRates: Map<String, Double>? = null,
)

data class ExchangeRate(
    val baseCurrency: String,
    val targetCurrency: String,
    val rate: Double,
    val timestamp: Long,
)
