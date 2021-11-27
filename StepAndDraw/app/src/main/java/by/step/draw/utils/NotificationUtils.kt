package by.step.draw.utils

import android.app.NotificationChannel
import android.os.Build
import androidx.annotation.RequiresApi
import by.step.draw.App

class NotificationUtils {

    @RequiresApi(api = Build.VERSION_CODES.O)
    fun createChannel(idRes: Int, nameRes: Int, importance: Int): NotificationChannel {
        val context = App.instance.applicationContext
        return createChannel(context.getString(idRes), context.getString(nameRes), importance)
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    fun createChannel(id: String, name: String, importance: Int) =
        NotificationChannel(id, name, importance)

}