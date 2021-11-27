package by.step.draw.domain.usecases

import by.step.draw.data.repositories.InitialFlagsRepository

class InitialFlagsUseCase(private val initialFlagsRepository: InitialFlagsRepository) {

    fun isIntroShown() = initialFlagsRepository.isIntroShown()

    fun setIntroShown(isIntroShown: Boolean) = initialFlagsRepository.setIntroShown(isIntroShown)

    fun setAfterIntroAction(isShown: Boolean) = initialFlagsRepository.setAfterIntroAction(isShown)

    fun isAfterIntroAction() = initialFlagsRepository.isAfterIntroAction()

    fun setAnimationIntroDialogShown(isShown: Boolean) =
        initialFlagsRepository.setAnimationIntroDialogShown(isShown)

    fun isAnimationIntroDialogShown() = initialFlagsRepository.isAnimationIntroDialogShown()

    fun setFinalDialogShown(isShown: Boolean) = initialFlagsRepository.setFinalDialogShown(isShown)

    fun isFinalDialogShown() = initialFlagsRepository.isFinalDialogShown()

    fun setHideElementsIntroShown(isShown: Boolean) =
        initialFlagsRepository.setHideElementsIntroShown(isShown)

    fun isHideElementsIntroShown() = initialFlagsRepository.isHideElementsIntroShown()

    fun resetFlags() {
        initialFlagsRepository.setIntroShown(false)
    }
}