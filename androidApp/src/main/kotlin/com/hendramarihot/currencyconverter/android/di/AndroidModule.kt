package com.hendramarihot.currencyconverter.android.di

import com.hendramarihot.currencyconverter.android.ui.ConverterAndroidViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val androidModule = module {
    viewModel { ConverterAndroidViewModel(get()) }
}
