package by.step.draw.domain.models.drawing

import by.step.draw.domain.models.drawing.item.DrawingItemData

class DrawingData(
    val width: Int,
    val height: Int,
    val maxDrawSteps: Int,
    val maxAnimationSteps: Int,
    val items: List<DrawingItemData>,
    val bordersData: List<BorderData>
)