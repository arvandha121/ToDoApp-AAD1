package com.dicoding.todoapp.setting

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import androidx.work.Data
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.dicoding.todoapp.R
import com.dicoding.todoapp.notification.NotificationWorker
import com.dicoding.todoapp.utils.NOTIFICATION_CHANNEL_ENQUEUED
import com.dicoding.todoapp.utils.NOTIFICATION_CHANNEL_ID
import com.dicoding.todoapp.utils.NOTIFICATION_CHANNEL_REMIND
import java.util.concurrent.TimeUnit

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    class SettingsFragment : PreferenceFragmentCompat() {

        private lateinit var workManager: WorkManager
        private lateinit var periodicRequest: PeriodicWorkRequest

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)

            workManager = WorkManager.getInstance(requireContext())

            val prefNotification =
                findPreference<SwitchPreference>(getString(R.string.pref_key_notify))
            prefNotification?.setOnPreferenceChangeListener { _, notif ->

                //TODO 13 : Schedule and cancel daily reminder using WorkManager with data channelName

                if (notif == false) {
                    periodicTaskFalse()
                } else {
                    periodicTaskTrue()
                }
                true
            }
        }

        private fun periodicTaskFalse() {
            try {
                workManager.getWorkInfoByIdLiveData(periodicRequest.id)
                    .observe(viewLifecycleOwner) {
                        val statusNotif = it.state.name
                        Log.d(TAG, "Status Notification : $statusNotif")
                        if (it.state == WorkInfo.State.ENQUEUED) {
                            cancelPeriodicTask()
                            Toast.makeText(
                                requireContext(),
                                R.string.messageNotif,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            } catch (e: Exception) {
                Log.e(TAG, "${e.message}")
            }
        }

        private fun periodicTaskTrue() {
            val workManager = workManager
            val data = Data.Builder()
                .putString(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_REMIND)
                .putString(NOTIFICATION_CHANNEL_ID, getString(R.string.notify_content))
                .build()

            periodicRequest = PeriodicWorkRequest.Builder(
                NotificationWorker::class.java,
                1,
                TimeUnit.DAYS
            ).setInputData(data).build()

            workManager.enqueue(periodicRequest)
            workManager.getWorkInfoByIdLiveData(periodicRequest.id).observe(viewLifecycleOwner) {
                val status = it.state.name
                Log.d(TAG, "Status : $status")
                if (it.state == WorkInfo.State.ENQUEUED) {
                    Log.d(TAG, NOTIFICATION_CHANNEL_ENQUEUED)
                }
            }
        }

        private fun cancelPeriodicTask() {
            try {
                workManager.cancelAllWork()
            } catch (e: Exception) {
                Log.e(TAG, "Failed : ${e.message}")
            }
        }

        private fun updateTheme(mode: Int): Boolean {
            AppCompatDelegate.setDefaultNightMode(mode)
            requireActivity().recreate()
            return true
        }
    }
}