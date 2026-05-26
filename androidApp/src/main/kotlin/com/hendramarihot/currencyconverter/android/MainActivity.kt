package com.hendramarihot.currencyconverter.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.hendramarihot.currencyconverter.android.ui.ConverterScreen
import com.hendramarihot.currencyconverter.android.ui.theme.CurrencyConverterTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CurrencyConverterTheme {
                ConverterScreen()
            }
        }
    }
}
