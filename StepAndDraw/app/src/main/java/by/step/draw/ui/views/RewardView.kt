package by.step.draw.ui.views

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.constraintlayout.motion.widget.MotionLayout
import by.step.draw.R
import by.step.draw.utils.AnalyticsSender
import kotlinx.android.synthetic.main.view_reward.view.*
import nl.dionsegijn.konfetti.models.Shape
import nl.dionsegijn.konfetti.models.Size

// TODO make merge (skip)
class RewardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : MotionLayout(context, attrs, defStyleAttr), View.OnClickListener {

    private var LENGTH_TAKE_IT_ANIMATION =
        resources.getInteger(R.integer.reward_take_it_anim).toLong()

    private var listener: IOnRewardListener? = null
    private var lastShownSteps: Int = 0
    private var rewardSteps: Int = 0
    private var progressAnimator: ValueAnimator? = null

    init {
        isClickable = true
        isFocusable = true
        isFocusableInTouchMode = true
        inflate(context, R.layout.view_reward, this)

        mlRewardParent.setTransitionListener(object : TransitionListener {

            override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {
            }

            override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) {
                when (p1) {
                    R.id.step_1 -> {
                        rayView.setCurAnimProgress(p3)
                        tvRewardSteps.invalidate()
                        tvRewardSteps.requestLayout()
                    }
                    R.id.step_3 -> rayView.setCurAnimProgress(1 - p3)
                }
            }

            override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {
                when (currentId) {
                    R.id.step_2 -> {
                        bTakeIt.isEnabled = true
                        listener?.onRewardShowed()

                        viewKonfetti.build()
                            .addColors(Color.YELLOW, Color.GREEN, Color.MAGENTA, Color.RED)
                            .setDirection(0.0, 360.0)
                            .setSpeed(1f, 7f)
                            .setTimeToLive(5000L)
                            .setDelay(100L)
                            .addShapes(Shape.Square, Shape.Circle)
                            .addSizes(Size(12))
                            .setPosition(-50f, viewKonfetti.width + 50f, -50f, -50f)
                            .streamFor(400, 2000L)
                    }
                    R.id.step_3 -> {
                        val rewardValue = rewardProgress.progress.toInt()
                        progressAnimator = ObjectAnimator.ofFloat(rewardValue.toFloat(), 0f)

                        progressAnimator?.interpolator = LinearInterpolator()
                        progressAnimator?.duration = LENGTH_TAKE_IT_ANIMATION
                        progressAnimator?.addUpdateListener { animator: ValueAnimator ->
                            val curValue = animator.animatedValue as Float
                            setRewardProgressValue(curValue, rewardValue)
                            val bkgrProgressValue =
                                lastShownSteps + rewardValue - curValue.toInt()
                            listener?.onRewardStepsUpdated(bkgrProgressValue)
                        }

                        progressAnimator?.addListener(object : Animator.AnimatorListener {
                            override fun onAnimationStart(animation: Animator?) {}

                            override fun onAnimationEnd(animation: Animator?) {
                                showTransition(R.id.transition_close, true)
                            }

                            override fun onAnimationCancel(animation: Animator?) {}

                            override fun onAnimationRepeat(animation: Animator?) {}
                        })
                        progressAnimator?.start()
                        stepsLineView.startStepsAnimation()
                    }
                    R.id.step_4 -> listener?.onRewardFinished()
                }
            }

            override fun onTransitionTrigger(
                p0: MotionLayout?,
                p1: Int,
                p2: Boolean,
                p3: Float
            ) {

            }
        }
        )

        bTakeIt.setOnClickListener(this)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        progressAnimator?.removeAllUpdateListeners()
        progressAnimator?.removeAllListeners()
        progressAnimator?.cancel()
        progressAnimator?.end()

        mlRewardParent.setTransitionListener(null)
        mlRewardParent.clearAnimation()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.bTakeIt -> {
                if (bTakeIt.isEnabled) {
                    bTakeIt.isEnabled = false
                    takeReward()
                }
            }
        }
    }

    fun setLastShownSteps(lastShownSteps: Int) {
        this.lastShownSteps = lastShownSteps
    }

    fun setRewardSteps(rewardSteps: Int) {
        this.rewardSteps = rewardSteps
        setRewardProgressValue(rewardSteps.toFloat(), rewardSteps)
    }

    fun showReward(isAnimation: Boolean = true) {
        if (isAnimation) {
            post { showTransition(R.id.transition_reward) }
        } else {
            post { showTransition(R.id.transition_reward, false) }
            rayView.setCurAnimProgress(1f)
        }
    }

    private fun showTransition(id: Int, isAnimation: Boolean = true) {
        mlRewardParent.setTransition(id)
        if (isAnimation) {
            mlRewardParent.transitionToEnd()
        } else {
            progress = 1f
        }
    }

    private fun setRewardProgressValue(value: Float, maxValue: Int) {
        rewardProgress.progress = value
        rewardProgress.progressMax = maxValue.toFloat() / 0.75f
        tvRewardSteps.setText(context.getString(R.string.plus_amount, value.toInt()))
    }

    private fun takeReward() {
        AnalyticsSender.getInstance().takeReward(rewardSteps)
        listener?.onTakeRewardClicked()
        viewKonfetti.stopGracefully()
        stepsLineView.visibility = VISIBLE
        showTransition(R.id.transition_take_it)
    }

    fun setRewardListener(listener: IOnRewardListener) {
        this.listener = listener
    }
}

interface IOnRewardListener {
    fun onRewardStepsUpdated(steps: Int)
    fun onRewardFinished()
    fun onRewardShowed()
    fun onTakeRewardClicked()
}