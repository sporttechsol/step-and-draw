package by.step.draw.ui.services

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleService
import by.step.draw.App
import by.step.draw.R
import by.step.draw.data.models.step_counter_state.StepCounterServiceRunningState
import by.step.draw.data.models.step_counter_state.StepCounterServiceStoppedState
import by.step.draw.ui.activities.main.MainActivity
import by.step.draw.ui.receivers.StepsServiceBroadcastReceiver
import by.step.draw.utils.AnalyticsSender
import by.step.draw.utils.NotificationId
import by.step.draw.utils.NotificationUtils

class StepService : LifecycleService(), NotificationId {

    private val notificationManager: NotificationManager? by lazy {
        App.instance.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
    }

    private val notificationBuilder: NotificationCompat.Builder by lazy {
        NotificationCompat.Builder(
            applicationContext,
            applicationContext.getString(R.string.notification_channel_pedometer_id)
        )
    }

    private lateinit var viewModel: StepServiceViewModel

    override fun onCreate() {
        super.onCreate()
        startForeground(
            ID_COUNTING_NOTIFICATION,
            createNotification(App.instance.getString(R.string.loading))
        )
        viewModel = StepServiceViewModel()

        viewModel.textNotificationLiveData.observe(this, { text: String ->
            updateNotification(text)
        })

        viewModel.init()
        viewModel.sendServiceStatus(StepCounterServiceRunningState())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        intent?.let {
            AnalyticsSender.getInstance().stepServiceStarted()
        } ?: run {
            AnalyticsSender.getInstance().stepServiceRestarted()
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        super.onBind(intent)
        return null
    }

    override fun onDestroy() {
        AnalyticsSender.getInstance().serviceStopped()
        viewModel.sendServiceStatus(StepCounterServiceStoppedState())
        viewModel.onCleared()
        super.onDestroy()
    }

    private fun createNotification(message: String): Notification {
        createNotificationChannel()

        return notificationBuilder
            .setSmallIcon(R.drawable.ic_step)
            .setContentTitle(App.instance.getString(R.string.app_name))
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message).setSummaryText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSound(null)
            .setVibrate(null)
            .setContentIntent(createPendingIntent())
            .addAction(
                R.drawable.ic_step,
                "ADD STEPS",
                StepsServiceBroadcastReceiver.addStepsPendingIntent(this)
            )
            .addAction(
                R.drawable.ic_cancel,
                App.instance.getString(R.string.close),
                StepsServiceBroadcastReceiver.cancelServicePendingIntent(this)
            )
            .build()
    }

    private fun createPendingIntent(): PendingIntent {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)

        return PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT)
    }

    private fun updateNotification(message: String) {
        notificationBuilder
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
        notificationManager?.notify(ID_COUNTING_NOTIFICATION, notificationBuilder.build())
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationUtils().createChannel(
                R.string.notification_channel_pedometer_id,
                R.string.notification_channel_pedometer_name,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.enableVibration(false)
            channel.setSound(null, null)
            notificationManager?.createNotificationChannel(channel)
        }
    }

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, StepService::class.java)

            ContextCompat.startForegroundService(App.instance, intent)
        }

        fun stop(context: Context) {
            val intent = Intent(context, StepService::class.java)
            context.stopService(intent)
        }
    }
}