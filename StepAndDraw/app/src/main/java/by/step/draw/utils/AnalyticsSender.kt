package by.step.draw.utils

import android.os.Build
import android.os.Bundle
import by.step.draw.BuildConfig
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase

class AnalyticsSender(private val userId: String) {

    private val firebaseAnalytics: FirebaseAnalytics

    fun introFinished() {
        firebaseAnalytics.logEvent(EVENT_INTRO_FINISHED, null)
    }

    fun stepServiceStarted() {
        firebaseAnalytics.logEvent(EVENT_SERVICE_STARTED, null)
    }

    fun stepServiceRestarted() {
        firebaseAnalytics.logEvent(EVENT_SERVICE_RESTARTED, null)
    }// click home button while on application // click terminate in logcat

    fun serviceStopped() {
        firebaseAnalytics.logEvent(EVENT_SERVICE_STOPPED, null)
    }

    fun takeReward(steps: Int) {
        val bundle = Bundle()
        bundle.putString(PARAM_STEPS, steps.toString())
        firebaseAnalytics.logEvent(EVENT_TAKE_REWARD, bundle)
    }

    fun rewardIsFinished() {
        firebaseAnalytics.logEvent(EVENT_REWARD_IS_FINISHED, null)
    }

    fun drawingStarted(interval: Pair<Int, Int>) {
        val bundle = Bundle()
        bundle.putString(PARAM_STEPS, (interval.second - interval.first).toString())
        bundle.putString(PARAM_FROM_STEPS, interval.first.toString())
        bundle.putString(PARAM_TO_STEPS, interval.second.toString())
        bundle.putString(PARAM_USER_ID, userId)
        firebaseAnalytics.logEvent(EVENT_DRAWING_STARTED, bundle)
    }

    fun drawingIsFinished(interval: Pair<Int, Int>) {
        val bundle = Bundle()
        bundle.putString(PARAM_STEPS, (interval.second - interval.first).toString())
        bundle.putString(PARAM_FROM_STEPS, interval.first.toString())
        bundle.putString(PARAM_TO_STEPS, interval.second.toString())
        bundle.putString(PARAM_USER_ID, userId)
        firebaseAnalytics.logEvent(EVENT_DRAWING_IS_FINISHED, bundle)
    }

    fun hideUIClicked() {
        firebaseAnalytics.logEvent(EVENT_HIDE_UI_CLICKED, null)
    }

    fun permissionBlocked() {
        firebaseAnalytics.logEvent(EVENT_PERMISSION_BLOCKED, null)
    }

    fun permissionGranted() {
        firebaseAnalytics.logEvent(EVENT_PERMISSION_GRANTED, null)
    }

    fun noStepDetector() {
        val bundle = Bundle()
        bundle.putString(PARAM_DEVICE_INFO, getDeviceInformation())
        firebaseAnalytics.logEvent(EVENT_NO_STEP_DETECTOR, bundle)
    }

    fun serviceIsRunning(livingTime: Long) {
        val bundle = Bundle()
        bundle.putString(PARAM_LIVING_TIME, convertTime(livingTime))
        firebaseAnalytics.logEvent(EVENT_SERVICE_IS_RUNNING, bundle)
    }

    private fun getDeviceInformation() =
        "${Build.MANUFACTURER} ${Build.MODEL}" + "\n" +
                "ANDROID ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})"

    private fun convertTime(millis: Long): String {
        val seconds: Long = millis / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24
        return days.toString() + ":" + hours % 24 + ":" + minutes % 60 + ":" + seconds % 60
    }

    init {
        firebaseAnalytics = Firebase.analytics
        if (BuildConfig.FLAVOR.equals("prod")) {
            firebaseAnalytics.setUserId(userId)
            firebaseAnalytics.setAnalyticsCollectionEnabled(true)
        } else {
            firebaseAnalytics.setAnalyticsCollectionEnabled(false)
        }
    }

    companion object {
        private var instance: AnalyticsSender? = null
        fun getInstance(userId: String): AnalyticsSender =
            instance ?: AnalyticsSender(userId).also { instance = it }

        fun getInstance() = instance!!

        // events
        private const val EVENT_INTRO_FINISHED = "intro_finished"
        private const val EVENT_SERVICE_STARTED = "step_service_started"
        private const val EVENT_SERVICE_RESTARTED = "step_service_restarted"
        private const val EVENT_SERVICE_STOPPED = "step_service_stopped"
        private const val EVENT_TAKE_REWARD = "take_reward"
        private const val EVENT_DRAWING_IS_FINISHED = "drawing_is_finished"
        private const val EVENT_HIDE_UI_CLICKED = "hide_ui_clicked"
        private const val EVENT_PERMISSION_BLOCKED = "permission_blocked"
        private const val EVENT_PERMISSION_GRANTED = "permission_granted"
        private const val EVENT_NO_STEP_DETECTOR = "no_step_detector"
        private const val EVENT_REWARD_IS_FINISHED = "reward_is_finished"
        private const val EVENT_DRAWING_STARTED = "drawing_started"
        private const val EVENT_SERVICE_IS_RUNNING = "service_is_running"

        // params
        private const val PARAM_STEPS = "steps"
        private const val PARAM_FROM_STEPS = "from_steps"
        private const val PARAM_TO_STEPS = "to_steps"
        private const val PARAM_DEVICE_INFO = "device_info"
        private const val PARAM_LIVING_TIME = "living_time"
        private const val PARAM_USER_ID = "user_id"
    }
}