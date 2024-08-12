package com.example.vext.ui.audio

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableLongState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.vext.recorder.recorder.AndroidAudioRecorder
import kotlinx.coroutines.delay

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "UnrememberedMutableState",
    "ProduceStateDoesNotAssignValue", "MutableCollectionMutableState"
)
@Composable
fun RecordScreen(
    context: Context,
    reloadData: () -> Unit,
    navController: NavController
) {

    //Record State
    val recorder by lazy {
        AndroidAudioRecorder(context = context, reloadData = reloadData)
    }
    var showAlertDialog by rememberSaveable {
        mutableStateOf(false)
    }
    var isRecording by mutableStateOf(false)
    var isPaused by mutableStateOf(false)

    //Amplitudes
    val amplitudes = remember {
        mutableStateOf(mutableListOf<Float>())
    }

    LaunchedEffect(key1 = recorder) {
        while(!recorder.isPaused){
            val amplitude = recorder.getAmplitude().toFloat()
            amplitudes.value.add(amplitude)
            delay(10)
        }
    }

//    val amplitudes = produceState(initialValue = mutableStateListOf<Float>(), producer = {
//        while (!recorder.isPaused) {
//            val amplitude = recorder.getAmplitude().toFloat()
//            value.add(amplitude)
//            delay(10)
//        }
//    })

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Title
                Text(
                    text = "Record Audio",
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        fontStyle = FontStyle.Normal
                    ),
                    modifier = Modifier.align(Alignment.Center)
                )

                // Back Button
                IconButton(
                    onClick = {
                        navController.popBackStack()
                    },
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBackIosNew,
                        contentDescription = "Back"
                    )
                }
            }
        },

        bottomBar = {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                ,
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ){
                IconButton(
                    onClick = {
                        if(!isRecording){
                            recorder.start()
                            isRecording = true
                        } else {
                            if(recorder.isPaused) {
                                recorder.resume()
                                isPaused = false
                            } else {
                                recorder.pause()
                                isPaused = true
                            }
                        }
                    },
                    modifier = Modifier.size(100.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.FiberManualRecord,
                        contentDescription = "Record",
                        modifier = Modifier.size(100.dp)
                    )
                }
            }
        }
    ){
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
        ) {
            Timer(
                recordingTime = recorder.recordingTime
            )
            AudioFingerprintDisplay(amplitudes.value)
        }

        //Save file
        if(showAlertDialog){
            StopAlertDialog(
                onSaveRequest = {filename ->
                    showAlertDialog = false
                    isRecording = false
                    recorder.stop(filename)
                },
                onDismissRequest = {
                    showAlertDialog = false
                    isRecording = false
                    recorder.cancel()
                }
            )
        }
    }
}

@Composable
fun Timer(
    recordingTime: MutableLongState
){
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.3F)
            .height(100.dp)
        ,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val timeDisplay = timeStampRecorder_Tick(recordingTime.longValue)
        Text(
            text = timeDisplay,
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 60.sp,
                fontStyle = FontStyle.Normal
            ),
        )
    }
}

@Composable
fun StopAlertDialog(
    onDismissRequest: () -> Unit,
    onSaveRequest: (String)-> Unit
){
    var fileName = rememberSaveable {
        mutableStateOf("AudioFile")
    }
    AlertDialog(
        onDismissRequest = { onDismissRequest() },
        title = { Text(text = "Save Record") },
        text = {
            TextField(
                value = fileName.value,
                onValueChange = { fileName.value = it },
                label = { Text(text = "File Name") },
                maxLines = 1
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    fileName.value += ".mp3"
                    onSaveRequest(fileName.value)
                }
            ) {
                Text(text = "Save")
            }
        },
        dismissButton = {
            TextButton(
                onClick = { onDismissRequest() }
            ) {
                Text(text = "Cancel")
            }
        }
    )
}

//@Preview(showBackground = true)
//@Composable
//fun RecordScreenPreview() {
//    RecordScreen(navController = rememberNavController())
//}