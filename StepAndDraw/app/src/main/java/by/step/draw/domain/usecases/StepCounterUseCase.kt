package by.step.draw.domain.usecases

import by.step.draw.data.repositories.StepCounterRepository
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

// TODO it should be better to make ObservableSharedPreferences (skip)
class StepCounterUseCase(private val repository: StepCounterRepository) {

    private var previousStepCount: Int = -1

    // should be called only in service
    fun startCountSteps(): Observable<Unit> {
        return repository.startCountingSteps()
            .subscribeOn(Schedulers.io())
            .map { curTotalStepCount: Int ->
                if (previousStepCount == -1) {
                    previousStepCount = curTotalStepCount
                } else {
                    val difference = curTotalStepCount - previousStepCount
                    val totalSteps = repository.getSavedTotalSteps() + difference
                    repository.saveTotalSteps(totalSteps)
                    previousStepCount = curTotalStepCount
                    repository.onCalculatedStepsChanged(totalSteps)
                }
            }.observeOn(AndroidSchedulers.mainThread())
    }
}