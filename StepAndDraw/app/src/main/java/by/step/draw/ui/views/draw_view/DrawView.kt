package by.step.draw.ui.views.draw_view

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import androidx.core.animation.addListener
import by.step.draw.domain.models.drawing.BorderData
import by.step.draw.domain.models.drawing.DrawingData
import by.step.draw.domain.models.drawing.item.DrawingItemData
import by.step.draw.ui.views.draw_view.image_item.DrawingBorderItemView
import by.step.draw.ui.views.draw_view.image_item.DrawingItemView
import by.step.draw.utils.AnalyticsSender

class DrawView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val LENGTH_OF_PICTURE_ANIM = 150000L

    private var curWidth: Int = 0
    private var curHeight: Int = 0

    private var listener: IOnDrawListener? = null

    private var lastShownSteps: Int = 0
    private var animatedToSteps: Int = 0

    private val itemsImage = arrayListOf<DrawingItemView>()
    private val itemsBorder = arrayListOf<DrawingBorderItemView>()

    private val animationBasePoints = arrayListOf<Float>()
    private var isAnimationPlaying = false
    private var isViewHidden = false

    private var progressAnimator: ValueAnimator? = null

    private lateinit var drawingData: DrawingData

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        destroyAnimator()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val widthMes = MeasureSpec.getSize(widthMeasureSpec)
        val heightMes = MeasureSpec.getSize(heightMeasureSpec)
        if (widthMes != curWidth || heightMes != curHeight) {
            curWidth = widthMes
            curHeight = heightMes

            updateItems()
        }
    }

    override fun onVisibilityAggregated(isVisible: Boolean) {
        super.onVisibilityAggregated(isVisible)
        if (!isAnimationPlaying) {
            return
        }
        if (isVisible) {
            resumeAnimation()
        } else {
            pauseAnimation()
        }
    }

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        super.onVisibilityChanged(changedView, visibility)
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M || !isAnimationPlaying) {
            return
        }
        if (visibility == View.VISIBLE) {
            resumeAnimation()
        } else {
            pauseAnimation()
        }
    }

    private fun pauseAnimation() {
        isViewHidden = true
        progressAnimator?.pause()
    }

    private fun resumeAnimation() {
        isViewHidden = false
        progressAnimator?.resume()

    }

    fun setLastShownSteps(lastShownSteps: Int) {
        this.lastShownSteps = lastShownSteps
        drawValue(lastShownSteps.toFloat(), true)
    }

    fun drawPicture(pair: Pair<Int, Int>) {
        this.lastShownSteps = pair.first
        this.animatedToSteps = pair.second
        startMagicAnimation()
    }

    private fun startMagicAnimation() {
        animationBasePoints.clear()
        animationBasePoints.add(lastShownSteps.toFloat())

        for (item in drawingData.items) {
            if (item.drawingSteps.stepsTo in (lastShownSteps + 1) until animatedToSteps) {
                animationBasePoints.add(item.drawingSteps.stepsTo.toFloat())
            }
        }

        animationBasePoints.add(animatedToSteps.toFloat())
        animationBasePoints.sort()
        startAnimation()
    }

    private fun startAnimation() {
        val fromSteps = animationBasePoints[0]
        val toSteps = animationBasePoints[1]

        progressAnimator = ObjectAnimator.ofFloat(fromSteps, toSteps)
        progressAnimator?.interpolator = LinearInterpolator()
        progressAnimator?.duration =
            ((toSteps - fromSteps) / drawingData.maxDrawSteps.toFloat() * LENGTH_OF_PICTURE_ANIM.toFloat()).toLong()
        progressAnimator?.addUpdateListener {
            drawValue(it.animatedValue as Float, false)
        }

        progressAnimator?.addListener(onEnd = {
            isAnimationPlaying = false
            animationBasePoints.removeAt(0)
            if (animationBasePoints.size <= 1) {
                destroyAnimator()
                AnalyticsSender.getInstance()
                    .drawingIsFinished(Pair(lastShownSteps, animatedToSteps))
                listener?.onDrawFinished()
            } else {
                startAnimation()
            }
        })

        isAnimationPlaying = true
        progressAnimator?.start()
        if (isViewHidden) {
            pauseAnimation()
        }
    }

    private fun drawValue(steps: Float, isInit: Boolean) {
        listener?.onStepsUpdate(steps.toInt())

        itemsImage.forEach { itemDrawing: DrawingItemView ->
            itemDrawing.update(steps)
        }

        itemsBorder.forEach {
            it.update(steps, isInit)
        }
    }

    fun setData(drawingData: DrawingData) {
        this.drawingData = drawingData

        removeAllViews()
        addImageItems(drawingData)
    }

    private fun addImageItems(drawingData: DrawingData) {
        itemsImage.clear()
        itemsBorder.clear()

        drawingData.items.forEach {
            itemsImage.add(addItemToContainer(it))
        }

        drawingData.bordersData.forEach {
            itemsBorder.add(addItemToContainer(it))
        }
        updateItems()
    }

    private fun addItemToContainer(
        itemImageData: DrawingItemData,
    ): DrawingItemView {
        val drawingItem = createDrawingItem(itemImageData)
        addView(drawingItem)
        return drawingItem
    }

    private fun addItemToContainer(
        itemImageData: BorderData,
    ): DrawingBorderItemView {
        val drawingItem = createBorderItem(itemImageData)
        addView(drawingItem)
        return drawingItem
    }

    fun setDrawListener(listener: IOnDrawListener) {
        this.listener = listener
    }

    private fun createDrawingItem(itemImageData: DrawingItemData): DrawingItemView {
        return DrawingItemView(
            context = context,
            drawingItemData = itemImageData
        )
    }

    private fun createBorderItem(borderData: BorderData): DrawingBorderItemView {
        return DrawingBorderItemView(
            context = context,
            borderData = borderData
        )
    }

    private fun destroyAnimator() {
        progressAnimator?.removeAllUpdateListeners()
        progressAnimator?.removeAllListeners()
        progressAnimator?.cancel()
        progressAnimator?.end()
    }

    private fun updateItems() {
        itemsImage.forEach {
            it.recalculateSizes(drawingData.width, drawingData.height, curWidth, curHeight)
        }
        itemsBorder.forEach {
            it.recalculateSizes(drawingData.width, drawingData.height, curWidth, curHeight)
        }
    }
}

interface IOnDrawListener {
    fun onDrawFinished()
    fun onStepsUpdate(steps: Int)
}