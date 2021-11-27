package by.step.draw.ui.activities.main

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.animation.AnimationUtils
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import by.step.draw.App
import by.step.draw.R
import by.step.draw.data.models.step_counter_state.BaseStepCounterServiceState
import by.step.draw.data.models.step_counter_state.StepCounterServiceRunningState
import by.step.draw.data.models.step_counter_state.StepCounterServiceStoppedState
import by.step.draw.domain.models.drawing.DrawingData
import by.step.draw.ui.dialogs.InfoDialogButtonListener
import by.step.draw.ui.dialogs.InfoDialog
import by.step.draw.ui.services.StepService
import by.step.draw.ui.views.IOnRewardListener
import by.step.draw.ui.views.IntroListener
import by.step.draw.ui.views.StepCounterControllerListener
import by.step.draw.ui.views.draw_view.IOnDrawListener
import by.step.draw.utils.AnalyticsSender
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), View.OnLongClickListener, View.OnClickListener {

    companion object {
        private val ACTIVITY_RECOGNITION_REQUEST_CODE = 101
    }

    private val PERMISSION_ACTIVITY_RECOGNITION = arrayOf(
        Manifest.permission.ACTIVITY_RECOGNITION
    )

    private lateinit var viewModel: MainActivityViewModel

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)

        rewardView.setRewardListener(object : IOnRewardListener {
            override fun onRewardStepsUpdated(steps: Int) {
                stepsProgressView.updateBackgroundProgress(steps)
            }

            override fun onRewardFinished() {
                rewardView.visibility = GONE
                viewModel.onRewardFinished()
            }

            override fun onRewardShowed() {
                viewModel.setRewardState(REWARD_STATE.SHOWING)
            }

            override fun onTakeRewardClicked() {
                rewardView.elevation = resources.getDimension(R.dimen.reward_view_end_elevation)
            }
        })

        drawView.setDrawListener(object : IOnDrawListener {
            override fun onStepsUpdate(steps: Int) {
                stepsProgressView.updateMainProgress(steps)
            }

            override fun onDrawFinished() {
                viewModel.onDrawFinished()
            }
        })

        stepCounterController.setListener(object : StepCounterControllerListener {
            override fun onStopClicked() {
                viewModel.onStopClick()
            }

            override fun onPlayClicked() {
                viewModel.onStartClick()
                if (introView.visibility == VISIBLE) {
                    introView.onPlayServiceClicked()
                }
            }
        })

        introView.setListener(object : IntroListener {
            override fun onIntroClosed() {
                viewModel.introFinished()
            }
        })

        viewModel.noDetectorDialogLiveData.observe(this, {
            showAlertDialog(
                getString(R.string.alert_no_detector_title),
                getString(R.string.alert_no_detector_descr),
                positiveCallback = { dialog, which -> this.onBackPressed() }
            )
        })

        viewModel.stepServiceStateLiveData.observe(this, { state: BaseStepCounterServiceState ->
            when (state) {
                is StepCounterServiceRunningState ->
                    stepCounterController.initRunningStatus()
                is StepCounterServiceStoppedState ->
                    stepCounterController.initStoppedStatus()
            }
        })

        viewModel.serviceEnableLiveData.observe(this, {
            it?.let {
                StepService.start(App.instance)
            } ?: run {
                StepService.stop(App.instance)
            }
        })

        viewModel.showIntroLiveData.observe(this, {
            if (it != null) {
                introView.visibility = VISIBLE
            } else {
                introView.animate()
                    .scaleX(0f)
                    .scaleY(0f)
                    .setDuration(400)
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator?) {
                            super.onAnimationEnd(animation)
                            introView.visibility = GONE
                        }
                    })
                    .start()
            }
        })

        viewModel.showAfterIntroDialogLiveData.observe(this, {
            showLimitStepInfoDialog(R.string.start_walking_title, R.string.start_walking_descr,
                bCancelText = R.string.b_continue,
                resIcon = R.drawable.ic_walking,
                cancelListener = object : InfoDialogButtonListener {
                    override fun onClicked() {
                    }
                })
        })

        viewModel.bServiceControllerEnabledLiveData.observe(this, {
            stepCounterController.enable(it)
        })

        viewModel.initSceneLiveData.observe(this, { drawingData: DrawingData ->
            drawView.visibility = VISIBLE
            drawView.setData(drawingData)
            stepsProgressView.setData(drawingData)
        })

        viewModel.initialProgressLiveData.observe(
            this, { stepsInitial: Int ->
                drawView.setLastShownSteps(stepsInitial)
                rewardView.setLastShownSteps(stepsInitial)
            })

        viewModel.showRewardLiveData.observe(this, { pair: Pair<REWARD_STATE, Int>? ->
            if (pair != null) {
                when (pair.first) {
                    REWARD_STATE.NOT_SHOWN -> {
                        rewardView.visibility = VISIBLE
                        rewardView.setRewardSteps(pair.second)
                        rewardView.showReward()
                    }
                    REWARD_STATE.SHOWING -> {
                        rewardView.visibility = VISIBLE
                        rewardView.setRewardSteps(pair.second)
                        rewardView.showReward(false)
                    }
                }
            }
        })

        viewModel.drawPictureLiveData.observe(this, { pair: Pair<Int, Int>? ->
            pair?.let { drawView.drawPicture(it) }
        })

        viewModel.dialogDrawStepFinishedShowLiveData.observe(this, {
            it?.let { showDrawStepFinishedDialog() }
        })

        viewModel.dialogFinalShowLiveData.observe(this, {
            it?.let { showDialogFinal() }
        })

        viewModel.recreateScreenLiveData.observe(this, {
            viewModelStore.clear()
            recreate()
        })

        viewModel.bFinalRewardVisibilityLiveData.observe(this, { isAnimation: Boolean ->
            val shake = AnimationUtils.loadAnimation(App.instance, R.anim.anim_b_final_reward)
            mlParent.setTransition(R.id.transition_final_reward)
            if (isAnimation) {
                mlParent.transitionToEnd()
            } else {
                mlParent.progress = 1f
            }
            bFinalReward.startAnimation(shake)
        })

        viewModel.checkActivityRecognitionLiveData.observe(this, {
            it?.let { checkPermissions() }
        })

        viewModel.showPermissionRationaleDialogLiveData.observe(this, {
            it?.let {
                showAlertDialog(
                    getString(R.string.permission_required_title),
                    getString(R.string.permission_required_descr),
                    positiveCallback = { dialog, which ->
                        viewModel.permissionRationaleDialogClosed()
                        requestPermissions(
                            PERMISSION_ACTIVITY_RECOGNITION,
                            ACTIVITY_RECOGNITION_REQUEST_CODE
                        )
                    })
            }
        })

        viewModel.showPermissionBlockedDialogLiveData.observe(this, {
            it?.let {
                showAlertDialog(
                    getString(R.string.permission_required_title),
                    getString(R.string.permission_blocked_descr),
                    positiveCallback = { dialog, which ->
                        val appSettingsIntent = Intent(
                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.parse("package:" + this.packageName)
                        )
                        startActivityForResult(
                            appSettingsIntent,
                            ACTIVITY_RECOGNITION_REQUEST_CODE
                        )
                    })
            }
        })

        viewModel.showNextLevelAlertDialogLiveData.observe(this, {
            it?.let {
                showAlertDialog(
                    getString(R.string.title_next_level),
                    getString(R.string.descr_next_level),
                    positiveCallback = { dialog, which -> viewModel.onResetResultsClicked() },
                    negativeCallback = null,
                    neutralCallback = { dialog, which -> },
                    positiveButtonText = R.string.play_again,
                    negativeButtonText = null,
                    neutralButtonText = R.string.cancel
                )
            }
        })

        viewModel.showClickIntroLiveData.observe(this, {
            it?.let {
                mlParent.setTransition(R.id.transition_to_click_intro)
                mlParent.transitionToEnd()
            }
        })

        viewModel.changeElementsVisibilityLiveData.observe(this, {
            when (mlParent.currentState) {
                R.id.step_final_reward ->
                    mlParent.setTransition(R.id.transition_hide_elements_from_init)
                R.id.step_picture_showed ->
                    mlParent.setTransition(R.id.transition_show_elements)
            }
            mlParent.transitionToEnd()
        })

        viewModel.progressLiveData.observe(this, { isShow: Boolean ->
            if (isShow) {
                progressBar.visibility = VISIBLE
            } else {
                progressBar.visibility = GONE
                stepCounterController.visibility = VISIBLE
                cvProgressContainer.visibility = VISIBLE
            }
        })

        //  stepsProgressView.setOnLongClickListener(this)
        bFinalReward.setOnClickListener(this)
        vBackgroundClickTutorial.setOnClickListener(this)
        drawView.setOnClickListener(this)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        for (fragment in supportFragmentManager.fragments) {
            supportFragmentManager.beginTransaction().remove(fragment).commitAllowingStateLoss()
        }
        super.onSaveInstanceState(outState)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode != ACTIVITY_RECOGNITION_REQUEST_CODE) {
            return
        }

        when {
            ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) -> {
                AnalyticsSender.getInstance().permissionGranted()
                viewModel.permissionGranted()
            }
            else -> viewModel.showPermissionBlockedDialog()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == ACTIVITY_RECOGNITION_REQUEST_CODE) {
            checkPermissions()
        }
    }

    private fun checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            when {
                ContextCompat.checkSelfPermission(
                    this, PERMISSION_ACTIVITY_RECOGNITION[0]
                ) == PackageManager.PERMISSION_GRANTED -> {
                    viewModel.permissionGranted()
                }
                shouldShowRequestPermissionRationale(PERMISSION_ACTIVITY_RECOGNITION[0]) -> {
                    viewModel.showPermissionRationaleDialog()
                }
                else -> requestPermissions(
                    PERMISSION_ACTIVITY_RECOGNITION,
                    ACTIVITY_RECOGNITION_REQUEST_CODE
                )
            }
        } else {
            viewModel.permissionGranted()
        }
    }

    override fun onLongClick(v: View): Boolean {
        when (v.id) {
            R.id.stepsProgressView -> viewModel.onResetStepsClick()
        }
        return true
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.bFinalReward -> viewModel.onFinalRewardClicked()
            R.id.vBackgroundClickTutorial -> {
                mlParent.setTransition(R.id.transition_hide_elements_from_intro)
                mlParent.transitionToEnd()
            }
            R.id.drawView -> viewModel.onDrawViewClicked()
        }
    }

    private fun showDrawStepFinishedDialog() {
        showLimitStepInfoDialog(
            title = R.string.congratulations,
            descr = R.string.dialog_draw_finished_descr,
            bCancelText = R.string.b_continue,
            resIcon = R.drawable.ic_fire_work,
            cancelListener = object : InfoDialogButtonListener {
                override fun onClicked() {
                    viewModel.dialogDrawStepFinishedClosed()
                }
            })
    }

    private fun showDialogFinal() {
        showLimitStepInfoDialog(
            R.string.congratulations,
            R.string.dialog_final_descr,
            R.string.close,
            null,
            R.string.next_level,
            R.drawable.ic_fire_work,
            object : InfoDialogButtonListener {
                override fun onClicked() {
                    viewModel.dialogFinalClosed()
                }
            },
            null,
            object : InfoDialogButtonListener {
                override fun onClicked() {
                    viewModel.onNextLevelClicked()
                }
            })
    }

    private fun showLimitStepInfoDialog(
        title: Int, descr: Int, bCancelText: Int,
        bOption1Text: Int? = null, bOption2Text: Int? = null,
        resIcon: Int,
        cancelListener: InfoDialogButtonListener,
        bOption1Listener: InfoDialogButtonListener? = null,
        bOption2Listener: InfoDialogButtonListener? = null
    ) {
        supportFragmentManager.findFragmentByTag(InfoDialog::class.java.simpleName)
            ?: run {
                val dialog = InfoDialog.newInstance(
                    title, descr, bCancelText, resIcon, bOption1Text, bOption2Text
                )
                dialog.setCancelListener(cancelListener)
                bOption1Listener?.let { dialog.setOption1Listener(it) }
                bOption2Listener?.let { dialog.setOption2Listener(it) }

                dialog.showNow(
                    supportFragmentManager,
                    InfoDialog::class.java.simpleName
                )
            }
    }

    private fun showAlertDialog(
        title: String,
        message: String,
        iconRes: Int? = null,
        positiveCallback: ((DialogInterface?, Int) -> Unit?)? = null,
        negativeCallback: ((DialogInterface?, Int) -> Unit?)? = null,
        neutralCallback: ((DialogInterface?, Int) -> Unit?)? = null,
        positiveButtonText: Int? = null,
        negativeButtonText: Int? = null,
        neutralButtonText: Int? = null
    ) {
        val dialog = AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setCancelable(false)

        iconRes?.let { dialog.setIcon(it) }

        positiveCallback?.let {
            dialog.setPositiveButton(
                positiveButtonText ?: R.string.ok
            ) { dialog, which -> it(dialog, which) }
        }

        negativeCallback?.let {
            dialog.setNegativeButton(negativeButtonText ?: R.string.cancel) { dialog, which ->
                it(
                    dialog,
                    which
                )
            }
        }

        neutralCallback?.let {
            dialog.setNeutralButton(neutralButtonText ?: R.string.cancel) { dialog, which ->
                it(
                    dialog,
                    which
                )
            }
        }

        dialog.show()
    }
}

// TODO it should not be in a ViewModel because it is kind of strange to save state of view in ViewModel if view can save it yourself (skip)
enum class REWARD_STATE {
    NOT_SHOWN, SHOWING
}