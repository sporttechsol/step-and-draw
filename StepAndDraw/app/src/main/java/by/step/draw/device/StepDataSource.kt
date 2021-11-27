package by.step.draw.device

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import io.reactivex.Observable
import io.reactivex.functions.Action
import io.reactivex.subjects.ReplaySubject

// TODO setup delay for event (skip)
class StepDataSource(var context: Context) {

    private var sensorManager: SensorManager? = null
    private var stepSensor: Sensor
    private var replaySubject = ReplaySubject.createWithSize<SensorEvent>(1)

    private var sensorEventListener: SensorEventListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent?) {
            replaySubject.onNext(event!!)
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

        }
    }

    private var finallyAction = Action {
        if (!replaySubject.hasObservers()) {
            replaySubject = ReplaySubject.createWithSize(1)
            sensorManager?.unregisterListener(sensorEventListener)
        }
    }

    init {
        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        stepSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)!!
    }

    fun startCountingSteps(): Observable<SensorEvent> {
        sensorManager?.registerListener(
            sensorEventListener,
            stepSensor,
            SensorManager.SENSOR_DELAY_UI
        )
        return replaySubject.doFinally(finallyAction)
    }
}