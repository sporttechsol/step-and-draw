package by.step.draw.ui.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import by.step.draw.R
import kotlinx.android.synthetic.main.view_step_counter_controller.view.*

class StepCounterControllerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), View.OnClickListener {

    private var listener: StepCounterControllerListener? = null
    private var isAnimRunning = false

    private val hidePlayAnimListener = object : Animation.AnimationListener {
        override fun onAnimationStart(animation: Animation?) {
            isAnimRunning = true
        }

        override fun onAnimationEnd(animation: Animation?) {
            fabStop.visibility = View.VISIBLE
            fabPlay.visibility = View.GONE
            showStop()
        }

        override fun onAnimationRepeat(animation: Animation?) {
        }
    }

    private val showPlayAnimListener = object : Animation.AnimationListener {
        override fun onAnimationStart(animation: Animation?) {

        }

        override fun onAnimationEnd(animation: Animation?) {
            isAnimRunning = false
        }

        override fun onAnimationRepeat(animation: Animation?) {
        }
    }

    private val hideStopAnimListener = object : Animation.AnimationListener {

        override fun onAnimationStart(animation: Animation?) {
            isAnimRunning = true
        }

        override fun onAnimationEnd(animation: Animation?) {
            fabPlay.visibility = View.VISIBLE
            fabStop.visibility = View.GONE
            showPlay()
        }

        override fun onAnimationRepeat(animation: Animation?) {
        }
    }

    private val showStopAnimListener = object : Animation.AnimationListener {
        override fun onAnimationStart(animation: Animation?) {

        }

        override fun onAnimationEnd(animation: Animation?) {
            isAnimRunning = false
        }

        override fun onAnimationRepeat(animation: Animation?) {
        }
    }

    init {
        View.inflate(context, R.layout.view_step_counter_controller, this)
        fabPlay.setOnClickListener(this)
        fabStop.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        if (isAnimRunning) {
            return
        }
        when (v.id) {
            R.id.fabPlay -> {
                hidePlay()
                listener?.onPlayClicked()
            }
            R.id.fabStop -> {
                hideStop()
                listener?.onStopClicked()
            }
        }
    }

    private fun hidePlay() {
        val scaleInAnimation = AnimationUtils.loadAnimation(context, R.anim.anim_scale_in)
        scaleInAnimation.setAnimationListener(hidePlayAnimListener)
        fabPlay.startAnimation(scaleInAnimation)
    }

    private fun hideStop() {
        val scaleInAnimation = AnimationUtils.loadAnimation(context, R.anim.anim_scale_in)
        scaleInAnimation.setAnimationListener(hideStopAnimListener)
        fabStop.startAnimation(scaleInAnimation)
    }

    private fun showStop() {
        val scaleOutAnimation = AnimationUtils.loadAnimation(context, R.anim.anim_scale_out)
        fabStop.startAnimation(scaleOutAnimation)
        scaleOutAnimation.setAnimationListener(showStopAnimListener)
    }

    private fun showPlay() {
        val scaleOutAnimation = AnimationUtils.loadAnimation(context, R.anim.anim_scale_out)
        fabPlay.startAnimation(scaleOutAnimation)
        scaleOutAnimation.setAnimationListener(showPlayAnimListener)
    }

    fun initRunningStatus() {
        stopAnimation()
        val playView = fabPlay
        val stopView = fabStop
        removeView(playView)
        removeView(stopView)
        addView(playView)
        addView(stopView)
        fabStop.visibility = View.VISIBLE
        fabPlay.visibility = View.GONE
    }

    fun initStoppedStatus() {
        stopAnimation()
        val playView = fabPlay
        val stopView = fabStop
        removeView(playView)
        removeView(stopView)
        addView(stopView)
        addView(playView)
        fabPlay.visibility = View.VISIBLE
        fabStop.visibility = View.GONE
    }

    private fun stopAnimation() {
        isAnimRunning = false
        fabPlay.animation?.setAnimationListener(null)
        fabStop.animation?.setAnimationListener(null)
        fabPlay.clearAnimation()
        fabPlay.clearAnimation()
        fabPlay.animation?.cancel()
        fabStop.animation?.cancel()
    }

    fun setListener(listener: StepCounterControllerListener) {
        this.listener = listener
    }

    fun enable(enable: Boolean) {
        fabPlay.isEnabled = enable
        fabStop.isEnabled = enable
    }
}

interface StepCounterControllerListener {
    fun onStopClicked()
    fun onPlayClicked()
}