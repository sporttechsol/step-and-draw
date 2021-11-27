package by.step.draw.domain.usecases

import by.step.draw.data.repositories.DrawingRepository
import by.step.draw.domain.models.drawing.DrawingData
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class GetDrawingDataUseCase(private val repository: DrawingRepository) {

    fun getDrawingData(): Single<DrawingData> {
        return Single.fromCallable {
            repository.getDrawingData()
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }
}