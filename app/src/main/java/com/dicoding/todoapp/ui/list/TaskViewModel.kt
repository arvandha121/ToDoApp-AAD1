package com.dicoding.todoapp.ui.list

import androidx.lifecycle.*
import androidx.paging.PagedList
import com.dicoding.todoapp.R
import com.dicoding.todoapp.data.Task
import com.dicoding.todoapp.data.TaskRepository
import com.dicoding.todoapp.utils.Event
import com.dicoding.todoapp.utils.TasksFilterType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TaskViewModel(private val taskRepository: TaskRepository) : ViewModel() {

    private val _filter = MutableLiveData<TasksFilterType>()
    private var undoDelete: Task? = null

    val tasks: LiveData<PagedList<Task>> = _filter.switchMap {
        taskRepository.getTasks(it)
    }

    private val _snackbarText = MutableLiveData<Event<Int>>()
    val snackbarText: LiveData<Event<Int>> = _snackbarText

    init {
        _filter.value = TasksFilterType.ALL_TASKS
    }

    fun filter(filterType: TasksFilterType) {
        _filter.value = filterType
    }

    fun insertTask(task: Task): Long {
        var returnValue: Long = -1
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                returnValue = taskRepository.insertTask(task)
            }
        }
        return returnValue
    }

    fun completeTask(task: Task, completed: Boolean) = viewModelScope.launch {
        taskRepository.completeTask(task, completed)
        if (completed) {
            _snackbarText.value = Event(R.string.task_marked_complete)
        } else {
            _snackbarText.value = Event(R.string.task_marked_active)
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            undoDelete = task
            taskRepository.deleteTask(task)
        }
    }

    fun undoDelete() {
        val taskToRestore = undoDelete
        if (taskToRestore != null) {
            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    taskRepository.insertTask(taskToRestore)
                }
            }
        }
        undoDelete = null
    }
}