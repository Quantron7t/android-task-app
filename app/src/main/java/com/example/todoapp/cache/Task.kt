package com.example.todoapp.cache

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Tasks")
data class Task(
    @PrimaryKey(autoGenerate = true) val taskId: Int? = null,
    @ColumnInfo(name = "task_title") val taskTitle: String,
    @ColumnInfo(name = "task_body") val taskBody: String,
    @ColumnInfo(name = "task_progress") val taskProgress: Float = 0f,
    @ColumnInfo(name = "task_created_on") val taskCreatedOn: Long = 0L
)