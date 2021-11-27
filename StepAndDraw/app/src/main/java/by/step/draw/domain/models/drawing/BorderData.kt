package by.step.draw.domain.models.drawing

import android.graphics.Path

class BorderData(
    val fromSteps: Int,
    val toSteps: Int,
    val pathsAndCords: List<Pair<Pair<Float, Float>, Path>>,
    val cords: Pair<Float, Float>,
    val width: Float,
    val height: Float
)