package by.step.draw.domain.usecases

import by.step.draw.data.repositories.StepCounterRepository
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class StepsSourceUseCase(private val repository: StepCounterRepository) {

    fun subscribe(): Observable<Pair<Int, Int>> {
        return Single.fromCallable { repository.getSavedTotalSteps() }
            .toObservable().concatWith(
                repository.subscribeOnTotalSteps()
            )
            .map { totalSteps: Int ->
                return@map Pair(repository.getLastShownSteps(), totalSteps)
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun getStepsInfo(): Single<Pair<Int, Int>> {
        return Single.fromCallable { repository.getSavedTotalSteps() }
            .map { totalSteps: Int ->
                return@map Pair(repository.getLastShownSteps(), totalSteps)
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }
}