package by.step.draw.domain.models.drawing.item.steps

class DrawingStepsAnimationData(
    val stepsFrom: Int,
    val stepsTo: Int,
    val colorFrom: Int,
    val colorTo: Int,
    val duration: Int,
    val boundaryPoints: BoundaryPoints
)