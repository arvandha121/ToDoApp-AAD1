package com.dicoding.todoapp.ui.list

import android.view.View
import android.widget.DatePicker
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.dicoding.todoapp.R
import com.dicoding.todoapp.ui.add.AddTaskActivity
import org.hamcrest.Matcher
import org.junit.Before
import org.junit.Test

//TODO 16 : Write UI test to validate when user tap Add Task (+), the AddTaskActivity displayed
class TaskActivityTest {
    private fun setDate(year: Int, month: Int, dayOfMonth: Int): ViewAction {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> {
                return isAssignableFrom(DatePicker::class.java)
            }

            override fun getDescription(): String {
                return "Set date on DatePicker"
            }

            override fun perform(uiController: UiController, view: View) {
                val datePicker = view as DatePicker
                datePicker.updateDate(year, month, dayOfMonth)
            }
        }
    }

    @Before
    fun setUp() {
        ActivityScenario.launch(TaskActivity::class.java)
    }

    @Test
    fun addTaskTest() {
        // Open AddTaskActivity by clicking the FloatingActionButton
        Intents.init()
        onView(withId(R.id.fab)).perform(click())
        Intents.intended(IntentMatchers.hasComponent(AddTaskActivity::class.java.name))

        // Enter title, description
        val title = "Task Title"
        val description = "Task Description"

        onView(withId(R.id.add_ed_title))
            .perform(ViewActions.typeText(title), ViewActions.closeSoftKeyboard())
        onView(withId(R.id.add_ed_description))
            .perform(ViewActions.typeText(description), ViewActions.closeSoftKeyboard())

        // Save the task
        onView(withId(R.id.img_button)).perform(ViewActions.click())
        onView(isAssignableFrom(DatePicker::class.java)).perform(setDate(2023, 11, 20))
        onView(ViewMatchers.withText("OK")).perform(click())

        onView(withId(R.id.action_save)).perform(click())
        Intents.release()
    }
}