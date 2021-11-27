package by.step.draw.data.repositories

import by.step.draw.data.DataManagerLocal
import by.step.draw.data.mappers.DrawingDataMapper

class DrawingRepository(private val dataManagerLocal: DataManagerLocal) {

    private val drawingDataMapper: DrawingDataMapper by lazy {
        DrawingDataMapper()
    }

    fun getDrawingData() = drawingDataMapper.transform()
}