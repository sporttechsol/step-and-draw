package by.step.draw.data.sources.local

import android.content.Context

private const val KEY_TOTAL_STEPS = "KEY_TOTAL_STEPS"
private const val KEY_LAST_SHOWN_STEPS = "KEY_LAST_SHOWN_STEPS"
private const val KEY_INTRO = "KEY_INTRO"
private const val KEY_AFTER_INTRO_ACTION="KEY_AFTER_INTRO_ACTION"
private const val KEY_ANIMATION_INTRO_DIALOG_SHOWN = "KEY_ANIMATION_INTRO_DIALOG_SHOWN"
private const val KEY_FINAL_DIALOG_SHOWN = "KEY_FINAL_DIALOG_SHOWN"
private const val KEY_HIDE_ELEMENTS_INTRO_SHOWN = "KEY_HIDE_ELEMENTS_INTRO_SHOWN"
private const val KEY_USER_ID = "KEY_USER_ID"

class PreferencesHelper(context: Context) : BasePreferencesHelper(context) {

    fun putTotalSteps(steps: Int) = putInt(KEY_TOTAL_STEPS, steps)

    fun getTotalSteps() = getInt(KEY_TOTAL_STEPS, 0)

    fun putLastShownSteps(steps: Int) = putInt(KEY_LAST_SHOWN_STEPS, steps)

    fun getLastShownSteps() = getInt(KEY_LAST_SHOWN_STEPS, 0)

    fun setIntroShown(isShown: Boolean) = putBoolean(KEY_INTRO, isShown)

    fun isIntroShown() = getBoolean(KEY_INTRO, false)

    fun setAfterIntroAction(isShown:Boolean) = putBoolean(KEY_AFTER_INTRO_ACTION,isShown)

    fun isAfterIntroAction() = getBoolean(KEY_AFTER_INTRO_ACTION,false)

    fun setAnimationIntroDialogShown(isShown: Boolean) =
        putBoolean(KEY_ANIMATION_INTRO_DIALOG_SHOWN, isShown)

    fun isAnimationIntroDialogShown() = getBoolean(KEY_ANIMATION_INTRO_DIALOG_SHOWN, false)

    fun setFinalDialogShown(isShown: Boolean) = putBoolean(KEY_FINAL_DIALOG_SHOWN, isShown)

    fun isFinalDialogShown() = getBoolean(KEY_FINAL_DIALOG_SHOWN, false)

    fun setHideElementsIntroShown(isShown: Boolean) =
        putBoolean(KEY_HIDE_ELEMENTS_INTRO_SHOWN, isShown)

    fun isHideElementsIntroShown() = getBoolean(KEY_HIDE_ELEMENTS_INTRO_SHOWN, false)

    fun setUserId(userId: String) = putString(KEY_USER_ID, userId)

    fun getUserId() = getString(KEY_USER_ID, "")
}