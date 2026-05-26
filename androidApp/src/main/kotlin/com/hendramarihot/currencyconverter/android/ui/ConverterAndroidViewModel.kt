package com.hendramarihot.currencyconverter.android.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hendramarihot.currencyconverter.data.model.Currency
import com.hendramarihot.currencyconverter.domain.ConvertCurrencyUseCase
import com.hendramarihot.currencyconverter.presentation.ConverterUiState
import com.hendramarihot.currencyconverter.presentation.ConverterViewModel
import kotlinx.coroutines.flow.StateFlow

class ConverterAndroidViewModel(
    convertCurrencyUseCase: ConvertCurrencyUseCase,
) : ViewModel() {

    private val delegate = ConverterViewModel(
        convertCurrencyUseCase = convertCurrencyUseCase,
        scope = viewModelScope,
    )

    val uiState: StateFlow<ConverterUiState> = delegate.uiState

    fun onFromCurrencyChanged(currency: Currency) = delegate.onFromCurrencyChanged(currency)
    fun onToCurrencyChanged(currency: Currency) = delegate.onToCurrencyChanged(currency)
    fun onAmountChanged(amount: String) = delegate.onAmountChanged(amount)
    fun onSwapCurrencies() = delegate.onSwapCurrencies()
    fun onConvert() = delegate.onConvert()
    fun onErrorShown() = delegate.onErrorShown()
}
