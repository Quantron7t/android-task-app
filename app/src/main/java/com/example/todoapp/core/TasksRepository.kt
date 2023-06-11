package com.example.todoapp.core

import com.example.todoapp.cache.Task
import com.example.todoapp.cache.TasksDB
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TasksRepository @Inject constructor(
    private val database: TasksDB
) {
    val tasks: Flow<List<Task>> =
        database.taskDAO().getAllTasks()

    /*val tasks: Flow<List<Task>> =
        TasksDatabase().getAllTasks().asFlow()*/


    /*val getTaskAsHtmlString: Flow<String> =
        database.getTaskAsHtmlString().asFlow()*/

    fun saveTask(task : Task) {
        database.taskDAO().saveTask(task)
    }

    fun clearTable(){
        database.taskDAO().nukeTable()
    }

    fun deleteTask(task: Task){
        database.taskDAO().delete(task)
    }
}