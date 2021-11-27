package by.step.draw.data.repositories

import android.hardware.SensorEvent
import by.step.draw.data.DataManagerLocal
import by.step.draw.data.models.StepsTotalEvent
import by.step.draw.data.models.step_counter_state.BaseStepCounterServiceState
import io.reactivex.Observable

class StepCounterRepository(val dataManagerLocal: DataManagerLocal) {

    // TODO be careful with this conversion
    fun startCountingSteps(): Observable<Int> {
        return dataManagerLocal.startCountingSteps()
            .map { event: SensorEvent ->
                event.values[0].toInt()
            }
    }

    fun saveTotalSteps(steps: Int) = dataManagerLocal.putTotalSteps(steps)

    fun getSavedTotalSteps() = dataManagerLocal.getTotalSteps()

    fun saveLastShownSteps(steps: Int) = dataManagerLocal.putLastShownSteps(steps)

    fun getLastShownSteps() = dataManagerLocal.getLastShownSteps()

    fun onCalculatedStepsChanged(steps: Int) =
        dataManagerLocal.onTotalStepsChanged(StepsTotalEvent(steps))

    fun subscribeOnTotalSteps() =
        dataManagerLocal.subscribeOnTotalSteps().map { it.steps }

    fun sendStepCounterServiceState(baseStepCounterServiceState: BaseStepCounterServiceState) =
        dataManagerLocal.sendStepCounterServiceState(baseStepCounterServiceState)

    fun subscribeOnStepCounterServiceState() = dataManagerLocal.subscribeOnStepCounterServiceState()
}