package com.example.vext.ui.audio

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.vext.model.Audio
import com.example.vext.ui.theme.backgroundLight
import java.util.concurrent.TimeUnit
import kotlin.time.Duration

@Composable
fun SearchScreen(
    audioList: List<Audio>,
    navController: NavController,
    isAudioPlaying: Boolean,
    currentPlayingAudio: Audio,
    progress: Float,
    onProgress: (Float) -> Unit,
    onAudioClick: (Int) -> Unit,
    onStart:() -> Unit,
    modifier: Modifier = Modifier,
){
    Search(
//        navController = navController,
    )
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun Search(
//    navController: NavController,
){

    var isFiltered = remember {
        mutableStateOf(false)
    }
    var searchQuery by remember {
        mutableStateOf("")
    }
    var dateFilterString = remember {
        mutableStateOf("")
    }

    var dateFilter = 0L

    var showFilter = remember {
        mutableStateOf(false)
    }

    LaunchedEffect(key1 = searchQuery, key2 = dateFilter) {

        Log.e("SearchScreen", "Search Query: $searchQuery")
        Log.e("SearchScreen", "Date Filter: $dateFilter")
    }

    LaunchedEffect(key1 = dateFilterString.value) {
        dateFilter = when(dateFilterString.value) {
            "Yesterday" -> System.currentTimeMillis() - 24 * 60 * 60 * 1000
            "7 days ago" -> System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000
            "30 days ago" -> System.currentTimeMillis() - TimeUnit.DAYS.toMillis(30)
            else -> 0L
        }
    }

    Scaffold(
        topBar = {
            Row(

                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
            ){
                IconButton(
                    onClick = {
//                        navController.popBackStack()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBackIosNew,
                        contentDescription = "Back"
                    )
                }
                TextField(
                    value = searchQuery,
                    onValueChange = {
                        searchQuery = it
                    },
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color.White,
                        unfocusedPlaceholderColor = Color.Gray,
                        focusedPlaceholderColor = Color.Black,
                    ),
                    enabled = true,
                    placeholder = {
                        Text(
                            text = "Search",
                            color = Color.Gray
                        )
                    },
                    textStyle = TextStyle(
                        fontSize = 24.sp,
                    ),
                    modifier = Modifier
                        .fillMaxWidth(),
                )
            }
        }
    ) {
        Column(
            modifier = Modifier.padding(it)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Filter",
                    style = TextStyle(
                        fontWeight = FontWeight.Normal,
                        fontSize = 16.sp,
                        fontStyle = FontStyle.Normal
                    ),
                    modifier = Modifier.fillMaxWidth(0.9f)
                        .padding(start = 10.dp)
                )

                IconButton(
                    onClick = {
                        showFilter.value = !showFilter.value
                    },
                ) {
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowDown,
                        contentDescription = "Open"
                    )
                }
            }

            FilterCard(
                showFilter = showFilter,
                dateFilterString = dateFilterString,
            )


            LazyColumn() {

            }
        }
    }
}

@Composable
fun FilterCard(
    showFilter: MutableState<Boolean>,
    dateFilterString: MutableState<String>,
){
    val timeFilterList = listOf(
        "Yesterday",
        "7 days ago",
        "30 days ago"
    )

    AnimatedVisibility(
        visible = showFilter.value,
        enter = expandVertically(),
        exit = shrinkVertically()
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color.Gray
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(8.dp)
            ) {
                Text(
                    text = "Created At",
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        fontStyle = FontStyle.Normal,
                        color = Color.White,
                    ),
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row{
                    timeFilterList.forEachIndexed{index, item ->
                        FilterCardOptionItem(
                            content = item,
                            onClick = {
                                dateFilterString.value = it
                                Log.e("FilterCard", "Selected: $it")
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FilterCardOptionItem(
    content: String,
    onClick: (String) -> Unit
){
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color.LightGray
        ),
        modifier = Modifier
            .padding(4.dp)
            .clickable {
                onClick(content)
            }
    ) {
        Text(
            text = content,
            style = TextStyle(
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                fontStyle = FontStyle.Normal,
                color = Color.Black
            ),
            modifier = Modifier.padding(8.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSearchScreen(){
    Search()
}

@Preview(showBackground = true)
@Composable
fun PreviewFilterCard(){
    var showFilter = remember {
        mutableStateOf(true)
    }
    var dateFilterString = remember {
        mutableStateOf("")
    }
    FilterCard(showFilter, dateFilterString)
}