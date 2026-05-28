package com.hendramarihot.currencyconverter.android.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType

private val AMOUNT_REGEX = Regex("^\\d*\\.?\\d*$")

@Composable
fun AmountInput(
    amount: String,
    onAmountChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        value = amount,
        onValueChange = { input ->
            if (input.isEmpty() || input.matches(AMOUNT_REGEX)) {
                onAmountChanged(input)
            }
        },
        label = { Text("Amount") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        singleLine = true,
        modifier = modifier.fillMaxWidth(),
    )
}
