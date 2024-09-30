package com.example.fitness

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.example.fitness.databinding.ActivityYogaBinding

@Suppress("DEPRECATION")
class YogaActivity : AppCompatActivity() {

    private lateinit var lottie2: LottieAnimationView
    private lateinit var eventname: TextView
    private lateinit var binding: ActivityYogaBinding
    private lateinit var handler: Handler
    private var isRunning = true // Flag to control the loop
    private var timers: List<CountDownTimer> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityYogaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        eventname = findViewById(R.id.eventname)
        lottie2 = findViewById(R.id.lottie2)

        // Initialize views
        handler = Handler(Looper.getMainLooper())

        // Set Lottie animation
        binding.lottie2.animate().translationZ(500f).setStartDelay(0)

        // Set timers with start buttons
        setupTimers()

        // Start the continuous breathing exercise
        startContinuousBreathingExercise()

        fun completeWorkout(duration: Int) {
            // Save total yoga session time
            saveYogaSessionTime(duration) // Call this when the yoga session is complete
        }
        val completeYogaButton = findViewById<Button>(R.id.completeYogaButton)

        completeYogaButton.setOnClickListener {
            val yogaDuration = calculateYogaDuration() // Implement this to calculate actual duration
            completeWorkout(yogaDuration) // Call your function to save the yoga session
            Toast.makeText(this, "Yoga session completed!", Toast.LENGTH_SHORT).show()
            finish() // Optional: close activity or navigate to another screen
        }

    }

    private fun saveYogaSessionTime(duration: Int) {
        // Logic to save the yoga session duration, e.g., to a database or shared preferences
        // Example using SharedPreferences
        val sharedPreferences = getSharedPreferences("YogaPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt("lastYogaSessionDuration", duration)
        editor.apply()
    }
    private fun calculateYogaDuration(): Int {
        // Implement your logic to calculate the duration of the cycling workout
        return 10 // Placeholder for actual duration
    }
    private fun setupTimers() {
        val timerDurations = listOf(5 * 60 * 1000L, 1 * 60 * 1000L, 1 * 60 * 1000L, 1 * 60 * 1000L, 1 * 60 * 1000L, 1 * 60 * 1000L, 1 * 60 * 1000L, 1 * 60 * 1000L, 1 * 60 * 1000L)
        val timerTextViews = listOf(binding.timerText1, binding.timerText2, binding.timerText3, binding.timerText4, binding.timerText5, binding.timerText6, binding.timerText7, binding.timerText8, binding.timerText9)

        for (i in timerDurations.indices) {
            val totalTime = timerDurations[i]
            val timerTextView = timerTextViews[i]

            val timer = object : CountDownTimer(totalTime, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    val minutes = (millisUntilFinished / 1000) / 60
                    val seconds = (millisUntilFinished / 1000) % 60
                    timerTextView.text = String.format("%02d:%02d", minutes, seconds)
                }

                override fun onFinish() {
                    timerTextView.text = "00:00"
                    Toast.makeText(applicationContext, "Time's up!", Toast.LENGTH_SHORT).show()
                }
            }

            timers += timer

            // Assuming you have separate start buttons for each timer
            val startButton = when (i) {
                0 -> binding.startButton1
                1 -> binding.startButton2
                2 -> binding.startButton3
                3 -> binding.startButton4
                4 -> binding.startButton5
                5 -> binding.startButton6
                6 -> binding.startButton7
                7 -> binding.startButton8
                8 -> binding.startButton9
                else -> null
            }

            startButton?.setOnClickListener {
                timer.start()
                Toast.makeText(this, "Timer Started", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun startContinuousBreathingExercise() {
        // Repeat the breathing exercise until activity is stopped
        handler.post(object : Runnable {
            override fun run() {
                if (isRunning) {
                    startBreathingCycle()
                    handler.postDelayed(this, 15000)  // Repeat every 15 seconds
                }
            }
        })
    }

    private fun startBreathingCycle() {
        // 1. Inhale phase
        handler.postDelayed({
            triggerBeepOrVibration()
            eventname.text = "Inhale"
        }, 0)

        // 2. Hold phase
        handler.postDelayed({
            triggerBeepOrVibration()
            eventname.text = "Hold Breath"
        }, 4000)  // After 3 seconds

        // 3. Exhale phase
        handler.postDelayed({
            triggerBeepOrVibration()
            eventname.text = "Exhale"
        }, 7000)  // After 5 seconds
    }

    private fun triggerBeepOrVibration() {
        // Beep sound using ToneGenerator
        val toneGen = ToneGenerator(AudioManager.STREAM_MUSIC, 100)
        toneGen.startTone(ToneGenerator.TONE_PROP_BEEP, 200)  // 200 ms beep

        // Vibration
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            vibrator.vibrate(200)  // Vibrate for 200 ms
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Stop the continuous breathing exercise when the activity is destroyed
        isRunning = false
        timers.forEach { it.cancel() }  // Cancel all timers
    }
}




//finaluptodate











//package com.example.fitness
//
//import android.content.Context
//import android.media.AudioManager
//import android.media.ToneGenerator
//import android.os.Build
//import android.os.Bundle
//import android.os.CountDownTimer
//import android.os.Handler
//import android.os.Looper
//import android.os.VibrationEffect
//import android.os.Vibrator
//import android.widget.TextView
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import com.airbnb.lottie.LottieAnimationView
//import com.example.fitness.databinding.ActivityYogaBinding
//
//class YogaActivity : AppCompatActivity() {
//
//    private lateinit var lottie2: LottieAnimationView
//    private lateinit var eventname: TextView
//    private lateinit var binding: ActivityYogaBinding
//    private lateinit var handler: Handler
//    private var isRunning = true // Flag to control the loop
//    private var timer : CountDownTimer? = null
//
//
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityYogaBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        eventname = findViewById(R.id.eventname)
//        lottie2 = findViewById(R.id.lottie2)
//
//        // Initialize views
//        handler = Handler(Looper.getMainLooper())
//
//        // Set Lottie animation
//        binding.lottie2.animate().translationZ(500f).setStartDelay(0)
//
//        // Set minutes timer with a start button
//        setupTimer1()
//        setupTimer2()
//        setupTimer3()
//        setupTimer4()
//        setupTimer5()
//        setupTimer6()
//        setupTimer7()
//        setupTimer8()
//
//        // Start the continuous breathing exercise
//        startContinuousBreathingExercise()
//    }
//
//        private fun startContinuousBreathingExercise() {
//        // Repeat the breathing exercise until activity is stopped
//        handler.post(object : Runnable {
//            override fun run() {
//                if (isRunning) {
//                    startBreathingCycle()
//                    handler.postDelayed(this, 15000)  // Repeat every 15 seconds (adjust based on your timing)
//                }
//            }
//        })
//    }
//
//    private fun startBreathingCycle() {
//        // 1. Inhale phase (Beep/Vibrate when inhaling starts)
//        handler.postDelayed({
//            triggerBeepOrVibration()
//            eventname.text = "Inhale"
//        }, 0)  // Starts immediately
//
//        // 2. Hold phase (Beep/Vibrate when holding breath starts)
//        handler.postDelayed({
//            triggerBeepOrVibration()
//            eventname.text = "Hold Breath"
//        }, 3000)  // After 5 seconds (adjust based on your animation)
//
//        // 3. Exhale phase (Beep/Vibrate when exhaling starts)
//        handler.postDelayed({
//            triggerBeepOrVibration()
//            eventname.text = "Exhale"
//        }, 6000)  // After 10 seconds (adjust based on your animation)
//    }
//
//    // Method to trigger a beep sound or vibration
//    private fun triggerBeepOrVibration() {
//        // Beep sound using ToneGenerator
//        val toneGen = ToneGenerator(AudioManager.STREAM_MUSIC, 100)
//        toneGen.startTone(ToneGenerator.TONE_PROP_BEEP, 200)  // 200 ms beep
//
//        // Vibration
//        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
//        } else {
//            vibrator.vibrate(200)  // Vibrate for 200 ms
//        }
//    }
//
//
//
//
//
//    private fun setupTimer1() {
//        val totalTime = 5 * 60 * 1000L // 5 minutes in milliseconds
//        timer = object : CountDownTimer(totalTime, 1000) {
//            override fun onTick(millisUntilFinished: Long) {
//                val minutes = (millisUntilFinished / 1000) / 60
//                val seconds = (millisUntilFinished / 1000) % 60
//                binding.timerText1.text = String.format("%02d:%02d", minutes, seconds)
//            }
//
//            override fun onFinish() {
//                binding.timerText1.text = "00:00"
//                Toast.makeText(applicationContext, "Time's up!", Toast.LENGTH_SHORT).show()
//            }
//        }
//
//        // Start button click listener
//        binding.startButton.setOnClickListener {
//            timer?.start()
//            Toast.makeText(this, "Timer Started", Toast.LENGTH_SHORT).show()
//
//        }
//    }
//
//    private fun setupTimer2() {
//        val totalTime = 1 * 60 * 1000L // 5 minutes in milliseconds
//        timer = object : CountDownTimer(totalTime, 1000) {
//            override fun onTick(millisUntilFinished: Long) {
//                val minutes = (millisUntilFinished / 1000) / 60
//                val seconds = (millisUntilFinished / 1000) % 60
//                binding.timerText2.text = String.format("%02d:%02d", minutes, seconds)
//            }
//
//            override fun onFinish() {
//                binding.timerText2.text = "00:00"
//                Toast.makeText(applicationContext, "Time's up!", Toast.LENGTH_SHORT).show()
//            }
//        }
//
//        // Start button click listener
//        binding.startButton.setOnClickListener {
//            timer?.start()
//            Toast.makeText(this, "Timer Started", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//
//    private fun setupTimer3() {
//        val totalTime = 1 * 60 * 1000L // 5 minutes in milliseconds
//        timer = object : CountDownTimer(totalTime, 1000) {
//            override fun onTick(millisUntilFinished: Long) {
//                val minutes = (millisUntilFinished / 1000) / 60
//                val seconds = (millisUntilFinished / 1000) % 60
//                binding.timerText3.text = String.format("%02d:%02d", minutes, seconds)
//            }
//
//            override fun onFinish() {
//                binding.timerText3.text = "00:00"
//                Toast.makeText(applicationContext, "Time's up!", Toast.LENGTH_SHORT).show()
//            }
//        }
//
//        // Start button click listener
//        binding.startButton.setOnClickListener {
//            timer?.start()
//            Toast.makeText(this, "Timer Started", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//    private fun setupTimer4() {
//        val totalTime = 1 * 60 * 1000L // 5 minutes in milliseconds
//        timer = object : CountDownTimer(totalTime, 1000) {
//            override fun onTick(millisUntilFinished: Long) {
//                val minutes = (millisUntilFinished / 1000) / 60
//                val seconds = (millisUntilFinished / 1000) % 60
//                binding.timerText4.text = String.format("%02d:%02d", minutes, seconds)
//            }
//
//            override fun onFinish() {
//                binding.timerText4.text = "00:00"
//                Toast.makeText(applicationContext, "Time's up!", Toast.LENGTH_SHORT).show()
//            }
//        }
//
//        // Start button click listener
//        binding.startButton.setOnClickListener {
//            timer?.start()
//            Toast.makeText(this, "Timer Started", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//    private fun setupTimer5() {
//        val totalTime = 1 * 60 * 1000L // 5 minutes in milliseconds
//        timer = object : CountDownTimer(totalTime, 1000) {
//            override fun onTick(millisUntilFinished: Long) {
//                val minutes = (millisUntilFinished / 1000) / 60
//                val seconds = (millisUntilFinished / 1000) % 60
//                binding.timerText5.text = String.format("%02d:%02d", minutes, seconds)
//            }
//
//            override fun onFinish() {
//                binding.timerText5.text = "00:00"
//                Toast.makeText(applicationContext, "Time's up!", Toast.LENGTH_SHORT).show()
//            }
//        }
//
//        // Start button click listener
//        binding.startButton.setOnClickListener {
//            timer?.start()
//            Toast.makeText(this, "Timer Started", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//    private fun setupTimer6() {
//        val totalTime = 1 * 60 * 1000L // 5 minutes in milliseconds
//        timer = object : CountDownTimer(totalTime, 1000) {
//            override fun onTick(millisUntilFinished: Long) {
//                val minutes = (millisUntilFinished / 1000) / 60
//                val seconds = (millisUntilFinished / 1000) % 60
//                binding.timerText6.text = String.format("%02d:%02d", minutes, seconds)
//            }
//
//            override fun onFinish() {
//                binding.timerText6.text = "00:00"
//                Toast.makeText(applicationContext, "Time's up!", Toast.LENGTH_SHORT).show()
//            }
//        }
//
//        // Start button click listener
//        binding.startButton.setOnClickListener {
//            timer?.start()
//            Toast.makeText(this, "Timer Started", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//    private fun setupTimer7() {
//        val totalTime = 1 * 60 * 1000L // 5 minutes in milliseconds
//        timer = object : CountDownTimer(totalTime, 1000) {
//            override fun onTick(millisUntilFinished: Long) {
//                val minutes = (millisUntilFinished / 1000) / 60
//                val seconds = (millisUntilFinished / 1000) % 60
//                binding.timerText7.text = String.format("%02d:%02d", minutes, seconds)
//            }
//
//            override fun onFinish() {
//                binding.timerText7.text = "00:00"
//                Toast.makeText(applicationContext, "Time's up!", Toast.LENGTH_SHORT).show()
//            }
//        }
//
//        // Start button click listener
//        binding.startButton.setOnClickListener {
//            timer?.start()
//            Toast.makeText(this, "Timer Started", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//    private fun setupTimer8() {
//        val totalTime = 1 * 60 * 1000L // 5 minutes in milliseconds
//        timer = object : CountDownTimer(totalTime, 1000) {
//            override fun onTick(millisUntilFinished: Long) {
//                val minutes = (millisUntilFinished / 1000) / 60
//                val seconds = (millisUntilFinished / 1000) % 60
//                binding.timerText8.text = String.format("%02d:%02d", minutes, seconds)
//            }
//
//            override fun onFinish() {
//                binding.timerText8.text = "00:00"
//                Toast.makeText(applicationContext, "Time's up!", Toast.LENGTH_SHORT).show()
//            }
//        }
//
//        // Start button click listener
//        binding.startButton.setOnClickListener {
//            timer?.start()
//            Toast.makeText(this, "Timer Started", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//
//    override fun onDestroy() {
//        super.onDestroy()
//        // Stop the continuous breathing exercise when the activity is destroyed
//        isRunning = false
//        timer?.cancel()
//
//    }
//
//}
























//package com.example.fitness
//
//import android.content.Context
//import android.media.AudioManager
//import android.media.ToneGenerator
//import android.os.Build
//import android.os.Bundle
//import android.os.Handler
//import android.os.Looper
//import android.os.VibrationEffect
//import android.os.Vibrator
//import android.widget.TextView
//import androidx.appcompat.app.AppCompatActivity
//import com.airbnb.lottie.LottieAnimationView
//
//class YogaActivity : AppCompatActivity() {
//
//    private lateinit var eventname: TextView
//    private lateinit var lottie2: LottieAnimationView
//    private lateinit var handler: Handler
//    private var isRunning = true  // Flag to control the loop
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_yoga)
//
//        // Initializing views
//        eventname = findViewById(R.id.eventname)
//        lottie2 = findViewById(R.id.lottie2)
//        handler = Handler(Looper.getMainLooper())
//
//        // Start the breathing exercise animation
//        lottie2.animate().translationZ(500f).setStartDelay(0)
//
//        // Start the continuous breathing exercise
//        startContinuousBreathingExercise()
//    }
//
//    private fun startContinuousBreathingExercise() {
//        // Repeat the breathing exercise until activity is stopped
//        handler.post(object : Runnable {
//            override fun run() {
//                if (isRunning) {
//                    startBreathingCycle()
//                    handler.postDelayed(this, 15000)  // Repeat every 15 seconds (adjust based on your timing)
//                }
//            }
//        })
//    }
//
//    private fun startBreathingCycle() {
//        // 1. Inhale phase (Beep/Vibrate when inhaling starts)
//        handler.postDelayed({
//            triggerBeepOrVibration()
//            eventname.text = "Inhale"
//        }, 0)  // Starts immediately
//
//        // 2. Hold phase (Beep/Vibrate when holding breath starts)
//        handler.postDelayed({
//            triggerBeepOrVibration()
//            eventname.text = "Hold Breath"
//        }, 3000)  // After 5 seconds (adjust based on your animation)
//
//        // 3. Exhale phase (Beep/Vibrate when exhaling starts)
//        handler.postDelayed({
//            triggerBeepOrVibration()
//            eventname.text = "Exhale"
//        }, 7000)  // After 10 seconds (adjust based on your animation)
//    }
//
//    // Method to trigger a beep sound or vibration
//    private fun triggerBeepOrVibration() {
//        // Beep sound using ToneGenerator
//        val toneGen = ToneGenerator(AudioManager.STREAM_MUSIC, 100)
//        toneGen.startTone(ToneGenerator.TONE_PROP_BEEP, 200)  // 200 ms beep
//
//        // Vibration
//        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
//        } else {
//            vibrator.vibrate(200)  // Vibrate for 200 ms
//        }
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        // Stop the continuous breathing exercise when the activity is destroyed
//        isRunning = false
//    }
//
//}








