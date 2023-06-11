package com.example.todoapp.ui.nav

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.todoapp.ui.composables.HomeScreen
import com.example.todoapp.ui.composables.TaskScreen
import com.example.todoapp.ui.viewmodels.ListViewModel
import com.example.todoapp.ui.viewmodels.TaskViewModel

@Composable
fun NavGraph(
    navHostController : NavHostController,
    listViewModel: ListViewModel,
    taskViewModel: TaskViewModel
){
    NavHost(
        navController = navHostController,
        startDestination = Screen.Home.route
    ){
        composable(
            route= Screen.Home.route
        ){
            HomeScreen(navHostController,listViewModel,taskViewModel)
        }

        composable(
            route= Screen.Detail.route
        ){
            TaskScreen(navHostController,taskViewModel)
        }
    }
}