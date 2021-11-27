package by.step.draw.ui.views

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import by.step.draw.R
import by.step.draw.domain.models.drawing.DrawingData
import kotlinx.android.synthetic.main.view_progress_steps.view.*

class StepsProgressView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private lateinit var drawingData: DrawingData

    private val colorGreenProgress = ContextCompat.getColor(context, R.color.green_5BC)
    private val colorTextBackgroundGreen = ContextCompat.getColor(context, R.color.green_84C)
    private val colorGreyProgressBackground = ContextCompat.getColor(context, R.color.grey_ddd)
    private var curMaxLimit = 0
    private var curSteps = 0
    private var curBackgroundSteps = 0

    init {
        inflate(context, R.layout.view_progress_steps, this)
        setBackgroundColor(colorGreenProgress)
        stepsProgress.setBackgroundTextColor(colorTextBackgroundGreen)
    }

    fun updateMainProgress(steps: Int) {
        curSteps = steps
        curMaxLimit = drawingData.maxDrawSteps
        tvSteps.text = steps.toString()

        if (steps <= drawingData.maxDrawSteps) {
            stepsProgress.setMaxValue(drawingData.maxDrawSteps)
            stepsProgressBkgr.setMaxValue(drawingData.maxDrawSteps)

            setBackgroundColor(colorGreenProgress)
            stepsProgress.setProgressDrawableColor(colorGreenProgress)
            stepsProgress.setBackgroundTextColor(colorTextBackgroundGreen)
            stepsProgressBkgr.setBackgroundDrawableColor(colorGreyProgressBackground)

            stepsProgress.setProgress(steps, false)
        }
        updateBackgroundProgress(curBackgroundSteps)
    }

    fun updateBackgroundProgress(steps: Int) {
        curBackgroundSteps = steps
        stepsProgressBkgr.setProgress(steps, false)
    }

    fun setData(drawingData: DrawingData) {
        this.drawingData = drawingData
    }
}