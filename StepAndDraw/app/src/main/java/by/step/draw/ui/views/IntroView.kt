package by.step.draw.ui.views

import android.animation.Animator
import android.animation.AnimatorInflater
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.view.ViewPropertyAnimator
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import by.step.draw.App
import by.step.draw.R
import kotlinx.android.synthetic.main.view_intro.view.*

class IntroView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr), View.OnClickListener {

    private val NUMBER_INTRO_STEPS = 2

    private var listener: IntroListener? = null
    private var arrowAnimator: ValueAnimator? =
        AnimatorInflater.loadAnimator(App.instance, R.animator.arrow_animator) as ValueAnimator
    private var lengthOfArrowPath: Float = 0f

    private var curDrawingStep = 0
    private var curIntroStep = 1

    private val animation3StepUpdateListener: ValueAnimator.AnimatorUpdateListener =
        ValueAnimator.AnimatorUpdateListener { animation ->
            ivArrowAnim.alpha = (100f - animation.animatedValue as Float) / 100f
            ivArrowAnim.x = (animation.animatedValue as Float / 100f) * lengthOfArrowPath
        }

    private val animation3StepStateListener: Animator.AnimatorListener =
        object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator?) {
            }

            override fun onAnimationEnd(animation: Animator?) {
                setNextDrawingImage()
                arrowAnimator?.start()
            }

            override fun onAnimationCancel(animation: Animator?) {

            }

            override fun onAnimationRepeat(animation: Animator?) {
                setNextDrawingImage()
            }
        }

    private var drawingIds = arrayOf(
        R.drawable.ic_drawing_step_1,
        R.drawable.ic_drawing_step_2,
        R.drawable.ic_drawing_step_3
    )

    init {
        View.inflate(context, R.layout.view_intro, this)
        setBackgroundColor(ContextCompat.getColor(context, R.color.black_99))
        bNext.setOnClickListener(this)

        ivArrow1.post {
            startFadeInAnim(ivArrow1)
            startFadeInAnim(tvTitle1)
            startFadeInAnim(ivArrow2)
            startFadeInAnim(tvTitle2)
        }
    }

    override fun onVisibilityAggregated(isVisible: Boolean) {
        super.onVisibilityAggregated(isVisible)
        changeAnimationState(isVisible)
    }

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        super.onVisibilityChanged(changedView, visibility)
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            return
        }
        changeAnimationState(visibility == View.VISIBLE)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.bNext -> nextStep()
        }
    }

    private fun nextStep() {
        curIntroStep++
        when (curIntroStep) {
            NUMBER_INTRO_STEPS -> run2StepIntro()
            else -> listener?.onIntroClosed()
        }
    }

    fun onPlayServiceClicked() {
        if (curIntroStep < NUMBER_INTRO_STEPS) {
            nextStep()
        }
    }

    private fun changeAnimationState(isVisible: Boolean) {
        if (arrowAnimator == null) {
            return
        }
        if (isVisible && curIntroStep == NUMBER_INTRO_STEPS) {
            run2StepIntro()
        } else {
            arrowAnimator?.removeUpdateListener(animation3StepUpdateListener)
            arrowAnimator?.removeListener(animation3StepStateListener)
            arrowAnimator?.cancel()
            (ivSteps.drawable as AnimationDrawable).stop()
        }
    }

    private fun run2StepIntro() {
        bNext.setText(R.string.lets_go)
        if (tvTitle3.alpha == 0f) {
            startFadeInAnim(bMagic)
            startFadeInAnim(ivSteps)
            startFadeInAnim(ivDrawing)
            startFadeInAnim(flArrow)
            startFadeInAnim(ivArrow3)
            startFadeInAnim(ivArrowDown)
            startFadeInAnim(tvTitle3).setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    startDrawingAnimation()
                }
            })
        } else {
            startDrawingAnimation()
        }
    }

    private fun startDrawingAnimation() {
        flArrow.post {
            lengthOfArrowPath = flArrow.width.toFloat() - ivArrowAnim.width.toFloat()

            arrowAnimator?.addUpdateListener(animation3StepUpdateListener)
            arrowAnimator?.addListener(animation3StepStateListener)

            (ivSteps.drawable as AnimationDrawable).start()
            arrowAnimator?.start()
        }
    }

    private fun startFadeInAnim(view: View): ViewPropertyAnimator {
        val viewPropertyAnimator = view.animate()
            .alpha(1f)
            .setDuration(1000)
        viewPropertyAnimator.start()
        return viewPropertyAnimator
    }

    private fun setNextDrawingImage() {
        curDrawingStep++

        if (curDrawingStep > drawingIds.size - 1) {
            curDrawingStep = 0
        }

        ivDrawing.setImageResource(drawingIds[curDrawingStep])
    }

    fun setListener(listener: IntroListener) {
        this.listener = listener
    }
}

interface IntroListener {
    fun onIntroClosed()
}