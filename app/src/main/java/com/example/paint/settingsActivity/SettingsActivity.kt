package com.example.paint.settingsActivity

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.SeekBar
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.paint.AlignEventListener
import com.example.paint.COLOR_TAG
import com.example.paint.ColorDTO
import com.example.paint.mainActivity.MainActivity
import com.example.paint.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var sensorManager: SensorManager
    private var alignListener: SensorEventListener? = null

    private val viewModel: SettingsViewModel by viewModels()

    private val binding by lazy {
        ActivitySettingsBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        alignListener = AlignEventListener(binding)

        binding.logger.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                sensorManager.registerListener(
                    alignListener,
                    sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                    SensorManager.SENSOR_DELAY_UI
                )
            } else {
                sensorManager.unregisterListener(alignListener)
            }
        }

        startSeeKBar(binding.b, "blue")
        startSeeKBar(binding.g, "green")
        startSeeKBar(binding.r, "red")

        viewModel.currentColor.observe(this) {
            binding.colorView.setBackgroundColor(
                Color.rgb(
                    it.red!!,
                    it.green!!,
                    it.blue!!
                )
            )
            binding.r.progress = it.red
            binding.g.progress = it.green
            binding.b.progress = it.blue
        }

        if (binding.logger.isChecked) {
            sensorManager.registerListener(
                alignListener,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_UI
            )
        }
        binding.save.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra(
                COLOR_TAG, Color.rgb(
                    viewModel.currentColor.value?.red ?: 0,
                    viewModel.currentColor.value?.green ?: 0,
                    viewModel.currentColor.value?.blue ?: 0,
                )
            )
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP

            startActivity(intent)
        }

        binding.clear.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }


    }

    override fun onResume() {
        super.onResume()
        if (alignListener != null && binding.logger.isChecked)
            sensorManager.registerListener(
                alignListener,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_UI
            )
        val rgb: Int = intent.getIntExtra(COLOR_TAG, Color.BLACK)
        viewModel.currentColor.postValue(
            ColorDTO(
                blue = rgb and 0xFF,
                green = (rgb shr 8) and 0xFF,
                red = (rgb shr 16) and 0xFF
            )
        )
    }


    private fun startSeeKBar(seekBar: SeekBar, colorId: String) {
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(
                seek: SeekBar, progress: Int, fromUser: Boolean
            ) {
                when (colorId) {
                    "red" -> {
                        viewModel.setColor(ColorDTO(red = progress))
                        return
                    }
                    "green" -> {
                        viewModel.setColor(ColorDTO(green = progress))
                        return
                    }
                    "blue" -> {
                        viewModel.setColor(ColorDTO(blue = progress))
                        return
                    }
                }
            }

            override fun onStartTrackingTouch(seek: SeekBar) {}

            override fun onStopTrackingTouch(seek: SeekBar) {}
        })
    }

    override fun onPause() {
        super.onPause()
        if (alignListener != null && binding.logger.isChecked)
            sensorManager.unregisterListener(alignListener)
    }
}