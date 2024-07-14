package com.example.vext.ui.audio

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectionModeTopAppBar(
    selectedItems: SnapshotStateList<Long>,
    resetSelectionMode: () -> Unit
){
    TopAppBar(
        title = {
            Text(
                text = "${selectedItems.size} selected",
                style = MaterialTheme.typography.titleLarge.copy(
                    color = MaterialTheme.colorScheme.onBackground,
                ),
            )
        },
        navigationIcon = {
            IconButton(
                onClick = resetSelectionMode,
            ) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onBackground,
                )
            }
        },
        actions = {
            var isDropDownVisible by remember {
                mutableStateOf(false)
            }
            Box(
                modifier = Modifier,
            ) {
                IconButton(
                    onClick = {
                        isDropDownVisible = true
                    },
                ) {
                    Icon(
                        imageVector = Icons.Rounded.MoreVert,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onBackground,
                    )
                }
                DropdownMenu(
                    expanded = isDropDownVisible,
                    onDismissRequest = {
                        isDropDownVisible = false
                    }
                ) {
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = "Move",
                            )
                        },
                        onClick = {
                            isDropDownVisible = false
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Outlined.Edit,
                                contentDescription = null
                            )
                        },
                    )
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = "Delete",
                            )
                        },
                        onClick = {
                            isDropDownVisible = false
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Outlined.Delete,
                                contentDescription = null
                            )
                        },
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
        ),
    )
}