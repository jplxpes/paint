package com.example.paint.mainActivity


import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.example.paint.*
import com.example.paint.databinding.ActivityMainBinding
import com.example.paint.fragments.DrawingBoard
import com.example.paint.fragments.HistoryFragment
import com.example.paint.settingsActivity.SettingsActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.time.Instant
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {

    private lateinit var sensorManager: SensorManager

    private var lightListener: SensorEventListener? = null
    private var accelerationListener: SensorEventListener? = null

    private val viewModel: MainActivityViewModel by viewModels()

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        lightListener = LightEventListener(viewModel)
        accelerationListener = AccelerometerEventListener(viewModel)

        viewModel.currentColor.observe(this) {
            binding.root.setBackgroundColor(it)
        }


        binding.bottomNavigationView?.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.paintButton -> {
                    this.supportFragmentManager.commit {
                        replace<DrawingBoard>(R.id.fragment)
                        setReorderingAllowed(true)
                    }
                    true
                }

                R.id.mapButton -> {
                    this.supportFragmentManager.commit {
                        replace<com.example.paint.fragments.MapFragment>(R.id.fragment)
                        setReorderingAllowed(true)
                    }
                    true
                }
                R.id.onlineButton -> {
                    this.supportFragmentManager.commit {
                        replace<com.example.paint.fragments.OnlineFragment>(R.id.fragment)
                        setReorderingAllowed(true)
                    }
                    true
                }
                R.id.history -> {
                    viewModel.clear()
                    this.supportFragmentManager.commit {
                        replace<HistoryFragment>(R.id.fragment)
                        setReorderingAllowed(true)
                    }
                    true
                }
                else -> false
            }
        }

    }

    override fun onResume() {
        super.onResume()
        registerListeners()
        val request = intent.getIntExtra(COLOR_TAG, defaultColor)
        viewModel.currentColor.postValue(request)
    }

    private fun registerListeners() {
        sensorManager.registerListener(
            lightListener,
            sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT),
            SensorManager.SENSOR_DELAY_NORMAL
        )
        sensorManager.registerListener(
            accelerationListener,
            sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_NORMAL
        )
    }

    override fun onPause() {
        sensorManager.unregisterListener(lightListener)
        sensorManager.unregisterListener(accelerationListener)
        super.onPause()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbarmenu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_favorite -> {
                val intent = Intent(this, SettingsActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP

                intent.putExtra(
                    COLOR_TAG,
                    if (viewModel.currentColor.value != defaultColor) viewModel.currentColor.value else blackColor
                )
                startActivity(intent)
                return true
            }
            R.id.save_info -> {
                val frag =
                    this.supportFragmentManager.findFragmentById(R.id.fragment) is DrawingBoard
                if (frag) {
                    val curr: ArrayList<DataWrapper>? = viewModel.drawingDTO.value

                    if (curr != null) {
                        val obs = "${(System.currentTimeMillis() / 1000)}"
                            Firebase.firestore.collection("History")
                            .document(obs).set(DataWrapperDTO(obs, curr))

                    }
                }
            }
        }
        return true
    }

}