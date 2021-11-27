package by.step.draw.ui.views

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import by.step.draw.R
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin

class RayView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val NUMBER_OF_RAYS = 10
    private val PERCENT_RAYS_WIDER = 0.25f

    private var curAnimProgress = 0f

    private lateinit var drawRect: RectF
    private var clipPath: Path = Path()

    private val paintRays: Paint = Paint()
    private var angles: Array<Float>
    private var gradients: ArrayList<Shader?>? = null
    private var curWidth: Int = 0
    private var curHeight: Int = 0

    init {
        paintRays.isAntiAlias = true
        paintRays.style = Paint.Style.FILL
        paintRays.strokeWidth = 1f

        val degreesDiffer = 360f / NUMBER_OF_RAYS.toFloat() / 2f

        angles = Array(NUMBER_OF_RAYS * 2) { 0f }

        for (i in 0 until NUMBER_OF_RAYS * 2) {
            if (i % 2 == 0) {
                angles[i] =
                    (i.toFloat() + 1f) * degreesDiffer - degreesDiffer * PERCENT_RAYS_WIDER
            } else {
                angles[i] =
                    (i.toFloat() + 1f) * degreesDiffer + degreesDiffer * PERCENT_RAYS_WIDER
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val widthMes = MeasureSpec.getSize(widthMeasureSpec)
        val heightMes = MeasureSpec.getSize(heightMeasureSpec)
        if (widthMes != curWidth || heightMes != curHeight) {
            curWidth = widthMes
            curHeight = heightMes
            val centerPoint = Point(curWidth / 2, curHeight / 2)

            val radius = hypot(curWidth.toDouble(), curHeight.toDouble()) / 2
            drawRect = RectF(
                centerPoint.x.toFloat() - radius.toFloat(),
                centerPoint.y.toFloat() - radius.toFloat(),
                centerPoint.x.toFloat() + radius.toFloat(),
                centerPoint.y.toFloat() + radius.toFloat()
            )

            val startGradientColor = ContextCompat.getColor(context, R.color.white_33f)
            val endGradientColor = ContextCompat.getColor(context, R.color.white_0df)

            gradients = arrayListOf()
            for (i in 0 until NUMBER_OF_RAYS * 2 step 2) {
                val angleInRadiance =
                    Math.toRadians(angles[i].toDouble() + (angles[i].toDouble() - angles[i + 1].toDouble()) / 2.0)
                gradients?.add(
                    LinearGradient(
                        centerPoint.x.toFloat(), centerPoint.y.toFloat(),
                        cos(angleInRadiance).toFloat() * radius.toFloat() + centerPoint.x.toFloat(),
                        sin(angleInRadiance).toFloat() * radius.toFloat() + centerPoint.y.toFloat(),
                        startGradientColor, endGradientColor,
                        Shader.TileMode.CLAMP
                    )
                )
            }
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (gradients == null) {
            return
        }

        clipPath.reset()
        clipPath.addCircle(
            drawRect.centerX(),
            drawRect.centerY(),
            drawRect.width() / 2 * curAnimProgress,
            Path.Direction.CW
        )

        canvas?.clipPath(clipPath)

        for (i in 0 until NUMBER_OF_RAYS * 2 step 2) {
            paintRays.shader = gradients!![i / 2]
            canvas?.drawArc(drawRect, angles[i], angles[i] - angles[i + 1], true, paintRays)
        }
    }

    fun setCurAnimProgress(curAnimProgress: Float) {
        this.curAnimProgress = curAnimProgress
        invalidate()
    }
}