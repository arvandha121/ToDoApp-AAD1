package com.dicoding.todoapp.ui.list

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers
import com.dicoding.todoapp.R
import com.dicoding.todoapp.ui.add.AddTaskActivity
import org.junit.Before
import org.junit.Test

//TODO 16 : Write UI test to validate when user tap Add Task (+), the AddTaskActivity displayed
class TaskActivityTest {
    @Before
    fun setUp() {
        ActivityScenario.launch(TaskActivity::class.java)
    }

    @Test
    fun addTaskTest() {
        // Open AddTaskActivity by clicking the FloatingActionButton
        Intents.init()
        Espresso.onView(ViewMatchers.withId(R.id.fab)).perform(ViewActions.click())
        Intents.intended(IntentMatchers.hasComponent(AddTaskActivity::class.java.name))

        // Enter title, description
        val title = "Task Title"
        val description = "Task Description"

        Espresso.onView(ViewMatchers.withId(R.id.add_ed_title))
            .perform(ViewActions.typeText(title), ViewActions.closeSoftKeyboard())
        Espresso.onView(ViewMatchers.withId(R.id.add_ed_description))
            .perform(ViewActions.typeText(description), ViewActions.closeSoftKeyboard())

        // Save the task
        Espresso.onView(ViewMatchers.withId(R.id.action_save)).perform(ViewActions.click())

        Intents.release()
    }
}