package com.anshtya.jetx.ui.features

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.anshtya.jetx.R

@Composable
fun OnboardingScreen(
    onCreateAccountClick: () -> Unit,
    onSignInClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = stringResource(id = R.string.app_name),
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(18.dp))
        Button(
            onClick = onCreateAccountClick,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .height(48.dp)
                .width(250.dp)
        ) {
            Text(text = stringResource(id = R.string.create_account))
        }
        Spacer(Modifier.height(10.dp))
        Button(
            onClick = onSignInClick,
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                contentColor = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier
                .height(48.dp)
                .width(250.dp)
        ) {
            Text(text = stringResource(id = R.string.sign_in))
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun OnboardingScreenPreview() {
    OnboardingScreen(
        onCreateAccountClick = {},
        onSignInClick = {}
    )
}