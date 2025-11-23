package com.calmare.app.utils

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject

data class ReminderTime(
    val hour: Int,
    val minute: Int
)

class ReminderStorage(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("calmare_reminders", Context.MODE_PRIVATE)

    fun addReminder(hour: Int, minute: Int) {
        val remindersList = getReminders().toMutableList()

        // Evita duplicatas
        if (remindersList.any { it.hour == hour && it.minute == minute }) {
            return
        }

        remindersList.add(ReminderTime(hour, minute))
        saveReminders(remindersList)
    }

    fun getReminders(): List<ReminderTime> {
        val jsonString = prefs.getString("reminder_times", "[]") ?: "[]"
        val jsonArray = JSONArray(jsonString)
        val list = mutableListOf<ReminderTime>()

        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)
            list.add(ReminderTime(
                hour = obj.getInt("hour"),
                minute = obj.getInt("minute")
            ))
        }

        // Ordena por horário
        return list.sortedWith(compareBy({ it.hour }, { it.minute }))
    }

    private fun saveReminders(list: List<ReminderTime>) {
        val jsonArray = JSONArray()
        list.forEach { reminder ->
            val obj = JSONObject()
            obj.put("hour", reminder.hour)
            obj.put("minute", reminder.minute)
            jsonArray.put(obj)
        }

        prefs.edit().putString("reminder_times", jsonArray.toString()).apply()
    }

    fun removeReminder(hour: Int, minute: Int) {
        val remindersList = getReminders().toMutableList()
        remindersList.removeAll { it.hour == hour && it.minute == minute }
        saveReminders(remindersList)
    }

    fun clearAllReminders() {
        prefs.edit().putString("reminder_times", "[]").apply()
    }

    fun hasReminders(): Boolean {
        return getReminders().isNotEmpty()
    }
}
