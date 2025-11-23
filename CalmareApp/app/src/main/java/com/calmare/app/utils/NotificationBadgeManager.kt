package com.calmare.app.utils

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject
import java.util.Locale

data class MissedMeditation(
    val time: String,
    val timestamp: Long,
    val title: String
)

class NotificationBadgeManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("calmare_notifications", Context.MODE_PRIVATE)

    fun addMissedMeditation(hour: Int, minute: Int, title: String) {
        val missedList = getMissedMeditations().toMutableList()
        val timestamp = System.currentTimeMillis()

        missedList.add(MissedMeditation(
            time = String.format(Locale.getDefault(), "%02d:%02d", hour, minute),
            timestamp = timestamp,
            title = title
        ))

        saveMissedMeditations(missedList)
    }

    fun getMissedMeditations(): List<MissedMeditation> {
        val jsonString = prefs.getString("missed_meditations", "[]") ?: "[]"
        val jsonArray = JSONArray(jsonString)
        val list = mutableListOf<MissedMeditation>()

        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)
            list.add(MissedMeditation(
                time = obj.getString("time"),
                timestamp = obj.getLong("timestamp"),
                title = obj.getString("title")
            ))
        }

        return list
    }

    private fun saveMissedMeditations(list: List<MissedMeditation>) {
        val jsonArray = JSONArray()
        list.forEach { missed ->
            val obj = JSONObject()
            obj.put("time", missed.time)
            obj.put("timestamp", missed.timestamp)
            obj.put("title", missed.title)
            jsonArray.put(obj)
        }

        prefs.edit().putString("missed_meditations", jsonArray.toString()).apply()
    }

    fun clearAllMissedMeditations() {
        prefs.edit().putString("missed_meditations", "[]").apply()
    }

    fun getUnreadCount(): Int {
        return getMissedMeditations().size
    }
}
