package com.hendramarihot.currencyconverter.presentation

import com.hendramarihot.currencyconverter.data.model.ConversionResult
import com.hendramarihot.currencyconverter.data.model.Currency
import com.hendramarihot.currencyconverter.data.model.supportedCurrencies
import com.hendramarihot.currencyconverter.domain.ConvertCurrencyUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ConverterUiState(
    val currencies: List<Currency> = supportedCurrencies,
    val fromCurrency: Currency = supportedCurrencies[0],
    val toCurrency: Currency = supportedCurrencies.first { it.code == "IDR" },
    val amount: String = "1.00",
    val result: ConversionResult? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
)

class ConverterViewModel(
    private val convertCurrencyUseCase: ConvertCurrencyUseCase,
    private val scope: CoroutineScope,
) {
    private val _uiState = MutableStateFlow(ConverterUiState())
    val uiState: StateFlow<ConverterUiState> = _uiState.asStateFlow()

    fun onFromCurrencyChanged(currency: Currency) {
        _uiState.update { it.copy(fromCurrency = currency, result = null) }
    }

    fun onToCurrencyChanged(currency: Currency) {
        _uiState.update { it.copy(toCurrency = currency, result = null) }
    }

    fun onAmountChanged(amount: String) {
        _uiState.update { it.copy(amount = amount, result = null) }
    }

    fun onSwapCurrencies() {
        _uiState.update {
            it.copy(
                fromCurrency = it.toCurrency,
                toCurrency = it.fromCurrency,
                result = null,
            )
        }
    }

    fun onConvert() {
        val state = _uiState.value
        val amount = state.amount.toDoubleOrNull() ?: return

        scope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val result = convertCurrencyUseCase(
                    fromCode = state.fromCurrency.code,
                    toCode = state.toCurrency.code,
                    amount = amount,
                )
                _uiState.update { it.copy(result = result, isLoading = false) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(error = e.message ?: "Conversion failed", isLoading = false)
                }
            }
        }
    }

    fun onErrorShown() {
        _uiState.update { it.copy(error = null) }
    }
}
