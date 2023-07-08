package com.dicoding.todoapp.ui.detail

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.dicoding.todoapp.R
import com.dicoding.todoapp.data.Task
import com.dicoding.todoapp.databinding.ActivityTaskDetailBinding
import com.dicoding.todoapp.ui.ViewModelFactory
import com.dicoding.todoapp.utils.DateConverter
import com.dicoding.todoapp.utils.TASK_ID

class DetailTaskActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTaskDetailBinding
    private lateinit var viewModel: DetailTaskViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //TODO 11 : Show detail task and implement delete action
        val viewModelFactory = ViewModelFactory.getInstance(this)
        viewModel = ViewModelProvider(
            this@DetailTaskActivity,
            viewModelFactory
        )[DetailTaskViewModel::class.java]

        if (intent.hasExtra(TASK_ID)) {
            val taskId : Int = intent.getIntExtra(TASK_ID, -1)
            val task = viewModel.setTaskId(taskId = taskId)
            task.observe(this) {tasks ->
                if (tasks != null) {
                    bind(task = tasks)
                } else {
                    finish()
                }
            }
        }
    }

    private fun bind(task: Task) {
        binding.detailEdTitle.setText(task.title)
        binding.detailEdDueDate.setText(DateConverter.convertMillisToString(task.dueDateMillis))
        binding.detailEdDescription.setText(task.description)

        binding.btnDeleteTask.setOnClickListener {
            viewModel.deleteTask()
            Toast.makeText(applicationContext, R.string.toast_taskDelete, Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}