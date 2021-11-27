package by.step.draw.data.repositories

import by.step.draw.data.DataManagerLocal
import java.util.*

class UserIdRepository(private val dataManagerLocal: DataManagerLocal) {

    fun getUserId(): String {
        val userId = dataManagerLocal.getUserId()
        return if (userId.isNullOrEmpty()) {
            val newUserId = UUID.randomUUID().toString()
            dataManagerLocal.setUserId(newUserId)
            newUserId
        } else {
            userId
        }
    }
}