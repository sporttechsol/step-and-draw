package by.step.draw.domain.usecases

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import by.step.draw.App

class IsStepCounterAvailableUseCase {
    fun isAvailable(): Boolean {
        val sensorManager =
            App.instance.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        return sensor != null
    }
}