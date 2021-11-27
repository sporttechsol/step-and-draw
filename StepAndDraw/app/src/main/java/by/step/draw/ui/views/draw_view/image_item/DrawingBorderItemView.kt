package by.step.draw.ui.views.draw_view.image_item

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import by.step.draw.R
import by.step.draw.domain.models.drawing.BorderData

class DrawingBorderItemView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    val borderData: BorderData
) : View(
    context,
    attrs,
    defStyleAttr
) {

    private val FADE_ANIM_DURATION =
        resources.getInteger(R.integer.drawing_border_item_alpha_duration).toLong()
    private var pathAndCordsItems: List<Pair<Pair<Float, Float>, Path>>

    private var paintItemStroke: Paint
    private var drawPaths: ArrayList<Path>? = null

    private var matrixForPath = Matrix()
    private var deltaDrawingSteps: Int = 0
    private var isDraw: Boolean = false
    private var borderOffset: Float = 0f

    private var isShowBorderAnimationShowed = false
    private var isHideBorderAnimationShowed = false

    init {
        this.deltaDrawingSteps =
            borderData.toSteps - borderData.fromSteps

        pathAndCordsItems = borderData.pathsAndCords

        paintItemStroke = Paint(Paint.ANTI_ALIAS_FLAG)
        paintItemStroke.style = Paint.Style.STROKE
        paintItemStroke.color = Color.BLACK
        paintItemStroke.strokeWidth =
            resources.getDimensionPixelSize(R.dimen.stroke_item_width).toFloat()

        borderOffset = resources.getDimensionPixelSize(R.dimen.border_offset).toFloat()
    }

    override fun onDraw(canvas: Canvas?) {
        drawItem(canvas)
        super.onDraw(canvas)
    }

    fun update(steps: Float, isInit: Boolean) {
        isDraw = steps >= borderData.fromSteps && steps < borderData.toSteps
        if (isDraw) {
            if (isInit) {
                if (steps == 0f) {
                    startFadeInAnim()
                }
                isShowBorderAnimationShowed = true
            } else {
                startFadeInAnim()
            }
        } else {
            if (isShowBorderAnimationShowed) {
                startFadeOutAnim()
            }
        }
        invalidate()
    }

    private fun drawItem(canvas: Canvas?) {
        if ((drawPaths == null || !isDraw) && !isHideBorderAnimationShowed) {
            return
        }
        drawPaths?.forEach {
            canvas?.drawPath(it, paintItemStroke)
        }
    }


    fun recalculateSizes(
        widthSource: Int,
        heightSource: Int,
        parentWidth: Int,
        parentHeight: Int
    ) {
        val ratio = heightSource.toFloat() / widthSource.toFloat()
        val itemOriginalWidth = borderData.width
        val itemOriginalHeight = borderData.height
        val itemCords = borderData.cords

        val resizedWidth = itemOriginalWidth * parentWidth / widthSource + borderOffset * 2f
        val resizedHeight =
            (itemOriginalHeight * parentWidth.toFloat() / heightSource.toFloat() * ratio) + borderOffset * 2f

        val ratioHeightSource = (parentWidth.toFloat() * ratio)
        val centerVerticalShift = (parentHeight.toFloat() - ratioHeightSource) / 2f

        this.layoutParams = FrameLayout.LayoutParams(resizedWidth.toInt(), resizedHeight.toInt())

        x = (itemCords.first * parentWidth.toFloat() / widthSource.toFloat()) - borderOffset
        y =
            itemCords.second * parentWidth.toFloat() / heightSource.toFloat() * ratio + centerVerticalShift - borderOffset

        if (drawPaths == null) {
            drawPaths = arrayListOf()
            pathAndCordsItems.forEach {
                drawPaths!!.add(Path())
            }
        }

        pathAndCordsItems.forEachIndexed { index: Int, pathAndCord: Pair<Pair<Float, Float>, Path> ->
            matrixForPath = Matrix()

            matrixForPath.setScale(
                (resizedWidth - borderOffset * 2) / borderData.width,
                (resizedHeight - borderOffset * 2) / borderData.height
            )

            val pathGlobalX =
                (pathAndCord.first.first * parentWidth.toFloat() / widthSource.toFloat())
            val pathLocalX = pathGlobalX - x

            val pathGlobalY =
                pathAndCord.first.second * parentWidth.toFloat() / heightSource.toFloat() * ratio + centerVerticalShift
            val pathLocalY = pathGlobalY - y

            pathAndCord.second.transform(matrixForPath, drawPaths!![index])

            matrixForPath.setTranslate(pathLocalX, pathLocalY)
            drawPaths!![index].transform(matrixForPath)
        }
    }

    private fun startFadeInAnim() {
        if (!isShowBorderAnimationShowed) {
            alpha = 0f
            val viewPropertyAnimator = animate()
                .alpha(1f)
                .setDuration(FADE_ANIM_DURATION)
            viewPropertyAnimator.start()
            isShowBorderAnimationShowed = true
        }
    }

    private fun startFadeOutAnim() {
        if (!isHideBorderAnimationShowed) {
            alpha = 1f
            val viewPropertyAnimator = animate()
                .alpha(0f)
                .setDuration(FADE_ANIM_DURATION)
            viewPropertyAnimator.start()
            isHideBorderAnimationShowed = true
        }
    }
}