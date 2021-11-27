package by.step.draw.domain.usecases

import by.step.draw.data.repositories.StepCounterRepository
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class ResetCountedStepsUseCase(private val repository: StepCounterRepository) {

    fun reset(): Single<Unit> {
        return Single.fromCallable {
            repository.saveTotalSteps(0)
            repository.saveLastShownSteps(0)
            repository.onCalculatedStepsChanged(repository.getSavedTotalSteps())
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }
}