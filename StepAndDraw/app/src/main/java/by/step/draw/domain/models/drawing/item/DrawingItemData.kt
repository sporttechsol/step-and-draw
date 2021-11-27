package by.step.draw.domain.models.drawing.item

import by.step.draw.domain.models.drawing.item.steps.DrawingStepsData

class DrawingItemData(
    val width: Float,
    val height: Float,
    val cords: Pair<Float, Float>,
    val startDrawPoint: Pair<Float, Float>,
    val drawingRadius: Float,
    val paintData: PaintItemData,
    val drawingSteps: DrawingStepsData
)