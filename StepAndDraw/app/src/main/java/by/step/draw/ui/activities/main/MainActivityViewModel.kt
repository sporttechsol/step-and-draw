package by.step.draw.ui.activities.main

import androidx.lifecycle.MutableLiveData
import by.step.draw.App
import by.step.draw.data.models.step_counter_state.BaseStepCounterServiceState
import by.step.draw.data.models.step_counter_state.StepCounterServiceStoppedState
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

    private val initialFlagsUseCase: InitialFlagsUseCase by lazy {
        InitialFlagsUseCase(InitialFlagsRepository((App.instance.getDataManagerLocal())))
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

    val showIntroLiveData: MutableLiveData<Unit?> by lazy {
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

    val dialogDrawStepFinishedShowLiveData: MutableLiveData<Unit?> by lazy {
        MutableLiveData()
    }

    val dialogFinalShowLiveData: MutableLiveData<Unit?> by lazy {
        MutableLiveData()
    }

    val recreateScreenLiveData: SingleLiveEvent<Unit> by lazy {
        SingleLiveEvent()
    }

    val bFinalRewardVisibilityLiveData: MutableLiveData<Boolean> by lazy {
        MutableLiveData()
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

    val showNextLevelAlertDialogLiveData: SingleLiveEvent<Unit?> by lazy {
        SingleLiveEvent()
    }

    val showAfterIntroDialogLiveData: SingleLiveEvent<Unit> by lazy {
        SingleLiveEvent()
    }

    fun init() {
        if (!isStepCounterAvailableUseCase.isAvailable()) {
            AnalyticsSender.getInstance().noStepDetector()
            noDetectorDialogLiveData.value = Unit
        } else if (!initialFlagsUseCase.isIntroShown()) {
            showIntroLiveData.value = Unit
            progressLiveData.value = false
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

    fun introFinished() {
        AnalyticsSender.getInstance().introFinished()
        initialFlagsUseCase.setIntroShown(true)
        showIntroLiveData.value = null

        checkActivityRecognitionLiveData.value = Unit
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
                if (!initialFlagsUseCase.isAfterIntroAction()) {
                    initialFlagsUseCase.setAfterIntroAction(true)
                    onStartClick()
                    showAfterIntroDialogLiveData.value = Unit
                }
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

                    if (!showReachLimitDialogIfRequired(steps.first)) {
                        val coercedSteps = coerceSteps(steps.first, steps.second)
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
                    showReachLimitDialogIfRequired(steps.first)
                    return@subscribe
                }

                removeDisposable(stepsSubscriptionDisposable)

                if (!showReachLimitDialogIfRequired(steps.first)) {
                    val coercedSteps = coerceSteps(steps.first, steps.second)

                    setLastShownStepsUseCase.setSteps(coercedSteps)
                    AnalyticsSender.getInstance().drawingStarted(Pair(steps.first, coercedSteps))
                    drawPictureLiveData.value =
                        Pair(steps.first, coercedSteps)
                }
            }
        addDisposable(stepsSubscriptionDisposable)
    }

    private fun showReachLimitDialogIfRequired(stepsFrom: Int): Boolean {
        val drawingData = initSceneLiveData.value!!

        return if (stepsFrom == drawingData.maxDrawSteps && !initialFlagsUseCase.isAnimationIntroDialogShown()) {
            AnalyticsSender.getInstance().firstLimitReached()
            dialogDrawStepFinishedShowLiveData.value = Unit
            initialFlagsUseCase.setAnimationIntroDialogShown(true)
            true
        } else if (stepsFrom == drawingData.maxAnimationSteps) {
            if (!initialFlagsUseCase.isFinalDialogShown()) {
                AnalyticsSender.getInstance().secondLimitReached()
                initialProgressLiveData.value = stepsFrom
                dialogFinalShowLiveData.value = Unit
                initialFlagsUseCase.setFinalDialogShown(true)
                bFinalRewardVisibilityLiveData.value = true
            } else {
                if (bFinalRewardVisibilityLiveData.value == null) {
                    bFinalRewardVisibilityLiveData.value =
                        false // TODO costyl' party because can't get the state of MotionLayout and when stepsFrom == MaxAnimationSteps there is no transition animation (skip)
                }
            }
            true
        } else {
            if (initialFlagsUseCase.isFinalDialogShown()) {
                bFinalRewardVisibilityLiveData.value = true
            }
            false
        }
    }

    private fun coerceSteps(stepsFrom: Int, stepsTo: Int): Int {
        val drawMaxValue = initSceneLiveData.value!!.maxDrawSteps
        val animationMaxValue = initSceneLiveData.value!!.maxAnimationSteps
        return if (stepsFrom < drawMaxValue && stepsTo > drawMaxValue) {
            drawMaxValue
        } else if (stepsTo > animationMaxValue) {
            animationMaxValue
        } else {
            stepsTo
        }
    }

    fun dialogDrawStepFinishedClosed() {
        dialogDrawStepFinishedShowLiveData.value = null
        subscribeOnSteps()
    }

    fun dialogFinalClosed() {
        AnalyticsSender.getInstance().secondLimitDialogClosed()
        dialogFinalShowLiveData.value = null
        if (!initialFlagsUseCase.isHideElementsIntroShown()) {
            initialFlagsUseCase.setHideElementsIntroShown(true)
            showClickIntroLiveData.value = Unit
        }
    }

    fun onDrawViewClicked() {
        initialProgressLiveData.value?.let {
            if (it >= initSceneLiveData.value!!.maxAnimationSteps) {
                AnalyticsSender.getInstance().hideUIClicked()
                changeElementsVisibilityLiveData.value = Unit
            }
        }
    }

    fun onNextLevelClicked() {
        AnalyticsSender.getInstance().nextLevel()
        dialogFinalShowLiveData.value = null
        showNextLevelAlertDialogLiveData.value = Unit
    }

    fun onResetResultsClicked() {
        AnalyticsSender.getInstance().playAgain()
        addDisposable(resetCurrentStepsUseCase.reset().subscribe { it: Unit ->
            initialFlagsUseCase.resetFlags()
            serviceEnableLiveData.value = null
            recreateScreenLiveData.value = Unit
        })
    }

    fun onFinalRewardClicked() {
        AnalyticsSender.getInstance().rewardButtonClicked()
        dialogFinalShowLiveData.value = Unit
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

    private fun subscribeOnStepCounterServiceStatus() {
        addDisposable(stepCounterStatusUseCase.subscribe()
            .subscribe { state: BaseStepCounterServiceState ->
                stepServiceStateLiveData.value = state
            })
    }

    fun onStartClick() {
        if (!initialFlagsUseCase.isIntroShown()) {
            return
        }
        addDisposable(stepsSourceUseCase.getStepsInfo()
            .subscribe { steps: Pair<Int, Int> ->
                val drawingData = initSceneLiveData.value!!
                if (drawingData.maxAnimationSteps == steps.first) {
                    dialogFinalShowLiveData.value = Unit
                    stepServiceStateLiveData.value = StepCounterServiceStoppedState()
                } else {
                    serviceEnableLiveData.value = Unit
                }
            })
    }

    fun onStopClick() {
        if (!initialFlagsUseCase.isIntroShown()) {
            return
        }
        serviceEnableLiveData.value = null
    }

    fun onResetStepsClick() {
        addDisposable(resetCurrentStepsUseCase.reset().subscribe())
    }
}