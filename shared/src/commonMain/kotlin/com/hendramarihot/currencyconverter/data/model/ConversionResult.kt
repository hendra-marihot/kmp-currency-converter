package com.hendramarihot.currencyconverter.data.model

data class ConversionResult(
    val fromCurrency: Currency,
    val toCurrency: Currency,
    val fromAmount: Double,
    val toAmount: Double,
    val rate: Double,
)
