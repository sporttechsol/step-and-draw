package by.step.draw.ui.receivers

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import by.step.draw.App
import by.step.draw.data.repositories.StepCounterRepository
import by.step.draw.domain.usecases.AddStepsUseCase
import by.step.draw.ui.services.StepService

class StepsServiceBroadcastReceiver : BroadcastReceiver() {

    private val addStepsUseCase: AddStepsUseCase by lazy {
        AddStepsUseCase(StepCounterRepository(App.instance.getDataManagerLocal()))
    }

    override fun onReceive(context: Context, intent: Intent) {
        val code = intent.action

        when (code) {
            ADD_STEPS_CODE -> addStepsUseCase.add(1000).subscribe()
            CANCEL_SERVICE_CODE -> StepService.stop(App.instance)
        }
    }

    companion object {

        private val ADD_STEPS_CODE = "101"
        private val CANCEL_SERVICE_CODE = "102"

        fun addStepsPendingIntent(context: Context): PendingIntent {
            val intent = Intent(context, StepsServiceBroadcastReceiver::class.java)
            intent.action = ADD_STEPS_CODE
            return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        fun cancelServicePendingIntent(context: Context): PendingIntent {
            val intent = Intent(context, StepsServiceBroadcastReceiver::class.java)
            intent.action = CANCEL_SERVICE_CODE
            return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }
    }
}