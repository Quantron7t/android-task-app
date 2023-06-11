package com.example.todoapp.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.cache.Task
import com.example.todoapp.core.TasksRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListViewModel @Inject constructor(
    private val tasksRepository: TasksRepository
) : ViewModel() {

    init {
        //saveTask(Task(taskTitle = "TITLE 1", taskBody = "sim"))
        refreshTaskDetails()
    }

    private val _tasks = MutableStateFlow<List<Task>>(listOf())
    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()

    private fun refreshTaskDetails() {
        viewModelScope.launch(Dispatchers.IO) {
            tasksRepository.tasks.collect { x->
                Log.d("TAG",x.toString())
                _tasks.value = x
            }
        }
    }

    private fun clearTable(){
        viewModelScope.launch(Dispatchers.IO) {
            tasksRepository.clearTable()
        }
    }

}