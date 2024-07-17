package com.example.vext.ui.audio

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.example.vext.data.local.model.Audio
import com.example.vext.utils.audioIntent.shareAudio
import com.example.vext.utils.getRealPathFromURI
import kotlinx.coroutines.CoroutineScope
import java.io.File


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectionModeTopAppBar(
    context: Context,
    selectedItems: SnapshotStateList<Audio>,
    resetSelectionMode: () -> Unit,
    deleteAudio: (List<Audio>) -> Unit,
){
    var showConfirmMenu by remember {
        mutableStateOf(false)
    }
    var isDropDownVisible by remember {
        mutableStateOf(false)
    }
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
                                text = "Share",
                            )
                        },
                        onClick = {
                            isDropDownVisible = false
                            shareAudio(context, selectedItems)
//                            showBottomSheet.value = true
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
                            showConfirmMenu = true
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
    //alert dialog
    if (showConfirmMenu) {
        val result = AlertCheck()
        if (result) {
            isDropDownVisible = false
            deleteAudio(selectedItems)
            resetSelectionMode()
            showConfirmMenu = false
        }
    }
}

@Composable
fun AlertCheck(
    modifier: Modifier = Modifier
): Boolean {
    var result by remember {
        mutableStateOf(false)
    }
    AlertDialog(
        onDismissRequest = { },
        title = {
            Text(text = "Delete")
        },
        text = {
            Text(text = "Are you sure you want to delete ?")
        },
        confirmButton = {
            TextButton(
                onClick = {result = true}
            ) {
                Text("Yes")
            }
        },
        dismissButton = {
            TextButton(
                onClick = { result = false }
            ) {
                Text("Cancel")
            }
        }
    )
    return result
}