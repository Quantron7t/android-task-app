package com.example.todoapp.core

import android.content.Context
import androidx.room.Room
import com.example.todoapp.cache.TasksDB
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TasksModule {

    @Provides
    @Singleton
    fun provideTasksDatabase(@ApplicationContext applicationContext: Context) : TasksDB = Room.databaseBuilder(
        applicationContext,
        TasksDB::class.java, "database-tasks"
    ).build()

    @Provides
    @Singleton
    fun provideTasksRepository(
        database : TasksDB
    ) : TasksRepository {
        return TasksRepository(database)
    }
}