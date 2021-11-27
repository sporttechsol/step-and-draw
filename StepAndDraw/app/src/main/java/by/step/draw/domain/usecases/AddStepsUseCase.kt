package by.step.draw.domain.usecases

import by.step.draw.data.repositories.StepCounterRepository
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class AddStepsUseCase(private val repository: StepCounterRepository) {

    fun add(additionalSteps: Int): Single<Unit> {
        return Single.fromCallable {
            repository.saveTotalSteps(repository.getSavedTotalSteps() + additionalSteps)
            repository.onCalculatedStepsChanged(repository.getSavedTotalSteps())
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }
}