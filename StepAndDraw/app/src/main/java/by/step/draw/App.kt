package by.step.draw

import android.app.Application
import by.step.draw.data.DataManagerLocal
import by.step.draw.data.repositories.UserIdRepository
import by.step.draw.utils.AnalyticsSender

class App : Application() {

    private lateinit var dataManagerLocal: DataManagerLocal

    companion object {
        lateinit var instance: App
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        dataManagerLocal = DataManagerLocal(this)
        AnalyticsSender.getInstance(UserIdRepository(dataManagerLocal).getUserId())
    }

    fun getDataManagerLocal() = dataManagerLocal
}