package by.step.draw.data.sources.local

import android.content.Context

private const val KEY_TOTAL_STEPS = "KEY_TOTAL_STEPS"
private const val KEY_LAST_SHOWN_STEPS = "KEY_LAST_SHOWN_STEPS"
private const val KEY_HIDE_ELEMENTS_INTRO_SHOWN = "KEY_HIDE_ELEMENTS_INTRO_SHOWN"
private const val KEY_USER_ID = "KEY_USER_ID"

class PreferencesHelper(context: Context) : BasePreferencesHelper(context) {

    fun putTotalSteps(steps: Int) = putInt(KEY_TOTAL_STEPS, steps)

    fun getTotalSteps() = getInt(KEY_TOTAL_STEPS, 0)

    fun putLastShownSteps(steps: Int) = putInt(KEY_LAST_SHOWN_STEPS, steps)

    fun getLastShownSteps() = getInt(KEY_LAST_SHOWN_STEPS, 0)

    fun setHideElementsIntroShown(isShown: Boolean) =
        putBoolean(KEY_HIDE_ELEMENTS_INTRO_SHOWN, isShown)

    fun isHideElementsIntroShown() = getBoolean(KEY_HIDE_ELEMENTS_INTRO_SHOWN, false)

    fun setUserId(userId: String) = putString(KEY_USER_ID, userId)

    fun getUserId() = getString(KEY_USER_ID, "")
}