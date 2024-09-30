package com.example.fitness

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView

class SplashActivity : AppCompatActivity() {

    private lateinit var appname: TextView
    private lateinit var lottie: LottieAnimationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Initializing views
        appname = findViewById(R.id.appname)
        lottie = findViewById(R.id.lottie)

        // Animations
        appname.animate().translationY(70f).setDuration(2000).setStartDelay(0)
        lottie.animate().translationZ(5f).setDuration(2000).setStartDelay(0)

        // Delay to start MainActivity after splash screen
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
            finish()  // Finish splash activity so it doesn't stay in the back stack
        }, 8000)  // 3 seconds delay
    }
}









//package com.example.fitness
//
//import androidx.appcompat.app.AppCompatActivity
//import android.os.Bundle
//import android.widget.TextView
//import com.airbnb.lottie.LottieAnimationView
//
//class SplashActivity : AppCompatActivity() {
//
//    private lateinit var appname : TextView
//    private lateinit var lottie: LottieAnimationView
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_splash)
//
//
//        appname = findViewById(R.id.appname)
//        lottie = findViewById(R.id.lottie)
//
//        appname.animate().translationY(- 500).setDuration(2000).setStartDelay(0)
//        lottie.animate().translationZ(500).setDuration(2000).setStartDelay(2900)
//
//         new Handler().postDelayed(new Runnable() {
//             @Override
//             public void run(){
//                 val intent  = Intent(applicationContext(), MainActivity::class.java)
//                 startActivity(intent)
//             }
//         }, 5000);
//    }
//}