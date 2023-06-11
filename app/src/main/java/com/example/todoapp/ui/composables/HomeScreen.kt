package com.example.todoapp.ui.composables

import CircularProgressBar
import android.annotation.SuppressLint
import android.view.LayoutInflater
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.graphics.toColorInt
import androidx.navigation.NavHostController
import com.example.todoapp.R
import com.example.todoapp.ancillary.Checkbox
import com.example.todoapp.ancillary.DateFormatter.getLocalFormattedTime
import com.example.todoapp.cache.Task
import com.example.todoapp.quill.QuillViewer
import com.example.todoapp.ui.nav.Screen
import com.example.todoapp.ui.viewmodels.ListViewModel
import com.example.todoapp.ui.viewmodels.TaskViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import me.saket.swipe.SwipeAction
import me.saket.swipe.SwipeableActionsBox


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "ClickableViewAccessibility",
    "SimpleDateFormat"
)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    navHostController: NavHostController,
    listViewModel: ListViewModel,
    taskViewModel: TaskViewModel
) {

    val listTaskTitleColor = "#636363".toColorInt()
    val homeScreenTitleColor = "#3a3d3b".toColorInt()

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope: CoroutineScope = rememberCoroutineScope()
    val data by listViewModel.tasks.collectAsState(initial = emptyList())
    val lazyListState = rememberLazyListState()

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState){
        Snackbar(
            snackbarData = it,
            containerColor = Color.LightGray,
            contentColor = Color.DarkGray,
            actionColor = Color("#999999".toColorInt()),
            dismissActionContentColor = Color.DarkGray
        )
    } }) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(start = 5.dp, end = 5.dp)
        ){
            Box(
                modifier = Modifier
                    .weight(0.8f)
                    .fillMaxWidth()
                    .padding(start = 5.dp, end = 5.dp),
                contentAlignment = Alignment.CenterStart
            ){
                Column(Modifier.fillMaxSize()){
                    Spacer(modifier = Modifier.height(20.dp))
                    Row(Modifier.fillMaxSize()){
                        Text(
                            text = "Tasks",
                            modifier = Modifier.weight(9f),
                            color = Color(homeScreenTitleColor),
                            fontFamily = FontFamily(Font(R.font.poppins_regular)),
                            fontSize = 32.sp
                        )
                        Icon(
                            painter = painterResource(id = R.drawable.add_task),
                            contentDescription = null,
                            tint = Color(homeScreenTitleColor),
                            modifier = Modifier
                                .weight(1f)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = rememberRipple(
                                        color = Color.Green
                                    ),
                                    onClick = {
                                        taskViewModel.setCurrentTask(
                                            Task(
                                                taskTitle = "",
                                                taskBody = ""
                                            )
                                        )
                                        navHostController.navigate(Screen.Detail.route)
                                    }
                                )
                        )
                    }
                }
            }
            Box(
                modifier = Modifier
                    .weight(9.2f)
                    .fillMaxWidth()
            ){
                Column(Modifier.fillMaxSize()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    LazyColumn(modifier = Modifier.fillMaxSize(),state = lazyListState){

                        items(data){item ->

                            val delete = SwipeAction(
                                background = Color("#E74C3C".toColorInt()),
                                onSwipe = {
                                    coroutineScope.launch {
                                        taskViewModel.deleteTask(task = item)
                                        val snackbarResult = snackbarHostState.showSnackbar(
                                            message = "Task is deleted",
                                            withDismissAction = true,
                                            actionLabel = "Undo",
                                            duration = SnackbarDuration.Long
                                        )
                                        when (snackbarResult) {
                                            SnackbarResult.ActionPerformed -> taskViewModel.saveTask(task = item)
                                            else -> return@launch
                                        }
                                    }
                                },
                                icon = rememberVectorPainter(Icons.Rounded.Delete)
                            )

                            val markAsCompleteOrIncomplete = SwipeAction(
                                icon = rememberVectorPainter(
                                    if(item.taskProgress < 1.0f) Icons.Rounded.Check
                                    else Icons.Rounded.Close
                                ),
                                background = Color(
                                    if(item.taskProgress < 1.0f) "#2ECC71".toColorInt()
                                    else "#FAED27".toColorInt()
                                ),
                                onSwipe = {
                                    coroutineScope.launch {
                                        taskViewModel.markTaskAsCompleteOrIncomplete(
                                            task = item,
                                            taskProgress = if(item.taskProgress < 1.0f) 1.0f else 0f,
                                            checkboxType = if(item.taskProgress < 1.0f) Checkbox.CHECKED else Checkbox.UNCHECKED
                                        )
                                        val snackbarResult = snackbarHostState.showSnackbar(
                                            message = if(item.taskProgress < 1.0f) "Task is marked as complete"
                                                    else "Task is marked as incomplete",
                                            withDismissAction = true,
                                            actionLabel = "Undo",
                                            duration = SnackbarDuration.Long
                                        )
                                        when (snackbarResult) {
                                            SnackbarResult.ActionPerformed -> taskViewModel.saveTask(task = item)
                                            else -> return@launch
                                        }
                                    }
                                },
                            )

                            SwipeableActionsBox(
                                startActions = listOf(delete),
                                endActions = listOf(markAsCompleteOrIncomplete),
                                swipeThreshold = 150.dp,
                                backgroundUntilSwipeThreshold = Color.DarkGray,
                                modifier = Modifier.animateItemPlacement()
                            ) {
                                // Swipe-able content goes here.
                                Column(
                                    Modifier
                                        .height(250.dp)
                                        .fillMaxWidth()
                                ){
                                    //title
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(45.dp)
                                            .padding(start = 10.dp),
                                        contentAlignment = Alignment.CenterStart
                                    ){
                                        Text(
                                            text = "# ${item.taskTitle}",
                                            color = Color(listTaskTitleColor),
                                            fontSize = 18.sp,
                                            fontFamily = FontFamily(Font(R.font.poppins_semi_bold)),
                                        )
                                    }


                                    //body
                                    Box(modifier = Modifier
                                        .fillMaxWidth()
                                        .height(180.dp)
                                        .padding(bottom = 10.dp, start = 10.dp, end = 10.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .border(
                                            width = 1.dp,
                                            color = Color.LightGray,
                                            RoundedCornerShape(10.dp)
                                        ),
                                        contentAlignment = Alignment.CenterStart
                                    ){
                                        AndroidView(
                                            factory = { context ->
                                                val view = LayoutInflater.from(context).inflate(R.layout.note_viewer, null, false)
                                                //addressing the recomposition problem for web views
                                                //https://stackoverflow.com/a/67916673/7873768
                                                view.findViewById<QuillViewer>(R.id.viewer).apply {
                                                    this.html = item.taskBody
                                                }
                                                view
                                            },
                                            update = {
                                                //addressing the recomposition problem for web views
                                                //https://stackoverflow.com/a/67916673/7873768
                                                it.findViewById<QuillViewer>(R.id.viewer)?.let { quillViewer ->
                                                    quillViewer.html = item.taskBody
                                                }
                                            },
                                            modifier = Modifier.fillMaxHeight()
                                        )
                                        //below box is for mocking a click on web-view
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .clickable(
                                                    indication = null,
                                                    interactionSource = remember { MutableInteractionSource() },
                                                    onClick = {
                                                        taskViewModel.setCurrentTask(task = item)
                                                        navHostController.navigate(Screen.Detail.route)
                                                    }),
                                        )
                                    }



                                    //footer
                                    Row(Modifier.height(20.dp)) {
                                        Spacer(Modifier.weight(0.25f))
                                        Box(Modifier.weight(8.50f)){
                                            Text(
                                                text = getLocalFormattedTime(item.taskCreatedOn),
                                                color = Color.LightGray,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                textAlign = TextAlign.Center,
                                                fontFamily = FontFamily(Font(R.font.poppins_regular))
                                            )
                                        }
                                        Box(
                                            Modifier
                                                .fillMaxHeight()
                                                .weight(1.20f), contentAlignment = Alignment.Center){
                                            CircularProgressBar(item.taskProgress,
                                                number= 100,
                                                fontSize = 10.sp,
                                                color = Color("#2ECC71".toColorInt()))
                                        }
                                        Spacer(Modifier.weight(0.05f))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}