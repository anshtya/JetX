package com.anshtya.jetx.registration.components

import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import com.anshtya.jetx.ui.theme.JetXTheme
import com.google.i18n.phonenumbers.PhoneNumberUtil

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CountryCodeDropdownMenu(
    code: String,
    onCodeChange: (String) -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    val phoneUtil = remember { PhoneNumberUtil.getInstance() }
    val countryCodes = remember { phoneUtil.supportedCallingCodes }
    val results = remember(code) {
        countryCodes.filter { supportedCode ->
            val supportedCodeString = supportedCode.toString()
            supportedCodeString.contains(code)
        }
    }

    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = TextFieldValue("+$code", TextRange(code.length + 1)),
            onValueChange = {
                onCodeChange(it.text.substringAfter("+"))
                expanded = true
            },
            enabled = enabled,
            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryEditable),
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            )
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            results.forEach { code ->
                DropdownMenuItem(
                    text = { Text("+$code") },
                    onClick = {
                        onCodeChange("$code")
                        expanded = false
                    }
                )
            }
        }
    }
}

@Preview
@Composable
private fun CountryCodePreview() {
    JetXTheme {
        CountryCodeDropdownMenu(
            code = "1",
            onCodeChange = {},
            enabled = true
        )
    }
}