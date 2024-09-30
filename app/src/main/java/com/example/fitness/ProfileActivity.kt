package com.example.fitness

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

class ProfileActivity : AppCompatActivity() {

    private lateinit var yogaProgressBar: ProgressBar
    private lateinit var cyclingProgressBar: ProgressBar
    private lateinit var runningProgressBar: ProgressBar
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    private lateinit var progressText1: TextView
    private lateinit var progressText2: TextView
    private lateinit var progressText3: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Initialize SwipeRefreshLayout
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)

        // Set up the refresh listener
        swipeRefreshLayout.setOnRefreshListener {
            refreshPage()
        }

        // Initialize Views
        yogaProgressBar = findViewById(R.id.yogaProgressBar)
        cyclingProgressBar = findViewById(R.id.cyclingProgressBar)
        runningProgressBar = findViewById(R.id.runningProgressBar)

        progressText1 = findViewById(R.id.progressText1)
        progressText2 = findViewById(R.id.progressText2)
        progressText3 = findViewById(R.id.progressText3)

        // Start Activity Buttons
        findViewById<AppCompatButton>(R.id.startYogaButton).setOnClickListener {
            startActivity(Intent(this, YogaActivity::class.java))
        }
        findViewById<AppCompatButton>(R.id.startCyclingButton).setOnClickListener {
            startActivity(Intent(this, CyclingActivity::class.java))
        }
        findViewById<AppCompatButton>(R.id.startRunningButton).setOnClickListener {
            startActivity(Intent(this, RunningActivity::class.java))
        }

        // Load Progress on Create
        loadProgress()
        updateProgressBars()
    }

    override fun onResume() {
        super.onResume()
        loadProgress()
        updateProgressBars()
    }
    private fun loadProgress() {
        val sharedPreferences = getSharedPreferences("fitness_prefs", Context.MODE_PRIVATE)
        val isFirstLaunch = sharedPreferences.getBoolean("isFirstLaunch", true)

        // If it's the first launch, set default values to 0
        if (isFirstLaunch) {
            with(sharedPreferences.edit()) {
                putInt("yogaTime", 0)
                putInt("cyclingTime", 0)
                putInt("runningTime", 0)
                putBoolean("isFirstLaunch", false)
                apply() // Apply the changes
            }
        }

        // Retrieve data from SharedPreferences
        val yogaProgress = sharedPreferences.getInt("yogaTime", 0)
        val cyclingProgress = sharedPreferences.getInt("cyclingTime", 0)
        val runningProgress = sharedPreferences.getInt("runningTime", 0)

        // Update progress text views
        progressText1.text = "Total Progress: $yogaProgress minutes"
        progressText2.text = "Total Progress: $cyclingProgress minutes"
        progressText3.text = "Total Progress: $runningProgress minutes"

        // Set progress for progress bars
        yogaProgressBar.progress = yogaProgress
        cyclingProgressBar.progress = cyclingProgress
        runningProgressBar.progress = runningProgress
    }


//    private fun loadProgress() {
//        val sharedPreferences = getSharedPreferences("fitness_prefs", Context.MODE_PRIVATE)
//
//        // Retrieve data
//        val yogaProgress = sharedPreferences.getInt("yogaTime", 0)
//        val cyclingProgress = sharedPreferences.getInt("cyclingTime", 0)
//        val runningProgress = sharedPreferences.getInt("runningTime", 0)
//
//        // Update progress text views
//        progressText1.text = "Total Progress: $yogaProgress minutes"
//        progressText2.text = "Total Progress: $cyclingProgress minutes"
//        progressText3.text = "Total Progress: $runningProgress minutes"
//
//        // Set progress for progress bars
//        yogaProgressBar.progress = yogaProgress // Update with actual logic
//        cyclingProgressBar.progress = cyclingProgress // Update with actual logic
//        runningProgressBar.progress = runningProgress // Update with actual logic
//    }

    private fun updateProgressBars() {
        // This can be further developed based on how you handle actual durations.

        val sharedPreferences = getSharedPreferences("fitness_prefs", Context.MODE_PRIVATE)
        val yogaProgress = sharedPreferences.getInt("yogaTime", 0)
        val cyclingProgress = sharedPreferences.getInt("cyclingTime", 0)
        val runningProgress = sharedPreferences.getInt("runningTime", 0)

        // You can set a max value for your progress bars, e.g., 1000 minutes as max
        val maxProgress = 1000
        yogaProgressBar.max = maxProgress
        cyclingProgressBar.max = maxProgress
        runningProgressBar.max = maxProgress

        yogaProgressBar.progress = yogaProgress
        cyclingProgressBar.progress = cyclingProgress
        runningProgressBar.progress = runningProgress
    }

    private fun refreshPage() {
        loadProgress() // Reload progress

//         Reset text to initial state (show 0 minutes)
        progressText1.text = "Total Progress: 0 minutes"
        progressText2.text = "Total Progress: 0 minutes"
        progressText3.text = "Total Progress: 0 minutes"

        // Reset progress bars
        yogaProgressBar.progress = 0
        cyclingProgressBar.progress = 0
        runningProgressBar.progress = 0

        // Optionally, show a message to the user
        Toast.makeText(this, "Page refreshed", Toast.LENGTH_SHORT).show()

        // Stop the refresh animation
        swipeRefreshLayout.isRefreshing = false

    }
}




//package com.example.fitnessapp
//
//import android.os.Bundle
//import androidx.appcompat.app.AppCompatActivity
//import com.example.fitnessapp.databinding.ActivityProfileBinding
//import com.github.mikephil.charting.data.Entry
//import com.github.mikephil.charting.data.LineData
//import com.github.mikephil.charting.data.LineDataSet
//
//class ProfileActivity : AppCompatActivity() {
//
//    // Using view binding
//    private lateinit var binding: ActivityProfileBinding
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        // Initializing view binding
//        binding = ActivityProfileBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        // Setting dummy progress values for exercises
//        binding.cardioProgressBar.progress = 70
//        binding.strengthProgressBar.progress = 80
//        binding.flexibilityProgressBar.progress = 60
//
//        // Line chart data for workouts progress
//        setUpLineChart()
//    }
//
//    // Function to set up the line chart
//    private fun setUpLineChart() {
//        val entries = ArrayList<Entry>()
//        entries.add(Entry(0f, 1f))
//        entries.add(Entry(1f, 2f))
//        entries.add(Entry(2f, 3f))
//        entries.add(Entry(3f, 4f))
//        entries.add(Entry(4f, 5f))
//
//        // Creating dataset and styling it
//        val lineDataSet = LineDataSet(entries, "Workout Progress")
//        lineDataSet.color = getColor(R.color.teal_200)
//        lineDataSet.setCircleColor(getColor(R.color.purple_200))
//        lineDataSet.circleRadius = 5f
//        lineDataSet.lineWidth = 2f
//
//        // Creating LineData object and setting it to the chart
//        val lineData = LineData(lineDataSet)
//        binding.workoutLineChart.data = lineData
//
//        // Refreshing the chart
//        binding.workoutLineChart.invalidate()
//    }
//}
