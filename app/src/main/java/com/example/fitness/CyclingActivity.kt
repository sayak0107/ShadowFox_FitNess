package com.example.fitness

import android.Manifest
import android.annotation.SuppressLint
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

class CyclingActivity : AppCompatActivity(), SensorEventListener {

    private var sensorManager: SensorManager? = null
    private var running = false
    private lateinit var caloriesTextView: TextView
    private lateinit var distanceTextView: TextView
    private lateinit var progressCircular: CircularProgressBar
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    private var totalSteps = 0f
    private var previousTotalSteps = 0f

    val ACTIVITY_RECOGNITION_REQUEST_CODE = 100
    private val targetSteps = 7000  // Target for completing the progress bar circle

    private lateinit var stepCountTextView: TextView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cycling)

        // Initialize SwipeRefreshLayout
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)

        // Set up the refresh listener
        swipeRefreshLayout.setOnRefreshListener {
            refreshPage()
        }

        loadData()

        // Initialize views
        stepCountTextView = findViewById(R.id.stepCountTextView)
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
        saveCyclingTime(0) // Replace 10 with actual cycling duration

        fun completeWorkout(duration: Int) {
            // Save total cycling time
            saveCyclingTime(duration) // Call this when workout is complete
        }

//        fun saveCyclingTime(duration: Int) {
//            val sharedPreferences = getSharedPreferences("fitness_prefs", Context.MODE_PRIVATE)
//            val currentProgress = sharedPreferences.getInt("cyclingTime", 0)
//            with(sharedPreferences.edit()) {
//                putInt("cyclingTime", currentProgress + duration) // Update with new duration
//                apply()
//            }
//        }

        // Initialize views
        val completeCyclingButton = findViewById<Button>(R.id.completeCyclingWorkoutButton)

        completeCyclingButton.setOnClickListener {
            val workoutDuration = calculateCyclingDuration() // Calculate the duration based on your logic
            completeWorkout(workoutDuration)
            Toast.makeText(this, "Cycling workout completed!", Toast.LENGTH_SHORT).show()
            finish() // End the activity if necessary
        }

    }

    private fun saveCyclingTime(duration: Int) {
        val sharedPreferences = getSharedPreferences("fitness_prefs", Context.MODE_PRIVATE)
        val currentProgress = sharedPreferences.getInt("cyclingTime", 0)
        with(sharedPreferences.edit()) {
            putInt("cyclingTime", currentProgress + duration)
            apply()
        }
    }
    private fun calculateCyclingDuration(): Int {
        // Implement your logic to calculate the duration of the cycling workout
        return 10 // Placeholder for actual duration
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
        if (running && event != null) {
            totalSteps = event.values[0]
            val currentSteps = totalSteps.toInt() - previousTotalSteps.toInt()
            stepCountTextView.text = "$currentSteps"

            // Calculate calories burned
            val caloriesBurned = currentSteps * 0.25  // Adjust based on cycling
            caloriesTextView.text = "%.1f Kcal".format(caloriesBurned)

            // Calculate distance covered
            val distanceCovered = currentSteps * 0.008  // Adjust based on cycling
            distanceTextView.text = "%.2f km".format(distanceCovered)

            // Update the CircularProgressBar
            progressCircular.apply {
                // Progress percentage based on 5000 step target
                val progressPercentage = (currentSteps.toFloat() / targetSteps) * 100
                setProgressWithAnimation(progressPercentage, 1500)  // Slower animation time
            }
        }
    }

    private fun refreshPage() {
        // Reload data or reset views
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
        Log.d("CyclingActivity", "$savedNumber")
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
//import androidx.appcompat.app.AppCompatActivity
//import android.widget.TextView
//import android.widget.Toast
//import androidx.core.app.ActivityCompat
//import androidx.core.content.ContextCompat
//import com.mikhaellopez.circularprogressbar.CircularProgressBar
//
//class CyclingActivity : AppCompatActivity(), SensorEventListener {
//
//    private lateinit var sensorManager: SensorManager
//    private var stepCount = 0
//    private var stepSensor: Sensor? = null
//    private lateinit var progressCircular: CircularProgressBar
//
//    private var totalSteps = 0f
//    private var previousTotalSteps = 0f
//
//    val ACTIVITY_RECOGNITION_REQUEST_CODE = 100
//    private lateinit var stepCountTextView: TextView
//    private lateinit var caloriesTextView: TextView
//    private lateinit var distanceTextView: TextView
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_cycling)  // Use cycling activity layout here
//
//        // Initialize the views
//        stepCountTextView = findViewById(R.id.stepCountTextView)
//        caloriesTextView = findViewById(R.id.caloriesTextView)
//        distanceTextView = findViewById(R.id.distanceTextView)
//
//        // Initialize the sensor manager and step counter sensor (use other sensors for cycling)
//        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
//        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
//
//        // Check if the device has a step counter sensor (can adapt to other sensors or GPS for cycling)
//        if (stepSensor != null) {
//            sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI)
//        } else {
//            stepCountTextView.text = "Step sensor not available!"
//        }
//    }
//
//    override fun onSensorChanged(event: SensorEvent?) {
//        if (event != null) {
//            // Update the step count
//            stepCount = event.values[0].toInt()
//            stepCountTextView.text = stepCount.toString() // Show number of steps
//
//            // Calculate calories burned (adjust the formula for cycling)
//            val caloriesBurned = stepCount * 0.25 // Vigorous Cycling (14-16 mph or ~22-25 km/h)
//            caloriesTextView.text = "%.1f kcal".format(caloriesBurned) // Show kcal format
//
//            // Calculate distance covered (adjust for cycling, or use GPS for accuracy)
//            val distanceCovered = stepCount * 0.008 // Vigorous Cycling (14-16 mph or ~22-25 km/h)
//            distanceTextView.text = "%.2f km".format(distanceCovered) // Show distance in km
//        }
//    }
//
//    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
//        // We can leave this empty for now
//        println("onAccuracyChanged: Sensor: $sensor; accuracy: $accuracy")
//    }
//
//    override fun onResume() {
//        super.onResume()
//        // Register the sensor listener again when activity resumes
//        if(stepSensor == null){
//            Toast.makeText(this, "Sensor not found", Toast.LENGTH_SHORT).show()
//        }else{
//            sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_NORMAL)
//            Toast.makeText(this, "Sensor found ✅", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//    override fun onPause() {
//        super.onPause()
//        // Unregister the sensor listener when activity pauses to save battery
//        sensorManager.unregisterListener(this)
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
//        ) != PackageManager.PERMISSION_GRANTED
//    }
//
//    //handle requested permission result(allow or deny)
//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        when (requestCode) {
//            ACTIVITY_RECOGNITION_REQUEST_CODE -> {
//                // If request is cancelled, the result arrays are empty.
//                if ((grantResults.isNotEmpty() &&
//                            grantResults[0] == PackageManager.PERMISSION_GRANTED)
//                ) {
//                    // Permission is granted. Continue the action or workflow
//                    // in your app.
//                }
//            }
//        }
//
//    }
//}
