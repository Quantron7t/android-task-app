package com.example.todoapp.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.ancillary.Checkbox
import com.example.todoapp.ancillary.Delta
import com.example.todoapp.cache.Task
import com.example.todoapp.core.TasksRepository
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.Base64
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val tasksRepository: TasksRepository
) : ViewModel() {

    private val _currentTask = MutableStateFlow<Task>(Task(taskTitle = "", taskBody = ""))
    val currentTask: StateFlow<Task> = _currentTask.asStateFlow()

    fun saveTask(task : Task){
        viewModelScope.launch(Dispatchers.IO) {
            if(task.taskCreatedOn == 0L) {
                val t = task.copy(taskCreatedOn = Instant.now().epochSecond)
                _currentTask.value = t
                tasksRepository.saveTask(t)
            }else {
                tasksRepository.saveTask(task)
            }
        }
    }

    fun setTaskTitle(taskTitle : String) {
        val t = currentTask.value.copy(taskTitle = taskTitle)
        _currentTask.value = t
    }

    fun setTaskBody(taskBody : String) {
        val t = currentTask.value.copy(taskBody = taskBody, taskProgress = calculateProgress(taskBody,currentTask.value.taskProgress))
        _currentTask.value = t
    }

    fun deleteTask(task : Task){
        viewModelScope.launch(Dispatchers.IO) {
            tasksRepository.deleteTask(task)
        }
    }

    fun markTaskAsCompleteOrIncomplete(task : Task, taskProgress : Float, checkboxType: Checkbox){
        viewModelScope.launch(Dispatchers.IO) {
            val t = task.copy(taskProgress = taskProgress,
                taskBody = updateTaskBodyCheckboxes(
                    task.taskBody,
                    checkboxType
                ))
            tasksRepository.saveTask(t)
        }
    }

    fun setCurrentTask(task: Task){
        _currentTask.value = task
    }

    private fun calculateProgress(taskBody : String, currentProgress : Float) : Float{
        val decodedString = String(Base64.getDecoder().decode(taskBody))
        val data = Gson().fromJson(decodedString, Delta::class.java)

        val allCheckboxes = data.ops
            .filter { x -> x.attributes?.list != null }
            .map { a -> a.attributes }

        if(allCheckboxes.isNotEmpty()) {
            val checkedItemsCount = allCheckboxes.filter { x -> x?.list == Checkbox.CHECKED.value }.size
            val uncheckedItemsCount = allCheckboxes.filter { x -> x?.list == Checkbox.UNCHECKED.value }.size
            val progress = ((checkedItemsCount.toFloat() / (checkedItemsCount + uncheckedItemsCount)) * 100f)/100
            return "%.2f".format(Locale.ENGLISH, progress).toFloat()
        }

        return currentProgress //send back user set or system default if check box not present
    }

    private fun updateTaskBodyCheckboxes(taskBody : String, checkboxType : Checkbox) : String{
        val decodedString = String(Base64.getDecoder().decode(taskBody))
        val data = Gson().fromJson(decodedString, Delta::class.java)

        data?.ops
            ?.filter { x -> x.attributes?.list != null }
            ?.map { a -> a.attributes }
            ?.forEach { a ->
                a?.list = checkboxType.value
            }

        return String(Base64.getEncoder().encode(Gson().toJson(data).toByteArray()))
    }
}