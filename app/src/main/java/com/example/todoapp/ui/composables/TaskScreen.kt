package com.example.todoapp.ui.composables

import CircularProgressBar
import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.graphics.toColorInt
import androidx.core.view.ViewCompat
import androidx.navigation.NavHostController
import com.example.todoapp.R
import com.example.todoapp.ancillary.DateFormatter
import com.example.todoapp.quill.QuillEditor
import com.example.todoapp.quill.QuillEditor.OnTextChangeListener
import com.example.todoapp.ui.viewmodels.TaskViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.Instant


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("MissingInflatedId", "InflateParams", "UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun TaskScreen(navHostController : NavHostController,taskViewModel: TaskViewModel) {
    val taskScreenTitleColor = "#3a3d3b".toColorInt()

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope: CoroutineScope = rememberCoroutineScope()
    val task by taskViewModel.currentTask.collectAsState()

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState){
        Snackbar(
            snackbarData = it,
            containerColor = Color.LightGray,
            contentColor = Color.DarkGray,
            actionColor = Color("#999999".toColorInt()),
            dismissActionContentColor = Color.DarkGray
        )
    } }) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .height(50.dp)
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(start = 8.dp, end = 8.dp),
                contentAlignment = Alignment.CenterStart
            ){
                Row(Modifier.fillMaxSize()) {
                    Text(
                        text = "Task details",
                        color = Color(taskScreenTitleColor),
                        modifier = Modifier
                            .align(Alignment.Bottom)
                            .weight(8.75f),
                        fontFamily = FontFamily(Font(R.font.poppins_regular)),
                        fontSize = 28.sp
                    )
                    Box(
                        Modifier
                            .fillMaxHeight()
                            .weight(1.25f),
                        contentAlignment = Alignment.Center
                    ){
                        CircularProgressBar(task.taskProgress,
                            number= 100,
                            fontSize = 10.sp,
                            color = Color("#2ECC71".toColorInt())
                        )
                    }
                    Icon(
                        Icons.Rounded.Done,
                        contentDescription = "",
                        tint = Color(taskScreenTitleColor),
                        modifier = Modifier
                            .height(35.dp)
                            .width(35.dp)
                            .align(Alignment.CenterVertically)
                            .padding(2.dp)
                            .weight(1f)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = rememberRipple(
                                    color = Color.Green
                                )
                            ) {
                                coroutineScope.launch {
                                    var message = "Task is saved"
                                    if (task.taskTitle.isNotEmpty()) {
                                        taskViewModel.saveTask(task)
                                    } else {
                                        message = "Not saved. Task title cannot be empty"
                                    }
                                    snackbarHostState.showSnackbar(
                                        message = message,
                                        withDismissAction = true,
                                        duration = SnackbarDuration.Short
                                    )
                                }
                            }
                    )

                }
            }
            Box(
                modifier = Modifier
                    .height(55.dp)
                    .background(Color.White)
                    .fillMaxWidth(),
                contentAlignment = Alignment.CenterStart
            ){
                Box(Modifier.fillMaxSize()){
                    TextField(
                        value = task.taskTitle,
                        onValueChange = { taskViewModel.setTaskTitle(it) },
                        modifier = Modifier
                            .fillMaxSize(),
                        placeholder = {
                            Text(
                                "Task title",
                                textAlign = TextAlign.Center,
                                fontFamily = FontFamily(Font(R.font.poppins_thin)),
                                color = Color.Black
                            )},
                        singleLine = true,
                        maxLines = 1,
                        textStyle = TextStyle(
                            color = Color.Gray,
                            fontSize = 18.sp,
                            fontFamily = FontFamily(Font(R.font.poppins_medium)
                            )),
                        colors = TextFieldDefaults.textFieldColors(
                            containerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            placeholderColor = Color.LightGray
                        )
                    )
                }
            }
            Box(
                Modifier
                    .height(15.dp)
                    .fillMaxWidth()
            ){
                val createdOn = if(task.taskCreatedOn==0L) Instant.now().epochSecond else task.taskCreatedOn
                Text(
                    text = DateFormatter.getLocalFormattedTime(createdOn),
                    color = Color.LightGray,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    fontFamily = FontFamily(Font(R.font.poppins_regular)),
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 15.dp)
                )
            }
            Box(modifier = Modifier
                .fillMaxWidth(),
                contentAlignment = Alignment.CenterStart
            ){
                AndroidView(
                    factory = { context ->
                        val view = LayoutInflater.from(context).inflate(R.layout.task_editor, null, false)


                        val quillEditor = view.findViewById<QuillEditor>(R.id.editor)

                        quillEditor.setOnTextChangeListener(OnTextChangeListener { html ->
                            Log.d("FULL_HTML",html)
                            taskViewModel.setTaskBody(html)
                        })

                        quillEditor.html = task.taskBody

                        view // return the view
                    },
                    modifier = Modifier.fillMaxHeight()
                )
            }
        }
    }

}