package com.dicoding.todoapp.ui.detail

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.dicoding.todoapp.R
import com.dicoding.todoapp.data.Task
import com.dicoding.todoapp.databinding.ActivityTaskDetailBinding
import com.dicoding.todoapp.ui.ViewModelFactory
import com.dicoding.todoapp.ui.list.TaskAdapter
import com.dicoding.todoapp.utils.DateConverter
import com.dicoding.todoapp.utils.TASK_ID

class DetailTaskActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTaskDetailBinding
    private lateinit var viewModel: DetailTaskViewModel
    private lateinit var task: Task

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_detail)
        binding = ActivityTaskDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //TODO 11 : Show detail task and implement delete action
        val viewModelFactory = ViewModelFactory.getInstance(this)
        viewModel = ViewModelProvider(
            this@DetailTaskActivity,
            viewModelFactory
        )[DetailTaskViewModel::class.java]

        if (intent.hasExtra(TASK_ID)) {
            val taskId: Int = intent.getIntExtra(TASK_ID, -1)
            val taskLiveData = viewModel.setTaskId(taskId)
            taskLiveData.observe(this) { task ->
                if (task != null) {
                    this.task = task
                    bind(task)

                    val deleteButton: Button = findViewById(R.id.btn_delete_task)
                    deleteButton.setOnClickListener {
                        deleteTask()
                    }
                } else {
                    finish()
                }
            }
        }
    }

    private fun bind(task: Task) {
        this.task = task
        binding.detailEdTitle.setText(task.title)
        binding.detailEdDueDate.setText(DateConverter.convertMillisToString(task.dueDateMillis))
        binding.detailEdDescription.setText(task.description)
    }

    private fun deleteTask() {
        viewModel.deleteTask(task)
        Toast.makeText(applicationContext, R.string.toast_taskDelete, Toast.LENGTH_SHORT).show()
        finish()
    }
}