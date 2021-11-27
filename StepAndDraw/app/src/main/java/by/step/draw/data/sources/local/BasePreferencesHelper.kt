package by.step.draw.data.sources.local

import android.content.Context
import android.content.SharedPreferences
import java.util.*

private const val PREF_FILE_NAME: String = "step_and_draw_prefs"

open class BasePreferencesHelper(context: Context) {

    private val pref: SharedPreferences

    init {
        pref = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE)
    }

    protected fun putBoolean(key: String, value: Boolean) =
        pref.edit().putBoolean(key, value).apply()

    protected fun getBoolean(key: String, defaultValue: Boolean) =
        pref.getBoolean(key, defaultValue)

    protected fun putInt(key: String, value: Int) = pref.edit().putInt(key, value).apply()

    protected fun getInt(key: String, defaultValue: Int) = pref.getInt(key, defaultValue)

    protected fun putDouble(key: String, value: Double) =
        pref.edit().putLong(key, value.toRawBits()).apply()

    protected fun getDouble(key: String, defaultValue: Double) =
        Double.fromBits(pref.getLong(key, defaultValue.toBits()))

    protected fun putLong(key: String, value: Long) = pref.edit().putLong(key, value).apply()

    protected fun getLong(key: String, defaultValue: Long) = pref.getLong(key, defaultValue)

    protected fun putString(key: String, value: String) = pref.edit().putString(key, value).apply()

    protected fun getString(key: String, defaultValue: String) = pref.getString(key, defaultValue)

    protected fun putFloat(key: String, value: Float) = pref.edit().putFloat(key, value).apply()

    protected fun getFloat(key: String, defaultValue: Float) = pref.getFloat(key, defaultValue)

    protected fun putDate(key: String, date: Date) = putLong(key, date.time)

    protected fun getDate(key: String): Date? {
        val timestamp = getLong(key, -1L)
        return if (timestamp == -1L) {
            null
        } else {
            Date(timestamp)
        }
    }
}