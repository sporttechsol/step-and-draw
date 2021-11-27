package by.step.draw.ui.activities.main

import androidx.lifecycle.MutableLiveData
import by.step.draw.App
import by.step.draw.data.models.step_counter_state.BaseStepCounterServiceState
import by.step.draw.data.repositories.DrawingRepository
import by.step.draw.data.repositories.InitialFlagsRepository
import by.step.draw.data.repositories.StepCounterRepository
import by.step.draw.domain.*
import by.step.draw.domain.models.drawing.DrawingData
import by.step.draw.domain.usecases.*
import by.step.draw.ui.base.BaseViewModel
import by.step.draw.ui.base.SingleLiveEvent
import by.step.draw.utils.AnalyticsSender
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit

class MainActivityViewModel : BaseViewModel() {

    private var rewardState = REWARD_STATE.NOT_SHOWN
    private var stepsSubscriptionDisposable: Disposable? = null

    private val initialFlagsUseCase: InitialFlagsUseCase by lazy {
        InitialFlagsUseCase(InitialFlagsRepository((App.instance.getDataManagerLocal())))
    }

    private val isStepCounterAvailableUseCase: IsStepCounterAvailableUseCase by lazy {
        IsStepCounterAvailableUseCase()
    }

    private val stepsSourceUseCase: StepsSourceUseCase by lazy {
        StepsSourceUseCase(StepCounterRepository(App.instance.getDataManagerLocal()))
    }

    private val resetCurrentStepsUseCase: ResetCountedStepsUseCase by lazy {
        ResetCountedStepsUseCase(StepCounterRepository(App.instance.getDataManagerLocal()))
    }

    private val stepCounterStatusUseCase: StepCounterStatusUseCase by lazy {
        StepCounterStatusUseCase(StepCounterRepository(App.instance.getDataManagerLocal()))
    }

    private val setLastShownStepsUseCase: SetLastShownStepsUseCase by lazy {
        SetLastShownStepsUseCase(StepCounterRepository(App.instance.getDataManagerLocal()))
    }

    private val getDrawingDataUseCase: GetDrawingDataUseCase by lazy {
        GetDrawingDataUseCase(DrawingRepository(App.instance.getDataManagerLocal()))
    }

    val noDetectorDialogLiveData: MutableLiveData<Unit> by lazy {
        MutableLiveData()
    }

    val initialProgressLiveData: MutableLiveData<Int> by lazy {
        MutableLiveData()
    }

    val drawPictureLiveData: MutableLiveData<Pair<Int, Int>> by lazy {
        init()
        MutableLiveData()
    }

    val stepServiceStateLiveData: MutableLiveData<BaseStepCounterServiceState> by lazy {
        MutableLiveData()
    }

    val initSceneLiveData: MutableLiveData<DrawingData> by lazy {
        MutableLiveData()
    }

    val bServiceControllerEnabledLiveData: MutableLiveData<Boolean> by lazy {
        MutableLiveData()
    }

    val showRewardLiveData: MutableLiveData<Pair<REWARD_STATE, Int>?> by lazy {
        MutableLiveData()
    }

    val serviceEnableLiveData: SingleLiveEvent<Unit?> by lazy {
        SingleLiveEvent()
    }

    val checkActivityRecognitionLiveData: SingleLiveEvent<Unit> by lazy {
        SingleLiveEvent()
    }

    val showPermissionRationaleDialogLiveData: MutableLiveData<Unit?> by lazy {
        MutableLiveData()
    }

    val showPermissionBlockedDialogLiveData: MutableLiveData<Unit?> by lazy {
        MutableLiveData()
    }

    val progressLiveData: MutableLiveData<Boolean> by lazy {
        MutableLiveData()
    }

    val showClickIntroLiveData: SingleLiveEvent<Unit> by lazy {
        SingleLiveEvent()
    }

    val changeElementsVisibilityLiveData: SingleLiveEvent<Unit> by lazy {
        SingleLiveEvent()
    }

    fun init() {
        if (!isStepCounterAvailableUseCase.isAvailable()) {
            AnalyticsSender.getInstance().noStepDetector()
            noDetectorDialogLiveData.value = Unit
        } else {
            checkActivityRecognitionLiveData.value = Unit
        }
    }

    fun showPermissionRationaleDialog() {
        showPermissionRationaleDialogLiveData.value = Unit
    }

    fun permissionRationaleDialogClosed() {
        showPermissionRationaleDialogLiveData.value = null
    }

    fun showPermissionBlockedDialog() {
        AnalyticsSender.getInstance().permissionBlocked()
        showPermissionBlockedDialogLiveData.value = Unit
    }

    fun permissionGranted() {
        showPermissionBlockedDialogLiveData.value = null
        getDrawingData()
    }

    private fun getDrawingData() {
        addDisposable(getDrawingDataUseCase.getDrawingData()
            .delaySubscription(1, TimeUnit.SECONDS)
            .doOnSubscribe {
                bServiceControllerEnabledLiveData.value = false
                progressLiveData.value = true
            }
            .subscribe { drawingData: DrawingData ->
                initSceneLiveData.value = drawingData
                checkReward()
            })
    }

    private fun checkReward() {
        subscribeOnStepCounterServiceStatus()
        addDisposable(stepsSourceUseCase.getStepsInfo()
            .subscribe { steps: Pair<Int, Int> ->
                bServiceControllerEnabledLiveData.value = true
                if (steps.second - steps.first == 0) {
                    subscribeOnSteps()
                } else {
                    initialProgressLiveData.value = steps.first
                    progressLiveData.value = false

                    if (!showHideElementsIntroIfRequired(steps.first)) {

                        val coercedSteps =
                            steps.second.coerceAtMost(initSceneLiveData.value!!.maxDrawSteps)
                        showRewardLiveData.value = Pair(
                            rewardState,
                            coercedSteps - steps.first
                        )
                    }
                }
            })
    }

    private fun subscribeOnSteps() {
        removeDisposable(stepsSubscriptionDisposable)
        stepsSubscriptionDisposable = stepsSourceUseCase.subscribe()
            .subscribe { steps: Pair<Int, Int> ->
                bServiceControllerEnabledLiveData.value = true
                if (steps.second - steps.first == 0) {
                    initialProgressLiveData.value = steps.first
                    progressLiveData.value = false
                    showHideElementsIntroIfRequired(steps.first)
                    return@subscribe
                }

                removeDisposable(stepsSubscriptionDisposable)

                if (!showHideElementsIntroIfRequired(steps.first)) {

                    val coercedSteps =
                        steps.second.coerceAtMost(initSceneLiveData.value!!.maxDrawSteps)

                    setLastShownStepsUseCase.setSteps(coercedSteps)
                    AnalyticsSender.getInstance().drawingStarted(Pair(steps.first, coercedSteps))
                    drawPictureLiveData.value =
                        Pair(steps.first, coercedSteps)
                }
            }
        addDisposable(stepsSubscriptionDisposable)
    }

    fun onDrawViewClicked() {
        initialProgressLiveData.value?.let {
            if (it >= initSceneLiveData.value!!.maxDrawSteps) {
                AnalyticsSender.getInstance().hideUIClicked()
                changeElementsVisibilityLiveData.value = Unit
            }
        }
    }

    fun setRewardState(rewardState: REWARD_STATE) {
        showRewardLiveData.value = Pair(rewardState, showRewardLiveData.value!!.second)
    }

    fun onRewardFinished() {
        showRewardLiveData.value = null
        AnalyticsSender.getInstance().rewardIsFinished()
        subscribeOnSteps()
    }

    fun onDrawFinished() {
        drawPictureLiveData.value = null
        subscribeOnSteps()
    }

    fun showHideElementsIntroIfRequired(stepsFrom: Int): Boolean {
        val drawingData = initSceneLiveData.value!!
        if (stepsFrom == drawingData.maxDrawSteps && !initialFlagsUseCase.isHideElementsIntroShown()) {
            initialFlagsUseCase.setHideElementsIntroShown(true)
            showClickIntroLiveData.value = Unit
            return true
        } else if (stepsFrom >= drawingData.maxDrawSteps) {
            return true
        }
        return false
    }

    private fun subscribeOnStepCounterServiceStatus() {
        addDisposable(stepCounterStatusUseCase.subscribe()
            .subscribe { state: BaseStepCounterServiceState ->
                stepServiceStateLiveData.value = state
            })
    }

    fun onStartClick() {
        serviceEnableLiveData.value = Unit
    }

    fun onStopClick() {
        serviceEnableLiveData.value = null
    }

    fun onResetStepsClick() {
        addDisposable(resetCurrentStepsUseCase.reset().subscribe())
    }
}