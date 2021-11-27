package by.step.draw.data.mappers

import by.step.draw.domain.models.drawing.item.DrawingItemData
import by.step.draw.domain.models.drawing.item.PaintItemData
import by.step.draw.domain.models.drawing.item.steps.DrawingStepsAnimationData
import by.step.draw.domain.models.drawing.item.steps.DrawingStepsData

class DrawingItemDataMapper {
    fun transform(
        width: Float,
        height: Float,
        cords: Pair<Float, Float>,
        startDrawPoint: Pair<Float, Float>,
        drawingRadius: Float,
        paintData: PaintItemData,
        drawingStepsData: DrawingStepsData,
        drawingStepsAnimationData: DrawingStepsAnimationData?
    ) = DrawingItemData(
        width,
        height,
        cords,
        startDrawPoint,
        drawingRadius,
        paintData,
        drawingStepsData,
        drawingStepsAnimationData
    )
}