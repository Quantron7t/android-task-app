package com.example.todoapp.cache

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Task::class], version = 1)
abstract class TasksDB : RoomDatabase() {
    abstract fun taskDAO(): TaskDAO
}