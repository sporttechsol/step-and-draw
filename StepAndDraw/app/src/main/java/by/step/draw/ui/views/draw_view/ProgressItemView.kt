package by.step.draw.ui.views.draw_view

import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import android.view.ViewPropertyAnimator
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.cardview.widget.CardView
import by.step.draw.App
import by.step.draw.R
import by.step.draw.domain.models.drawing.item.steps.BoundaryPoints
import kotlinx.android.synthetic.main.view_progress_item.view.*

class ProgressItemView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : CardView(context, attrs, defStyleAttr) {

    private val MOVE_ANIM_LENGTH = 900L

    private var parentWidth: Int = 0
    private var parentHeight: Int = 0
    private var animationRotation: Animation

    private var boundaryPoints: BoundaryPoints? = null
    private var minProgress = 2f
    private var maxProgress = 95f

    private var curX: Float = 0f
    private var curY: Float = 0f

    private val progressPadding: Int
    private val marginTop: Int

    private var moveAnimator: ViewPropertyAnimator? = null

    constructor(context: Context, parentWidth: Int, parentHeight: Int) : this(context) {
        this.parentWidth = parentWidth
        this.parentHeight = parentHeight
    }

    init {
        progressPadding = context.resources.getDimensionPixelSize(R.dimen.progress_padding)
        marginTop = resources.getDimensionPixelSize(R.dimen.top_margin_progress)

        View.inflate(context, R.layout.view_progress_item, this)
        val layoutParams = LayoutParams(
            resources.getDimensionPixelSize(R.dimen.progress_item_size),
            resources.getDimensionPixelSize(R.dimen.progress_item_size)
        )
        this.layoutParams = layoutParams

        setCardBackgroundColor(Color.WHITE)
        radius = resources.getDimensionPixelSize(R.dimen.progress_card_corner_radius).toFloat()
        cardElevation = resources.getDimensionPixelSize(R.dimen.progress_elevation).toFloat()

        animationRotation = AnimationUtils.loadAnimation(App.instance, R.anim.anim_rotation)
    }

    fun setPosition(boundaryPoints: BoundaryPoints) {
        if (this.boundaryPoints == null || this.boundaryPoints!!.leftPoint != boundaryPoints.leftPoint
            || this.boundaryPoints!!.topPoint != boundaryPoints.topPoint ||
            this.boundaryPoints!!.rightPoint != boundaryPoints.rightPoint ||
            this.boundaryPoints!!.bottomPoint != boundaryPoints.bottomPoint
        ) {
            this.boundaryPoints = boundaryPoints

            calculatePosition()
            if (visibility == GONE) {
                x = curX
                y = curY
                progress.startAnimation(animationRotation)
            } else {
                startMoveAnimation(curX, curY)
            }
            visibility = VISIBLE
        }
    }

    fun setData(percent: Float) {
        progress.progress = if (percent < minProgress) {
            minProgress
        } else if (percent > maxProgress) {
            maxProgress
        } else {
            percent
        }
    }

    fun calculatePosition(parentWidth: Int, parentHeight: Int) {
        this.parentWidth = parentWidth
        this.parentHeight = parentHeight
        calculatePosition()
    }

    private fun calculatePosition() {
        boundaryPoints?.let {
            val progressWidth = layoutParams.width
            val progressHeight = layoutParams.height

            var calculatedX = (it.leftPoint.x - progressWidth).toInt()
            var calculatedY = (it.leftPoint.y - progressHeight / 2).toInt()

            if (setCordsIfValid(calculatedX, calculatedY)) {
                return
            }

            calculatedX = (it.topPoint.x - progressWidth / 2).toInt()
            calculatedY = (it.topPoint.y - progressHeight).toInt()

            if (setCordsIfValid(calculatedX, calculatedY)) {
                return
            }

            calculatedX = it.rightPoint.x.toInt()
            calculatedY = (it.rightPoint.y - progressHeight / 2).toInt()

            if (setCordsIfValid(calculatedX, calculatedY)) {
                return
            }

            calculatedX = (it.bottomPoint.x - progressWidth / 2).toInt()
            calculatedY = it.bottomPoint.y.toInt()

            curX = calculatedX.toFloat()
            curY = calculatedY.toFloat()
        }
    }

    private fun setCordsIfValid(x: Int, y: Int): Boolean {
        if (x >= progressPadding && x <= parentWidth - progressPadding &&
            y >= progressPadding + marginTop && y <= parentHeight - progressPadding
        ) {
            curX = x.toFloat()
            curY = y.toFloat()

            return true
        }
        return false
    }

    private fun startMoveAnimation(x: Float, y: Float): ViewPropertyAnimator {
        stopAnimator(moveAnimator)
        val viewPropertyAnimator = animate()
            .x(x)
            .y(y)
            .setDuration(MOVE_ANIM_LENGTH)
        moveAnimator = viewPropertyAnimator
        moveAnimator?.start()
        return viewPropertyAnimator
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stop()
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        stop()
    }

    private fun stop() {
        stopAnimator(moveAnimator)
        moveAnimator = null
    }

    private fun stopAnimator(animator: ViewPropertyAnimator?) {
        animator?.setListener(null)
        animator?.cancel()
    }
}