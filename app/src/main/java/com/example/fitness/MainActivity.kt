package com.example.fitness

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.view.MenuItem

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    private lateinit var btn_running: Button
    private lateinit var btn_cycling: Button
    private lateinit var btn_yoga: Button
    private lateinit var bottomNavigationView: BottomNavigationView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize the button to start running activity
        btn_running = findViewById(R.id.btn_running)
        btn_cycling = findViewById(R.id.btn_cycling)
        btn_yoga = findViewById(R.id.btn_yoga)
        bottomNavigationView = findViewById(R.id.bottomNavigationView)

        // Set a click listener on the button
        btn_running.setOnClickListener {
            // Start the RunningActivity
            val intent = Intent(this, RunningActivity::class.java)
            startActivity(intent)
        }
        btn_cycling.setOnClickListener {
            // Start the CyclingActivity
            val intent = Intent(this, CyclingActivity::class.java)
            startActivity(intent)
        }

        btn_yoga.setOnClickListener {
            // Start the CyclingActivity
            val intent = Intent(this, YogaActivity::class.java)
            startActivity(intent)
        }

        bottomNavigationView.setOnNavigationItemSelectedListener { item: MenuItem ->
            when (item.itemId) {
                R.id.nav_exercises -> {
                    // Handle navigation to Exercises (which is MainActivity)
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.nav_profile -> {
                    // Handle navigation to ProfileActivity
                    val intent = Intent(this, ProfileActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.nav_settings -> {
                    // Handle navigation to SettingsActivity
                    val intent = Intent(this, SettingsActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }
}
