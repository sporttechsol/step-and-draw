package by.step.draw.data.mappers

import android.graphics.*
import androidx.core.content.ContextCompat
import by.step.draw.App
import by.step.draw.R
import by.step.draw.domain.models.drawing.BorderData
import by.step.draw.domain.models.drawing.DrawingData
import by.step.draw.domain.models.drawing.item.DrawingItemData
import by.step.draw.domain.models.drawing.item.PaintItemData
import by.step.draw.domain.models.drawing.item.steps.DrawingStepsAnimationData
import by.step.draw.domain.models.drawing.item.steps.DrawingStepsData
import com.caverock.androidsvg.SVG

// TODO maybe make amount of steps according to the size of area (skip)
class DrawingDataMapper {

    private val MAX_DRAWING_STEPS = 10000
    private val MAX_ANIMATION_STEPS = 15000

    private val drawingItemDataMapper: DrawingItemDataMapper by lazy {
        DrawingItemDataMapper()
    }

    private val boundaryPointsMapper: BoundaryPointsMapper by lazy {
        BoundaryPointsMapper()
    }

    fun transform(): DrawingData {
        val drawingItems = fetchItemsVer2("drawing_main.svg")

        return DrawingData(
            1440,
            1920,
            drawingItems.last().drawingSteps.stepsTo,
            findMaxAnimationSteps(drawingItems),
            drawingItems,
            getBorderData(drawingItems)
        )
    }

    // TODO group data in xml (skip)
    private fun getBorderData(drawingItemsData: List<DrawingItemData>): List<BorderData> {
        val bordersData = ArrayList<BorderData>()
        var curBordersItems = drawingItemsData.subList(0, 7)
        var bordersCordsWidthHeight = getBordersCordsWidthHeight(curBordersItems)
        bordersData.add(
            BorderData(
                curBordersItems[0].drawingSteps.stepsFrom,
                curBordersItems.last().drawingSteps.stepsTo,
                curBordersItems.map { Pair(it.cords, it.paintData.path) },
                bordersCordsWidthHeight.first,
                bordersCordsWidthHeight.second,
                bordersCordsWidthHeight.third
            )
        )

        curBordersItems = drawingItemsData.subList(7, 14)
        bordersCordsWidthHeight = getBordersCordsWidthHeight(curBordersItems)

        bordersData.add(
            BorderData(
                curBordersItems[0].drawingSteps.stepsFrom,
                curBordersItems.last().drawingSteps.stepsTo,
                curBordersItems.map { Pair(it.cords, it.paintData.path) },
                bordersCordsWidthHeight.first,
                bordersCordsWidthHeight.second,
                bordersCordsWidthHeight.third
            )
        )

        curBordersItems = drawingItemsData.subList(14, 23)
        bordersCordsWidthHeight = getBordersCordsWidthHeight(curBordersItems)

        bordersData.add(
            BorderData(
                curBordersItems[0].drawingSteps.stepsFrom,
                curBordersItems.last().drawingSteps.stepsTo,
                curBordersItems.map { Pair(it.cords, it.paintData.path) },
                bordersCordsWidthHeight.first,
                bordersCordsWidthHeight.second,
                bordersCordsWidthHeight.third
            )
        )

        curBordersItems = drawingItemsData.subList(23, 49)
        bordersCordsWidthHeight = getBordersCordsWidthHeight(curBordersItems)

        bordersData.add(
            BorderData(
                curBordersItems[0].drawingSteps.stepsFrom,
                curBordersItems.last().drawingSteps.stepsTo,
                curBordersItems.map { Pair(it.cords, it.paintData.path) },
                bordersCordsWidthHeight.first,
                bordersCordsWidthHeight.second,
                bordersCordsWidthHeight.third
            )
        )

        curBordersItems = drawingItemsData.subList(49, 83)
        bordersCordsWidthHeight = getBordersCordsWidthHeight(curBordersItems)

        bordersData.add(
            BorderData(
                curBordersItems[0].drawingSteps.stepsFrom,
                curBordersItems.last().drawingSteps.stepsTo,
                curBordersItems.map { Pair(it.cords, it.paintData.path) },
                bordersCordsWidthHeight.first,
                bordersCordsWidthHeight.second,
                bordersCordsWidthHeight.third
            )
        )

        curBordersItems = drawingItemsData.subList(83, 95)
        bordersCordsWidthHeight = getBordersCordsWidthHeight(curBordersItems)

        bordersData.add(
            BorderData(
                curBordersItems[0].drawingSteps.stepsFrom,
                curBordersItems.last().drawingSteps.stepsTo,
                curBordersItems.map { Pair(it.cords, it.paintData.path) },
                bordersCordsWidthHeight.first,
                bordersCordsWidthHeight.second,
                bordersCordsWidthHeight.third
            )
        )

        return bordersData
    }

    private fun getBordersCordsWidthHeight(drawingItemsData: List<DrawingItemData>): Triple<Pair<Float, Float>, Float, Float> {
        var cordX = drawingItemsData[0].cords.first
        var cordY = drawingItemsData[0].cords.second

        var width = drawingItemsData[0].cords.first + drawingItemsData[0].width
        var height = drawingItemsData[0].cords.second + drawingItemsData[0].height
        drawingItemsData.forEach {
            if (it.cords.first < cordX) {
                cordX = it.cords.first
            }
            if (it.cords.second < cordY) {
                cordY = it.cords.second
            }

            val curWidth = it.cords.first + it.width
            val curHeight = it.cords.second + it.height
            if (curWidth > width) {
                width = curWidth
            }
            if (curHeight > height) {
                height = curHeight
            }

        }
        return Triple(Pair(cordX, cordY), width - cordX, height - cordY)
    }

    private fun fetchItemsVer2(resName: String): ArrayList<DrawingItemData> {
        val drawingItems = ArrayList<DrawingItemData>()

        val pathsAndColors = getPathsAndColors(SVG.getFromAsset(App.instance.assets, resName))

        val drawingStepsDelta = MAX_DRAWING_STEPS / pathsAndColors.size
        var drawingStepsCounter = drawingStepsDelta

        pathsAndColors.forEachIndexed { index: Int, pathAndColor: Pair<Path, Int> ->
            val drawingSteps = if (index == pathsAndColors.size - 1) {
                Pair(drawingStepsCounter - drawingStepsDelta, MAX_DRAWING_STEPS)
            } else if (index == 0) {
                Pair(0, drawingStepsCounter)
            } else {
                Pair((drawingStepsCounter - drawingStepsDelta * 1.05).toInt(), drawingStepsCounter)
            }

            drawingItems.add(
                getDrawingItem(
                    pathAndColor.first, pathAndColor.second,
                    drawingSteps,
                    getDrawingStepsAnimationData(index, pathAndColor.first)
                )
            )
            drawingStepsCounter += drawingStepsDelta
        }
        return drawingItems
    }

    // TODO make this in xml (skip)
    private fun getDrawingStepsAnimationData(index: Int, path: Path): DrawingStepsAnimationData? {
        return when (index) {
            54 ->
                DrawingStepsAnimationData(
                    MAX_DRAWING_STEPS,
                    10330,
                    ContextCompat.getColor(App.instance, R.color.yellow_f1f),
                    ContextCompat.getColor(App.instance, R.color.red_ed1),
                    generateAnimationDuration(),
                    boundaryPointsMapper.transform(PathMeasure(path, false))
                )
            57 -> DrawingStepsAnimationData(
                10330,
                10660,
                ContextCompat.getColor(App.instance, R.color.red_ed1),
                ContextCompat.getColor(App.instance, R.color.yellow_f1f),
                generateAnimationDuration(),
                boundaryPointsMapper.transform(PathMeasure(path, false))
            )
            63 -> DrawingStepsAnimationData(
                10660,
                10990,
                ContextCompat.getColor(App.instance, R.color.yellow_f1f),
                ContextCompat.getColor(App.instance, R.color.red_ed1),
                generateAnimationDuration(),
                boundaryPointsMapper.transform(PathMeasure(path, false))
            )
            59 -> DrawingStepsAnimationData(
                10990,
                11320,
                ContextCompat.getColor(App.instance, R.color.yellow_f1f),
                ContextCompat.getColor(App.instance, R.color.red_ed1),
                generateAnimationDuration(),
                boundaryPointsMapper.transform(PathMeasure(path, false))
            )
            60 -> DrawingStepsAnimationData(
                11320,
                11650,
                ContextCompat.getColor(App.instance, R.color.red_ed1),
                ContextCompat.getColor(App.instance, R.color.yellow_f1f),
                generateAnimationDuration(),
                boundaryPointsMapper.transform(PathMeasure(path, false))
            )
            61 -> DrawingStepsAnimationData(
                11650,
                11980,
                ContextCompat.getColor(App.instance, R.color.red_ed1),
                ContextCompat.getColor(App.instance, R.color.yellow_f1f),
                generateAnimationDuration(),
                boundaryPointsMapper.transform(PathMeasure(path, false))
            )
            66 -> DrawingStepsAnimationData(
                11980,
                12310,
                ContextCompat.getColor(App.instance, R.color.red_ed1),
                ContextCompat.getColor(App.instance, R.color.yellow_f1f),
                generateAnimationDuration(),
                boundaryPointsMapper.transform(PathMeasure(path, false))
            )
            71 -> DrawingStepsAnimationData(
                12310,
                12640,
                ContextCompat.getColor(App.instance, R.color.yellow_f1f),
                ContextCompat.getColor(App.instance, R.color.red_ed1),
                generateAnimationDuration(),
                boundaryPointsMapper.transform(PathMeasure(path, false))
            )
            70 -> DrawingStepsAnimationData(
                12640,
                12970,
                ContextCompat.getColor(App.instance, R.color.red_ed1),
                ContextCompat.getColor(App.instance, R.color.yellow_f1f),
                generateAnimationDuration(),
                boundaryPointsMapper.transform(PathMeasure(path, false))
            )
            72 -> DrawingStepsAnimationData(
                12970,
                13300,
                ContextCompat.getColor(App.instance, R.color.red_ed1),
                ContextCompat.getColor(App.instance, R.color.yellow_f1f),
                generateAnimationDuration(),
                boundaryPointsMapper.transform(PathMeasure(path, false))
            )
            74 -> DrawingStepsAnimationData(
                13300,
                13630,
                ContextCompat.getColor(App.instance, R.color.yellow_f1f),
                ContextCompat.getColor(App.instance, R.color.red_ed1),
                generateAnimationDuration(),
                boundaryPointsMapper.transform(PathMeasure(path, false))
            )
            77 -> DrawingStepsAnimationData(
                13630,
                13960,
                ContextCompat.getColor(App.instance, R.color.red_ed1),
                ContextCompat.getColor(App.instance, R.color.yellow_f1f),
                generateAnimationDuration(),
                boundaryPointsMapper.transform(PathMeasure(path, false))
            )
            78 -> DrawingStepsAnimationData(
                13960,
                14290,
                ContextCompat.getColor(App.instance, R.color.yellow_f1f),
                ContextCompat.getColor(App.instance, R.color.red_ed1),
                generateAnimationDuration(),
                boundaryPointsMapper.transform(PathMeasure(path, false))
            )
            53 -> DrawingStepsAnimationData(
                14290,
                14620,
                ContextCompat.getColor(App.instance, R.color.red_ed1),
                ContextCompat.getColor(App.instance, R.color.yellow_f1f),
                generateAnimationDuration(),
                boundaryPointsMapper.transform(PathMeasure(path, false))
            )
            82 -> DrawingStepsAnimationData(
                14620,
                MAX_ANIMATION_STEPS,
                ContextCompat.getColor(App.instance, R.color.yellow_f1f),
                ContextCompat.getColor(App.instance, R.color.red_ed1),
                generateAnimationDuration(),
                boundaryPointsMapper.transform(PathMeasure(path, false))
            )
            else -> null
        }
    }

    private fun generateAnimationDuration(): Int {
        val max = 2500
        val min = 1500
        return ((Math.random() * (max - min)) + min).toInt()
    }

    private fun getPathsAndColors(svg: SVG): ArrayList<Pair<Path, Int>> {
        val pathsAndColors = ArrayList<Pair<Path, Int>>()

        val canvas: Canvas = object : Canvas() {
            private val mMatrix = Matrix()
            override fun getWidth(): Int {
                return svg.documentWidth.toInt() // TODO check this value
            }

            override fun getHeight(): Int {
                return svg.documentHeight.toInt() // TODO check this value
            }

            override fun drawPath(path: Path, paint: Paint) {
                val dst = Path()
                getMatrix(mMatrix)
                path.transform(mMatrix, dst)
                pathsAndColors.add(Pair(path, paint.color))
            }
        }

        val viewBox: RectF = svg.documentViewBox
        val scale: Float = Math.min(
            svg.documentViewBox.width() / viewBox.width(),
            svg.documentViewBox.height() / viewBox.height()
        )

        canvas.translate(
            (svg.documentViewBox.width() - viewBox.width() * scale) / 2.0f,
            (svg.documentViewBox.height() - viewBox.height() * scale) / 2.0f
        )
        canvas.scale(scale, scale)

        svg.renderToCanvas(canvas)

        return pathsAndColors
    }

    private fun getDrawingItem(
        path: Path,
        fillColor: Int,
        stepsDrawing: Pair<Int, Int>,
        drawingStepsAnimationData: DrawingStepsAnimationData? = null
    ): DrawingItemData {
        val rectInitial = fetchRectOfDrawingItem(path)
        val width = rectInitial.width()
        val height = rectInitial.height()

        val startDrawPoint = getStartDrawPoint(
            PathMeasure(path, false),
            Pair(rectInitial.centerX(), rectInitial.centerY())
        )

        val drawingRadius = findMaxDrawRadius(PathMeasure(path, false), startDrawPoint)

        val pathMeasure = PathMeasure(path, false)
        val cordsPath = FloatArray(pathMeasure.length.toInt())
        pathMeasure.getPosTan(pathMeasure.length, cordsPath, null)
        val matrix = Matrix()

        matrix.setTranslate(0f - rectInitial.left, 0f - rectInitial.top)

        path.transform(matrix)

        val startDrawPointLocalCords = getStartDrawPoint(
            PathMeasure(path, false),
            Pair(fetchRectOfDrawingItem(path).centerX(), fetchRectOfDrawingItem(path).centerY())
        )

        return drawingItemDataMapper.transform(
            width, height,
            Pair(rectInitial.left, rectInitial.top),
            startDrawPointLocalCords,
            Math.ceil(drawingRadius.toDouble()).toFloat(),
            PaintItemData(path, fillColor),
            DrawingStepsData(stepsDrawing.first, stepsDrawing.second),
            drawingStepsAnimationData
        )
    }

    private fun findMaxDrawRadius(
        pathMeasure: PathMeasure,
        centerPoint: Pair<Float, Float>
    ): Float {
        val pointArray: Array<PointF> = Array(pathMeasure.length.toInt()) { PointF() }
        val length = pathMeasure.length
        var distance = 0f
        val speed = 1f
        var counter = 0
        val aCoordinates = FloatArray(2)

        pathMeasure.getPosTan(distance, aCoordinates, null)
        var curFarthestDistance = 0f

        while (distance < length && counter < length - 1) {
            pathMeasure.getPosTan(distance, aCoordinates, null)
            pointArray[counter] = PointF(
                aCoordinates[0],
                aCoordinates[1]
            )
            val distanceBetweenPoints =
                distanceBetweenPoints(centerPoint, Pair(aCoordinates[0], aCoordinates[1])).toFloat()
            counter++
            distance += speed
            if (curFarthestDistance < distanceBetweenPoints) {
                curFarthestDistance = distanceBetweenPoints
            }
        }
        return curFarthestDistance
    }

    private fun getStartDrawPoint(
        pathMeasure: PathMeasure,
        center: Pair<Float, Float>
    ): Pair<Float, Float> {
        val pointArray: Array<PointF> = Array(pathMeasure.length.toInt()) { PointF() }
        val length = pathMeasure.length
        var distance = 0f
        val speed = 1f
        var counter = 0
        val aCoordinates = FloatArray(2)

        pathMeasure.getPosTan(distance, aCoordinates, null)
        var startDrawPoint = Pair(0f, 0f)
        var curDistanceBetweenPoints = Double.MAX_VALUE

        while (distance < length && counter < length - 1) {
            pathMeasure.getPosTan(distance, aCoordinates, null)
            pointArray[counter] = PointF(
                aCoordinates[0],
                aCoordinates[1]
            )
            counter++
            distance += speed

            val distanceBetweenPoints =
                distanceBetweenPoints(center, Pair(aCoordinates[0], aCoordinates[1]))

            if (distanceBetweenPoints < curDistanceBetweenPoints) {
                curDistanceBetweenPoints = distanceBetweenPoints
                startDrawPoint = Pair(aCoordinates[0], aCoordinates[1])
            }
        }
        return startDrawPoint
    }

    private fun distanceBetweenPoints(p1: Pair<Float, Float>, p2: Pair<Float, Float>) = Math.sqrt(
        Math.pow((p1.first - p2.first).toDouble(), 2.0) +
                Math.pow((p1.second - p2.second).toDouble(), 2.0)
    )

    private fun fetchRectOfDrawingItem(path: Path): RectF {
        val rectF = RectF()
        path.computeBounds(rectF, true)
        return rectF
    }
}

private fun findMaxAnimationSteps(items: ArrayList<DrawingItemData>) =
    items.maxOf {
        if (it.drawingAnimatedSteps != null) {
            return@maxOf it.drawingAnimatedSteps.stepsTo
        } else {
            return@maxOf it.drawingSteps.stepsTo
        }
    }