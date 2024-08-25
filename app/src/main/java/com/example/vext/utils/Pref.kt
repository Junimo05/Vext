package com.example.vext.utils

import android.content.Context
import android.content.SharedPreferences
import javax.inject.Inject

class Pref @Inject constructor(
    private val context: Context,
) {
    private val PREFS_NAME = "vext.prefs"
    private val sharedPref: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun save(key:String, value: Any){
        val editor = sharedPref.edit()

        when (value) {
            is String -> editor.putString(key, value)
            is Int -> editor.putInt(key, value)
            is Boolean -> editor.putBoolean(key, value)
            is Float -> editor.putFloat(key, value)
            is Long -> editor.putLong(key, value)
            else -> throw IllegalArgumentException("Unsupported type")
        }

        editor.apply()
    }

    fun getString(key: String, default: String = ""): String {
        return sharedPref.getString(key, default) ?: default
    }

    fun getInt(key: String, default: Int = 0): Int {
        return sharedPref.getInt(key, default)
    }

    fun getBoolean(key: String, default: Boolean = false): Boolean {
        return sharedPref.getBoolean(key, default)
    }

    fun getFloat(key: String, default: Float = 0f): Float {
        return sharedPref.getFloat(key, default)
    }

    fun getLong(key: String, default: Long = 0L): Long {
        return sharedPref.getLong(key, default)
    }

    fun remove(key: String) {
        val editor = sharedPref.edit()
        editor.remove(key)
        editor.apply()
    }

    fun clear() {
        val editor = sharedPref.edit()
        editor.clear()
        editor.apply()
    }
}