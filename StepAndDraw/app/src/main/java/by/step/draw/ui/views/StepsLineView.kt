package by.step.draw.ui.views

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.content.res.ColorStateList
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.animation.addListener
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import androidx.core.widget.ImageViewCompat
import by.step.draw.R

class StepsLineView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var velocityOfItem = 0f
    private var lengthOfAnim: Long = 0
    private var startTime: Long = 0
    private var respawnTime = 0L
    private var timeForAllDistance = 0L
    private var handlerSteps: Handler? = null

    private val runnableSteps = object : Runnable {
        override fun run() {
            if (lengthOfAnim - (System.currentTimeMillis() - startTime) < timeForAllDistance) {
                handlerSteps?.removeCallbacks(this)
            } else {
                val ivSteps = createStepImage()
                addView(ivSteps)

                val animator = ObjectAnimator.ofFloat(height.toFloat(), -width.toFloat())
                animator.interpolator = LinearInterpolator()
                animator.duration = timeForAllDistance
                animator.addUpdateListener { animator: ValueAnimator ->
                    val curValue = animator.animatedValue as Float

                    ivSteps.y = curValue
                }
                animator.addListener(onEnd = {
                    removeView(ivSteps)
                })
                animator.start()
                handlerSteps?.postDelayed(this, respawnTime)
            }
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        velocityOfItem = resources.getDimensionPixelSize(R.dimen.steps_reward_velocity).toFloat()
    }

    fun startStepsAnimation() {
        handlerSteps = Handler(Looper.getMainLooper())
        lengthOfAnim = resources.getInteger(R.integer.reward_take_it_anim).toLong()
        respawnTime = (width.toFloat() / velocityOfItem).toLong()
        timeForAllDistance =
            ((height.toLong() + width.toLong()).toFloat() / velocityOfItem).toLong()
        startTime = System.currentTimeMillis()
        handlerSteps?.post(runnableSteps)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        handlerSteps?.removeCallbacks(runnableSteps)
        handlerSteps = null
    }

    private fun createStepImage(): ImageView {
        val layoutParamImageView = LayoutParams(width, width)
        val imageView = ImageView(context)
        imageView.layoutParams = layoutParamImageView
        imageView.y = height.toFloat()
        imageView.setImageResource(R.drawable.ic_steps_reward)
        imageView.setPadding(resources.getDimension(R.dimen.steps_reward_padding).toInt())
        ImageViewCompat.setImageTintList(
            imageView,
            ColorStateList.valueOf(ContextCompat.getColor(context, R.color.blue_219))
        )
        return imageView
    }
}