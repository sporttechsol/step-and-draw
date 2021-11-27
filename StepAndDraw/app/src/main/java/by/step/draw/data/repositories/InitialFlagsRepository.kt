package by.step.draw.data.repositories

import by.step.draw.data.DataManagerLocal

class InitialFlagsRepository(private val dataManagerLocal: DataManagerLocal) {

    fun setIntroShown(isIntroShown: Boolean) = dataManagerLocal.setIntroShown(isIntroShown)

    fun isIntroShown() = dataManagerLocal.isIntroShown()

    fun setAfterIntroAction(isShown:Boolean) = dataManagerLocal.setAfterIntroAction(isShown)

    fun isAfterIntroAction() = dataManagerLocal.isAfterIntroAction()

    fun setAnimationIntroDialogShown(isShown: Boolean) =
        dataManagerLocal.setAnimationIntroDialogShown(isShown)

    fun isAnimationIntroDialogShown() = dataManagerLocal.isAnimationIntroDialogShown()

    fun setFinalDialogShown(isShown: Boolean) = dataManagerLocal.setFinalDialogShown(isShown)

    fun isFinalDialogShown() = dataManagerLocal.isFinalDialogShown()

    fun setHideElementsIntroShown(isShown: Boolean) =
        dataManagerLocal.setHideElementsIntroShown(isShown)

    fun isHideElementsIntroShown() = dataManagerLocal.isHideElementsIntroShown()
}