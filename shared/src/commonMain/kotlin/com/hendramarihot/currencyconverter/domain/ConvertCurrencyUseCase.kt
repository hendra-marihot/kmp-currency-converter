package com.hendramarihot.currencyconverter.domain

import com.hendramarihot.currencyconverter.data.CurrencyRepository
import com.hendramarihot.currencyconverter.data.model.ConversionResult
import com.hendramarihot.currencyconverter.data.model.supportedCurrencies

class ConvertCurrencyUseCase(private val repository: CurrencyRepository) {

    suspend operator fun invoke(
        fromCode: String,
        toCode: String,
        amount: Double,
    ): ConversionResult {
        val exchangeRate = repository.getExchangeRate(fromCode, toCode)
        val convertedAmount = amount * exchangeRate.rate

        return ConversionResult(
            fromCurrency = supportedCurrencies.firstOrNull { it.code == fromCode }
                ?: throw IllegalArgumentException("Unsupported currency: $fromCode"),
            toCurrency = supportedCurrencies.firstOrNull { it.code == toCode }
                ?: throw IllegalArgumentException("Unsupported currency: $toCode"),
            fromAmount = amount,
            toAmount = convertedAmount,
            rate = exchangeRate.rate,
        )
    }
}
