package by.step.draw.ui.services

import android.os.Handler
import androidx.lifecycle.MutableLiveData
import by.step.draw.App
import by.step.draw.R
import by.step.draw.data.models.step_counter_state.BaseStepCounterServiceState
import by.step.draw.data.repositories.DrawingRepository
import by.step.draw.data.repositories.StepCounterRepository
import by.step.draw.domain.models.drawing.DrawingData
import by.step.draw.domain.usecases.GetDrawingDataUseCase
import by.step.draw.domain.usecases.SendStepCounterStatusUseCase
import by.step.draw.domain.usecases.StepCounterUseCase
import by.step.draw.domain.usecases.StepsSourceUseCase
import by.step.draw.ui.base.BaseViewModel
import by.step.draw.utils.AnalyticsSender

class StepServiceViewModel : BaseViewModel() {

    private val getDrawingDataUseCase: GetDrawingDataUseCase by lazy {
        GetDrawingDataUseCase(DrawingRepository(App.instance.getDataManagerLocal()))
    }

    private val stepsSourceUseCase: StepsSourceUseCase by lazy {
        StepsSourceUseCase(StepCounterRepository(App.instance.getDataManagerLocal()))
    }

    private val stepCounterUseCase: StepCounterUseCase by lazy {
        StepCounterUseCase(StepCounterRepository(App.instance.getDataManagerLocal()))
    }

    private val sendStepCounterUseCase: SendStepCounterStatusUseCase by lazy {
        SendStepCounterStatusUseCase(StepCounterRepository(App.instance.getDataManagerLocal()))
    }

    val textNotificationLiveData: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    private val DELAY_SENDING_TIME = 120000L
    private lateinit var drawData: DrawingData
    private var startServieTimeStamp = 0L

    private val handler: Handler = Handler()

    private val runnable: Runnable = object : Runnable {
        override fun run() {
            AnalyticsSender.getInstance().serviceIsRunning(System.currentTimeMillis() - startServieTimeStamp)
            handler.removeCallbacksAndMessages(null)
            handler.postDelayed(this, DELAY_SENDING_TIME)
        }
    }

    fun init() {
        startServieTimeStamp = System.currentTimeMillis()
        handler.postDelayed(runnable, DELAY_SENDING_TIME)
        addDisposable(getDrawingDataUseCase.getDrawingData()
            .subscribe { drawingData: DrawingData ->
                this.drawData = drawingData
                subscribeOnSteps()
            })
    }

    private fun subscribeOnSteps() {
        addDisposable(stepCounterUseCase.startCountSteps().subscribe())
        addDisposable(stepsSourceUseCase.subscribe().subscribe {
            if (it.second >= drawData.maxDrawSteps) {
                textNotificationLiveData.value =
                    App.instance.getString(R.string.counted_steps, it.second.toString()) + " \n(" +
                            App.instance.getString(R.string.max_amount_steps_reached) + ")"
            } else {
                textNotificationLiveData.value =
                    App.instance.getString(R.string.counted_steps, it.second.toString())
            }
        })
    }

    fun sendServiceStatus(baseStepCounterServiceState: BaseStepCounterServiceState) =
        sendStepCounterUseCase.sendState(baseStepCounterServiceState)


    override fun onCleared() {
        handler.removeCallbacksAndMessages(null)
        super.onCleared()
    }
}