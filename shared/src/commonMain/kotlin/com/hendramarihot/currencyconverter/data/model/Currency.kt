package com.hendramarihot.currencyconverter.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Currency(
    val code: String,
    val name: String,
    val symbol: String,
)

val supportedCurrencies = listOf(
    Currency("USD", "US Dollar", "$"),
    Currency("EUR", "Euro", "€"),
    Currency("GBP", "British Pound", "£"),
    Currency("JPY", "Japanese Yen", "¥"),
    Currency("IDR", "Indonesian Rupiah", "Rp"),
    Currency("SGD", "Singapore Dollar", "S$"),
    Currency("AUD", "Australian Dollar", "A$"),
    Currency("CAD", "Canadian Dollar", "C$"),
    Currency("CHF", "Swiss Franc", "CHF"),
    Currency("CNY", "Chinese Yuan", "¥"),
    Currency("INR", "Indian Rupee", "₹"),
    Currency("KRW", "South Korean Won", "₩"),
    Currency("MYR", "Malaysian Ringgit", "RM"),
    Currency("THB", "Thai Baht", "฿"),
    Currency("PHP", "Philippine Peso", "₱"),
)
