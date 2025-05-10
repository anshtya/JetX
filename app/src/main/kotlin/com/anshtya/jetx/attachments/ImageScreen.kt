package com.anshtya.jetx.attachments

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.net.toUri
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import coil3.compose.AsyncImage
import com.anshtya.jetx.common.ui.BackButton
import kotlinx.serialization.Serializable

@Serializable
data class ImageScreen(val data: String)

fun NavGraphBuilder.imageScreen(
    onBackClick: () -> Unit
) {
    composable<ImageScreen> { backStackEntry ->
        ImageScreen(
            data = backStackEntry.toRoute<ImageScreen>().data,
            onBackClick = onBackClick
        )
    }
}

fun NavController.navigateToImageScreen(data: String) {
    navigate(ImageScreen(data = data))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageScreen(
    data: String,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = { BackButton(onClick = onBackClick) }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AsyncImage(
                model = data.toUri(),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxSize()
            )
        }
    }
}