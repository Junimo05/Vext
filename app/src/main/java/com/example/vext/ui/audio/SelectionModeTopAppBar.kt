package com.example.vext.ui.audio

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.vext.MainActivity
import com.example.vext.model.Audio
import com.example.vext.utils.audioIntent.shareAudio


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectionModeTopAppBar(
    context: Context,
    selectedItems: SnapshotStateList<Audio>,
    resetSelectionMode: () -> Unit,
    updateNameAudio: (String, Audio) -> Unit,
    deleteAudio: (List<Audio>) -> Unit,
){
    var showConfirmMenu by remember {
        mutableStateOf(false)
    }
    var showEditName by remember {
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
                    if(selectedItems.size == 1){
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = "Edit Name",
                                )
                            },
                            onClick = {
                                isDropDownVisible = false
                                //Edit Name
                                showEditName = true
                            }
                        )
                    }
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
        val result = alertCheck()
        if (result) {
            isDropDownVisible = false
            if(ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    context as MainActivity,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    1)
                deleteAudio(selectedItems)
            } else {
                deleteAudio(selectedItems)
            }
            resetSelectionMode()
            showConfirmMenu = false
        }
    }

    //Edit Name Dialog
    if (showEditName) {
        EditNameDialog(
            oldName = selectedItems[0].displayName,
            confirm = {
                updateNameAudio(it, selectedItems[0])
                resetSelectionMode()
            },
            dismiss = {
                showEditName = false
                resetSelectionMode()
            }
        )

    }
}

@Composable
fun alertCheck(
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

@Composable
fun EditNameDialog(
    oldName: String,
    confirm: (String) -> Unit,
    dismiss: () -> Unit,
    modifier: Modifier = Modifier
){
    var name by remember {
        mutableStateOf(oldName)
    }
    AlertDialog(
        onDismissRequest = { },
        title = {
            Text(text = "Edit Name")
        },
        text = {
                TextField(
                value = name,
                onValueChange = {name = it},
                placeholder = { Text("Enter Name") },
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    confirm(name)
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    dismiss()
                }
            ) {
                Text("Cancel")
            }
        }
    )
}