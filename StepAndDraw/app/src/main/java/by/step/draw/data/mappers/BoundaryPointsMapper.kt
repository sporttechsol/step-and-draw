package by.step.draw.data.mappers

import android.graphics.PathMeasure
import android.graphics.PointF
import by.step.draw.domain.models.drawing.item.steps.BoundaryPoints

class BoundaryPointsMapper {

    fun transform(pathMeasure: PathMeasure):BoundaryPoints{
        val pointArray: Array<PointF> = Array(pathMeasure.length.toInt()) { PointF() }
        val length = pathMeasure.length
        var distance = 0f
        val speed = 1f
        var counter = 0
        val aCoordinates = FloatArray(2)

        pathMeasure.getPosTan(distance, aCoordinates, null)
        var leftPoint = PointF(aCoordinates[0], aCoordinates[1])
        var topPoint = PointF(aCoordinates[0], aCoordinates[1])
        var rightPoint = PointF(aCoordinates[0], aCoordinates[1])
        var bottomPoint = PointF(aCoordinates[0], aCoordinates[1])

        while (distance < length && counter < length - 1) {
            pathMeasure.getPosTan(distance, aCoordinates, null)
            pointArray[counter] = PointF(
                aCoordinates[0],
                aCoordinates[1]
            )
            counter++
            distance += speed
            if (aCoordinates[0] < leftPoint.x) {
                leftPoint = PointF(aCoordinates[0], aCoordinates[1])
            }
            if (aCoordinates[0] > rightPoint.x) {
                rightPoint = PointF(aCoordinates[0], aCoordinates[1])
            }

            if (aCoordinates[1] < topPoint.y) {
                topPoint = PointF(aCoordinates[0], aCoordinates[1])
            }

            if (aCoordinates[1] > bottomPoint.y) {
                bottomPoint = PointF(aCoordinates[0], aCoordinates[1])
            }
        }
        return BoundaryPoints(leftPoint, topPoint, rightPoint, bottomPoint)
    }
}