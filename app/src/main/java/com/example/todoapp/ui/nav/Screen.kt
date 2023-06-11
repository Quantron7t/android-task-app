package com.example.todoapp.ui.nav

sealed class Screen(val route : String){
    object Home : Screen(route = "Home_Screen")
    object Detail : Screen(route = "Detail_Screen")
}
