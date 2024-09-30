package com.example.fitness

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.mikhaellopez.circularprogressbar.CircularProgressBar

class RunningActivity : AppCompatActivity(), SensorEventListener {

    private var sensorManager: SensorManager? = null
    private var running = false
    private lateinit var caloriesTextView: TextView
    private lateinit var distanceTextView: TextView
    private lateinit var progressCircular: CircularProgressBar
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    private var totalSteps = 0f
    private var previousTotalSteps = 0f

    val ACTIVITY_RECOGNITION_REQUEST_CODE = 100
    private val targetSteps = 5000  // Target for completing the progress bar circle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_running)

        // Initialize SwipeRefreshLayout
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)

        // Set up the refresh listener
        swipeRefreshLayout.setOnRefreshListener {
            refreshPage()
        }

        loadData()

        // Initialize views
        caloriesTextView = findViewById(R.id.caloriesTextView)
        distanceTextView = findViewById(R.id.distanceTextView)
        progressCircular = findViewById(R.id.circularProgressBar)

        // Check for activity recognition permission
        if (!isPermissionGranted()) {
            requestPermission()
        }

        // Initialize the sensor manager
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        // Example to save progress
        saveRunningTime(0)// Replace 15 with actual running duration

        fun completeWorkout(duration: Int) {
            // Save total running time
            saveRunningTime(duration) // Call this when workout is complete
        }
//
//        fun saveRunningTime(duration: Int) {
//            val sharedPreferences = getSharedPreferences("fitness_prefs", Context.MODE_PRIVATE)
//            val currentProgress = sharedPreferences.getInt("runningTime", 0)
//            with(sharedPreferences.edit()) {
//                putInt("runningTime", currentProgress + duration) // Update with new duration
//                apply()
//            }
//        }

        val completeWorkoutButton = findViewById<Button>(R.id.completeWorkoutButton)

        completeWorkoutButton.setOnClickListener {
            val workoutDuration = calculateWorkoutDuration() // Implement this to calculate actual duration
            completeWorkout(workoutDuration) // Call your function to save the workout
            Toast.makeText(this, "Workout completed!", Toast.LENGTH_SHORT).show()
            finish() // Optional: close activity or navigate to another screen
        }

    }

    private fun calculateWorkoutDuration(): Int {
        // Implement your logic to calculate the duration of the workout
        return 15 // Placeholder for actual duration
    }

    private fun saveRunningTime(duration: Int) {
        val sharedPreferences = getSharedPreferences("fitness_prefs", Context.MODE_PRIVATE)
        val currentProgress = sharedPreferences.getInt("runningTime", 0)
        with(sharedPreferences.edit()) {
            putInt("runningTime", currentProgress + duration)
            apply()
        }
    }

    override fun onResume() {
        super.onResume()
        running = true

        // Initialize step sensor
        val stepSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        if (stepSensor == null) {
            Toast.makeText(this, "Sensor not found", Toast.LENGTH_SHORT).show()
        } else {
            sensorManager?.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI)
            Toast.makeText(this, "Sensor found ✅", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onPause() {
        super.onPause()
        running = false
        sensorManager?.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        val stepCountTextView = findViewById<TextView>(R.id.stepCountTextView)

        if (running && event != null) {
            totalSteps = event.values[0]
            val currentSteps = totalSteps.toInt() - previousTotalSteps.toInt()
            stepCountTextView.text = "$currentSteps"

            // Calculate calories burned
            val caloriesBurned = currentSteps * 0.05  // Calculate based on current steps
            caloriesTextView.text = "%.1f Kcal".format(caloriesBurned)

            // Calculate distance covered
            val distanceCovered = currentSteps * 0.0008  // Calculate based on current steps
            distanceTextView.text = "%.2f km".format(distanceCovered)

            // Update the CircularProgressBar with slower animation
            progressCircular.apply {
                // Progress percentage based on 5000 step target
                val progressPercentage = (currentSteps.toFloat() / targetSteps) * 100

                // Set progress with slower animation, but update immediately
                setProgressWithAnimation(progressPercentage, 1500)  // Slower animation time
            }
        }
    }

    private fun refreshPage() {
        // Reload data or reset views
        val stepCountTextView = findViewById<TextView>(R.id.stepCountTextView)
        previousTotalSteps = totalSteps

        // Reset text to initial state (show 0 kcal and 0 km)
        stepCountTextView.text = "0"
        distanceTextView.text = "0.00 km"
        caloriesTextView.text = "0.0 Kcal"

        // Reset progress bar animation to 0 slowly
        progressCircular.apply {
            setProgressWithAnimation(0f, 1500)  // Reset progress with slower animation
        }

        saveData()
        loadData()

        // Stop the refreshing animation
        swipeRefreshLayout.isRefreshing = false

        // Optionally, show a message to the user
        Toast.makeText(this, "Page refreshed", Toast.LENGTH_SHORT).show()
    }

    private fun saveData() {
        val sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putFloat("key1", previousTotalSteps)
        editor.apply()
    }

    private fun loadData() {
        val sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val savedNumber = sharedPreferences.getFloat("key1", 0f)
        Log.d("RunningActivity", "$savedNumber")
        previousTotalSteps = savedNumber
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not used in this app
    }

    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACTIVITY_RECOGNITION),
                ACTIVITY_RECOGNITION_REQUEST_CODE
            )
        }
    }

    private fun isPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACTIVITY_RECOGNITION
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == ACTIVITY_RECOGNITION_REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // Permission granted
            } else {
                // Permission denied
            }
        }
    }
}











//package com.example.fitness
//
//import android.Manifest
//import android.content.Context
//import android.content.pm.PackageManager
//import android.hardware.Sensor
//import android.hardware.SensorEvent
//import android.hardware.SensorEventListener
//import android.hardware.SensorManager
//import android.os.Build
//import android.os.Bundle
//import android.util.Log
//import androidx.appcompat.app.AppCompatActivity
//import android.widget.TextView
//import android.widget.Toast
//import androidx.core.app.ActivityCompat
//import androidx.core.content.ContextCompat
//import com.mikhaellopez.circularprogressbar.CircularProgressBar
//import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
//
//class RunningActivity : AppCompatActivity(), SensorEventListener {
//
//    private var sensorManager: SensorManager? = null
//    private var running = false
//    private lateinit var caloriesTextView: TextView
//    private lateinit var distanceTextView: TextView
//    private lateinit var progressCircular: CircularProgressBar
//    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
//
//    private var totalSteps = 0f
//    private var previousTotalSteps = 0f
//
//    val ACTIVITY_RECOGNITION_REQUEST_CODE = 100
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_running)
//
//
//        // Initialize SwipeRefreshLayout
//        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)
//
//        // Set up the refresh listener
//        swipeRefreshLayout.setOnRefreshListener {
//            refreshPage()
//        }
//
//        loadData()
//        resetSteps()
//
//        // Initialize views
//        caloriesTextView = findViewById(R.id.caloriesTextView)
//        distanceTextView = findViewById(R.id.distanceTextView)
//        progressCircular = findViewById(R.id.circularProgressBar)  // Initialize the CircularProgressBar
//
//        // Check for activity recognition permission
//        if (!isPermissionGranted()) {
//            requestPermission()
//        }
//
//        // Initialize the sensor manager
//        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
//    }
//
//    override fun onResume() {
//        super.onResume()
//        running = true
//
//        // Initialize step sensor
//        val stepSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
//        if (stepSensor == null) {
//            Toast.makeText(this, "Sensor not found", Toast.LENGTH_SHORT).show()
//        } else {
//            sensorManager?.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI)
//            Toast.makeText(this, "Sensor found ✅", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//    override fun onPause() {
//        super.onPause()
//        running = false
//        sensorManager?.unregisterListener(this)
//    }
//
//    override fun onSensorChanged(event: SensorEvent?) {
//        val stepCountTextView = findViewById<TextView>(R.id.stepCountTextView)
//
//        if (running && event != null) {
//            totalSteps = event.values[0]
//            val currentSteps = totalSteps.toInt() - previousTotalSteps.toInt()
//            stepCountTextView.text = "$currentSteps"
//
//            // Calculate calories burned
//            val caloriesBurned = totalSteps * 0.05
//            caloriesTextView.text = "%.1f kcal".format(caloriesBurned)
//
//            // Calculate distance covered
//            val distanceCovered = totalSteps * 0.0008
//            distanceTextView.text = "%.2f km".format(distanceCovered)
//
//            // Update the CircularProgressBar
//            progressCircular.apply {
//                setProgressWithAnimation(currentSteps.toFloat())  // Set progress to current step count
//            }
//        }
//    }
//
//    private fun resetSteps() {
//        val stepCountTextView = findViewById<TextView>(R.id.stepCountTextView)
//        stepCountTextView.setOnClickListener {
//            Toast.makeText(this, "Long tap to reset steps", Toast.LENGTH_SHORT).show()
//        }
//
//        stepCountTextView.setOnClickListener {
//            previousTotalSteps = totalSteps
//            stepCountTextView.text = 0.toString()
//            distanceTextView.text = 0.toString()
//            caloriesTextView.text = 0.toString()
//            saveData()
//
//            recreate()
//            true
//
//        }
//    }
//
//    private fun refreshPage() {
//        // Perform your refresh logic here, like reloading data or resetting views
//        loadData()
//        resetSteps()
//
//        // Stop the refreshing animation
//        swipeRefreshLayout.isRefreshing = false
//
//        // Optionally, show a message to the user
//        Toast.makeText(this, "Page refreshed", Toast.LENGTH_SHORT).show()
//    }
//}
//
//    private fun saveData() {
//        val sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
//        val editor = sharedPreferences.edit()
//        editor.putFloat("key1", previousTotalSteps)
//        editor.apply()
//    }
//
//    private fun loadData() {
//        val sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
//        val savedNumber = sharedPreferences.getFloat("key1", 0f)
//        Log.d("RunningActivity", "$savedNumber")
//        previousTotalSteps = savedNumber
//    }
//
//    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
//        // Not used in this app
//    }
//
//    private fun requestPermission() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//            ActivityCompat.requestPermissions(
//                this,
//                arrayOf(Manifest.permission.ACTIVITY_RECOGNITION),
//                ACTIVITY_RECOGNITION_REQUEST_CODE
//            )
//        }
//    }
//
//    private fun isPermissionGranted(): Boolean {
//        return ContextCompat.checkSelfPermission(
//            this,
//            Manifest.permission.ACTIVITY_RECOGNITION
//        ) == PackageManager.PERMISSION_GRANTED
//    }
//
//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == ACTIVITY_RECOGNITION_REQUEST_CODE) {
//            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
//                // Permission granted
//            } else {
//                // Permission denied
//            }
//        }
//    }
//}
