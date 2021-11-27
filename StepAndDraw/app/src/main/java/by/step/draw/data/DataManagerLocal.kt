package by.step.draw.data

import android.content.Context
import by.step.draw.data.models.StepsTotalEvent
import by.step.draw.data.models.step_counter_state.BaseStepCounterServiceState
import by.step.draw.data.sources.events.StepCounterServiceStateSource
import by.step.draw.data.sources.events.StepsTotalSource
import by.step.draw.data.sources.local.PreferencesHelper
import by.step.draw.device.StepDataSource

class DataManagerLocal(private val context: Context) {

    private val preferencesHelper: PreferencesHelper by lazy {
        PreferencesHelper(context)
    }

    private val stepDataSource: StepDataSource by lazy {
        StepDataSource(context)
    }

    private val stepsTotalSource: StepsTotalSource by lazy {
        StepsTotalSource()
    }

    private val stepCounterServiceStateSource: StepCounterServiceStateSource by lazy {
        StepCounterServiceStateSource()
    }

    fun startCountingSteps() = stepDataSource.startCountingSteps()

    fun onTotalStepsChanged(stepsTotalEvent: StepsTotalEvent) {
        stepsTotalSource.sendSteps(stepsTotalEvent)
    }

    fun subscribeOnTotalSteps() = stepsTotalSource.subscribe()

    fun subscribeOnStepCounterServiceState() = stepCounterServiceStateSource.subscribe()

    fun sendStepCounterServiceState(baseStepCounterServiceState: BaseStepCounterServiceState) =
        stepCounterServiceStateSource.sendStatus(baseStepCounterServiceState)

    // preferences
    fun putTotalSteps(steps: Int) = preferencesHelper.putTotalSteps(steps)

    fun getTotalSteps() = preferencesHelper.getTotalSteps()

    fun putLastShownSteps(steps: Int) = preferencesHelper.putLastShownSteps(steps)

    fun getLastShownSteps() = preferencesHelper.getLastShownSteps()

    fun setHideElementsIntroShown(isShown: Boolean) =
        preferencesHelper.setHideElementsIntroShown(isShown)

    fun isHideElementsIntroShown() = preferencesHelper.isHideElementsIntroShown()

    fun setUserId(userId: String) = preferencesHelper.setUserId(userId)

    fun getUserId() = preferencesHelper.getUserId()
}