package by.step.draw.domain.usecases

import by.step.draw.data.repositories.StepCounterRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class StepCounterStatusUseCase(private val stepCounterRepository: StepCounterRepository) {
    fun subscribe() = stepCounterRepository.subscribeOnStepCounterServiceState()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
}