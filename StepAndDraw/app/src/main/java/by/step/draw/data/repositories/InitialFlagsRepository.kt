package by.step.draw.data.repositories

import by.step.draw.data.DataManagerLocal

class InitialFlagsRepository(private val dataManagerLocal: DataManagerLocal) {

    fun setHideElementsIntroShown(isShown: Boolean) =
        dataManagerLocal.setHideElementsIntroShown(isShown)

    fun isHideElementsIntroShown() = dataManagerLocal.isHideElementsIntroShown()
}