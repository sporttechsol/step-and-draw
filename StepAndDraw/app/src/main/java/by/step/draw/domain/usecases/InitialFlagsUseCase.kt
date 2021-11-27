package by.step.draw.domain.usecases

import by.step.draw.data.repositories.InitialFlagsRepository

class InitialFlagsUseCase(private val initialFlagsRepository: InitialFlagsRepository) {

    fun setHideElementsIntroShown(isShown: Boolean) =
        initialFlagsRepository.setHideElementsIntroShown(isShown)

    fun isHideElementsIntroShown() = initialFlagsRepository.isHideElementsIntroShown()
}