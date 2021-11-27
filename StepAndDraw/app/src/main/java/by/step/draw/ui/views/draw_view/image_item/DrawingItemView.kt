package by.step.draw.ui.views.draw_view.image_item

import android.animation.AnimatorInflater
import android.animation.ValueAnimator
import android.content.Context
import android.content.res.Configuration
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import by.step.draw.App
import by.step.draw.R
import by.step.draw.domain.models.drawing.item.DrawingItemData

class DrawingItemView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    val drawingItemData: DrawingItemData
) : View(
    context,
    attrs,
    defStyleAttr
) {
    private var alphaBkgrAnimator: ValueAnimator

    private var pathItem: Path

    private var paintItemFill: Paint
    private var paintBackgroundFade: Paint

    private var drawPath: Path? = null

    private var matrixForPath = Matrix()
    private var clipPath = Path()
    private var curDrawItemProgress: Float = -1f
    private var deltaDrawingSteps: Int = 0

    private var startDrawingPoint: Pair<Float, Float>? = null
    private var drawRadius: Float = 0f

    private var initRadius: Int = 0

    init {
        this.deltaDrawingSteps =
            drawingItemData.drawingSteps.stepsTo - drawingItemData.drawingSteps.stepsFrom

        pathItem = drawingItemData.paintData.path

        paintItemFill = Paint(Paint.ANTI_ALIAS_FLAG)
        paintItemFill.style = Paint.Style.FILL_AND_STROKE
        paintItemFill.color = drawingItemData.paintData.color
        paintItemFill.strokeWidth =
            resources.getDimensionPixelSize(R.dimen.stroke_item_width).toFloat()

        paintBackgroundFade = Paint(Paint.ANTI_ALIAS_FLAG)
        paintBackgroundFade.style = Paint.Style.FILL
        paintBackgroundFade.color = Color.BLACK

        initRadius = resources.getDimensionPixelSize(R.dimen.init_item_radius)
        clipPath = Path()

        alphaBkgrAnimator = AnimatorInflater.loadAnimator(
            App.instance,
            R.animator.bkgr_item_view_animator
        ) as ValueAnimator
    }

    override fun onDraw(canvas: Canvas?) {
        drawItem(canvas)
        super.onDraw(canvas)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stopBackgroundFadeAnimation()
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        stopBackgroundFadeAnimation()
    }

    fun update(steps: Float) {
        curDrawItemProgress = getCurDrawItemProgress(steps)

        if (steps >= drawingItemData.drawingSteps.stepsFrom &&
            steps < drawingItemData.drawingSteps.stepsTo
        ) {
            startBackgroundFadeAnimation()
        } else {
            stopBackgroundFadeAnimation()
        }

        invalidate()
    }

    private fun drawItem(canvas: Canvas?) {
        if (curDrawItemProgress == -1f || drawPath == null || startDrawingPoint == null) {
            return
        }

        clipPath.reset()
        clipPath.addCircle(
            startDrawingPoint!!.first,
            startDrawingPoint!!.second,
            initRadius + (drawRadius - initRadius) * curDrawItemProgress,
            Path.Direction.CW
        )

        if (alphaBkgrAnimator.isRunning) {
            paintBackgroundFade.alpha = alphaBkgrAnimator.animatedValue as Int
            canvas?.drawPath(drawPath!!, paintBackgroundFade)
        } else {
            paintItemFill.color = drawingItemData.paintData.color
        }

        canvas?.clipPath(clipPath)
        canvas?.drawPath(drawPath!!, paintItemFill)
    }

    fun recalculateSizes(
        widthSource: Int,
        heightSource: Int,
        parentWidth: Int,
        parentHeight: Int
    ) {
        val ratio = heightSource.toFloat() / widthSource.toFloat()
        val itemOriginalWidth = drawingItemData.width
        val itemOriginalHeight = drawingItemData.height
        val itemCords = drawingItemData.cords

        val resizedWidth = itemOriginalWidth * parentWidth / widthSource
        val resizedHeight =
            (itemOriginalHeight * parentWidth.toFloat() / heightSource.toFloat() * ratio)

        val ratioHeightSource = (parentWidth.toFloat() * ratio)
        val centerVerticalShift = (parentHeight.toFloat() - ratioHeightSource) / 2f

        this.layoutParams = FrameLayout.LayoutParams(resizedWidth.toInt(), resizedHeight.toInt())

        x = (itemCords.first * parentWidth.toFloat() / widthSource.toFloat())
        y =
            itemCords.second * parentWidth.toFloat() / heightSource.toFloat() * ratio + centerVerticalShift

        startDrawingPoint =
            Pair(
                drawingItemData.startDrawPoint.first * parentWidth.toFloat() / widthSource.toFloat(),
                drawingItemData.startDrawPoint.second * parentWidth.toFloat() / heightSource.toFloat() * ratio
            )

        drawRadius = drawingItemData.drawingRadius * parentWidth.toFloat() / widthSource.toFloat()

        matrixForPath.setScale(
            resizedWidth / drawingItemData.width,
            resizedHeight / drawingItemData.height
        )

        if (drawPath == null) {
            drawPath = Path()
        }

        pathItem.transform(matrixForPath, drawPath)
    }

    private fun getCurDrawItemProgress(steps: Float): Float {
        val stepsFrom = drawingItemData.drawingSteps.stepsFrom
        val stepsTo = drawingItemData.drawingSteps.stepsTo
        return if (steps < stepsFrom) {
            -1f
        } else if (steps > stepsTo) {
            1f
        } else {
            (steps - stepsFrom.toFloat()) / deltaDrawingSteps.toFloat()
        }
    }

    private fun startBackgroundFadeAnimation() {
        if (!alphaBkgrAnimator.isRunning) {
            alphaBkgrAnimator.addUpdateListener { invalidate() }
            alphaBkgrAnimator.start()
        }
    }

    private fun stopBackgroundFadeAnimation() {
        alphaBkgrAnimator.removeAllUpdateListeners()
        alphaBkgrAnimator.cancel()
    }
}