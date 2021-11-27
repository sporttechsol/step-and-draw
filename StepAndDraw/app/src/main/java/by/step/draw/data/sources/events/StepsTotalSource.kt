package by.step.draw.data.sources.events

import by.step.draw.data.models.StepsTotalEvent
import io.reactivex.subjects.ReplaySubject

class StepsTotalSource {

    private val stepsReplaySubject = ReplaySubject.createWithSize<StepsTotalEvent>(1)

    fun subscribe() = stepsReplaySubject

    fun sendSteps(stepsTotalEvent: StepsTotalEvent) =
        stepsReplaySubject.onNext(stepsTotalEvent)
}