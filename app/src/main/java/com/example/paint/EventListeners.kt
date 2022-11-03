package com.example.paint

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.example.paint.databinding.ActivitySettingsBinding
import com.example.paint.mainActivity.MainActivityViewModel
import java.lang.Math.abs
import java.util.*

class LightEventListener(private val viewModel: MainActivityViewModel) :
    SensorEventListener {

    private var threshold = 20000.0
    private var prevBright: Int = 0

    override fun onSensorChanged(sensorEvent: SensorEvent) {
        val brightness = sensorEvent.values[0]
        val currDiff = kotlin.math.abs(brightness.toInt() - prevBright)
        if (brightness < threshold) {
            viewModel.switchMode(false)
        } else {
            viewModel.switchMode(true)
        }
        prevBright = currDiff
    }

    override fun onAccuracyChanged(sensor: Sensor, i: Int) {}
}

class AccelerometerEventListener(private val viewModel: MainActivityViewModel) :
    SensorEventListener {

    private var threshhold = 4

    private var acceleration = 10f
    private var currentAcceleration = SensorManager.GRAVITY_EARTH
    private var lastAcceleration = SensorManager.GRAVITY_EARTH

    override fun onSensorChanged(event: SensorEvent) {

        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]
        lastAcceleration = currentAcceleration

        currentAcceleration = kotlin.math.sqrt((x * x + y * y + z * z).toDouble()).toFloat()
        val delta: Float = currentAcceleration - lastAcceleration
        acceleration = acceleration * 0.9f + delta

        if (acceleration > threshhold) {
            viewModel.removedHistory.postValue(LinkedList())
            viewModel.drawingHistory.postValue(LinkedList())
        }
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
}


class AlignEventListener(private val binding: ActivitySettingsBinding) : SensorEventListener {

    override fun onSensorChanged(event: SensorEvent) {

        if (!binding.logger.isChecked) {
            binding.loggerMessage.text = ""
            return
        }

        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]

        if (x in -0.9f..0.9f) {
            binding.loggerMessage.text = "X Aligned "
            return
        }
        if (y in -0.9f..0.9f) {
            binding.loggerMessage.text = "Y Aligned "
            return
        }
        if (z in -0.9f..0.9f) {
            binding.loggerMessage.text = "Z Aligned "
            return
        }
        binding.loggerMessage.text = ""
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
}


