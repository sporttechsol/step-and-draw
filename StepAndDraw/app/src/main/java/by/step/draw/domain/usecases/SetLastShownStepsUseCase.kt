package by.step.draw.domain.usecases

import by.step.draw.data.repositories.StepCounterRepository

class SetLastShownStepsUseCase(private val stepCounterRepository: StepCounterRepository) {
    fun setSteps(steps: Int) {
        stepCounterRepository.saveLastShownSteps(steps)
    }
}