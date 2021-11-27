package by.step.draw.domain.usecases

import by.step.draw.data.models.step_counter_state.BaseStepCounterServiceState
import by.step.draw.data.repositories.StepCounterRepository

class SendStepCounterStatusUseCase(private val repository: StepCounterRepository) {

    fun sendState(baseStepCounterServiceState: BaseStepCounterServiceState) =
        repository.sendStepCounterServiceState(baseStepCounterServiceState)
}