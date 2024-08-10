package com.example.vext.ui.audio

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.vext.utils.TrashBin

@Composable
fun TrashBinScreen(
    navController: NavController,
    trashBin: TrashBin
) {
    Scaffold(
        topBar = {},
        bottomBar = {},

    ) {
        LazyColumn(
            contentPadding = it
        ){

        }
    }
}