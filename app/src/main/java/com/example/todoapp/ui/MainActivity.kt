package com.example.todoapp.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.todoapp.ui.composables.HomeScreen
import com.example.todoapp.ui.nav.NavGraph
import com.example.todoapp.ui.theme.TodoAppTheme
import com.example.todoapp.ui.viewmodels.ListViewModel
import com.example.todoapp.ui.viewmodels.TaskViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    lateinit var navHostController : NavHostController
    private val listViewModel : ListViewModel by viewModels()
    private val taskViewModel : TaskViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val isDarkTheme = false //change for themes
        setContent {

            val systemUiController = rememberSystemUiController()
            if(isDarkTheme){
                systemUiController.setSystemBarsColor(color = Color.Transparent)
            }else{
                systemUiController.setStatusBarColor(color = Color.White)
                systemUiController.setSystemBarsColor(color = Color.White)
            }

            /*window.statusBarColor = Color.White.toArgb()
            window.navigationBarColor = Color.White.toArgb()
            val windowInsetController = WindowCompat.getInsetsController(window, window.decorView)
            windowInsetController.isAppearanceLightStatusBars = !isDarkTheme
            windowInsetController.isAppearanceLightNavigationBars = !isDarkTheme
*/
            // A surface container using the 'background' color from the theme
            Surface(modifier = Modifier.fillMaxSize(), color = Color.White) {

                navHostController = rememberNavController()
                NavGraph(navHostController = navHostController, listViewModel = listViewModel, taskViewModel = taskViewModel)
                //HomeScreen()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TodoAppTheme {
        HomeScreen(rememberNavController(),viewModel(),viewModel())
    }
}