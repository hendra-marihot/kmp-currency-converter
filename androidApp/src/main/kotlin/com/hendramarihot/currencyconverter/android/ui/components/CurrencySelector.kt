package com.hendramarihot.currencyconverter.android.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.hendramarihot.currencyconverter.data.model.Currency

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencySelector(
    label: String,
    selectedCurrency: Currency,
    currencies: List<Currency>,
    onCurrencySelected: (Currency) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier,
    ) {
        OutlinedTextField(
            value = "${selectedCurrency.flag} ${selectedCurrency.code} - ${selectedCurrency.name}",
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(type = MenuAnchorType.PrimaryNotEditable),
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            currencies.forEach { currency ->
                DropdownMenuItem(
                    text = { Text("${currency.flag} ${currency.code} - ${currency.name}") },
                    onClick = {
                        onCurrencySelected(currency)
                        expanded = false
                    },
                )
            }
        }
    }
}

private val Currency.flag: String
    get() = when (code) {
        "USD" -> "🇺🇸"
        "EUR" -> "🇪🇺"
        "GBP" -> "🇬🇧"
        "JPY" -> "🇯🇵"
        "IDR" -> "🇮🇩"
        "SGD" -> "🇸🇬"
        "AUD" -> "🇦🇺"
        "CAD" -> "🇨🇦"
        "CHF" -> "🇨🇭"
        "CNY" -> "🇨🇳"
        "INR" -> "🇮🇳"
        "KRW" -> "🇰🇷"
        "MYR" -> "🇲🇾"
        "THB" -> "🇹🇭"
        "PHP" -> "🇵🇭"
        else -> ""
    }
