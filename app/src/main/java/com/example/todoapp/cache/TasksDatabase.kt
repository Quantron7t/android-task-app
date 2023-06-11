package com.example.todoapp.cache

import androidx.lifecycle.MutableLiveData

class TasksDatabase {
    fun getAllTasks(): MutableLiveData<List<Task>> {
        val mutableLiveData = MutableLiveData<List<Task>>()

        val data = mutableListOf(
            Task(taskTitle = "zyyy", taskBody = ""),
            Task(taskTitle = "zyyy1", taskBody = ""),
            Task(taskTitle = "zyyy2", taskBody = "")
        )
        mutableLiveData.value = data

        return mutableLiveData
    }

    fun getTaskAsHtmlString(): MutableLiveData<String> {
        val mutableLiveData = MutableLiveData<String>()

        val taskHtml = "<ul data-checked=\"false\"><li>Cbq</li></ul><ul data-checked=\"true\"><li>Cbq</li><li>Cbt</li><li>Cbu</li></ul>"
        mutableLiveData.value = taskHtml

        return mutableLiveData
    }
}