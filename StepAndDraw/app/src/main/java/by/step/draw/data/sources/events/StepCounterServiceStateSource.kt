package by.step.draw.data.sources.events

import by.step.draw.data.models.step_counter_state.BaseStepCounterServiceState
import by.step.draw.data.models.step_counter_state.StepCounterServiceStoppedState
import io.reactivex.subjects.ReplaySubject

class StepCounterServiceStateSource  {

    private val stepCounterServiceStateSubject = ReplaySubject.createWithSize<BaseStepCounterServiceState>(1)

    init {
        sendStatus(StepCounterServiceStoppedState())
    }

    fun subscribe() = stepCounterServiceStateSubject

    fun sendStatus(baseStepCounterServiceState: BaseStepCounterServiceState) = stepCounterServiceStateSubject.onNext(baseStepCounterServiceState)
}