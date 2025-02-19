package com.example.sggsiet

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.sggsiet.AdminModule.PrincipalDashboard
import com.example.sggsiet.DocterModule.DocterDashboard
import com.example.sggsiet.StudentModule.StudentDashboard

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)


        val sharedPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val isLoggedIn = sharedPref.getBoolean("isLoggedIn", false)
        val userType = sharedPref.getString("userType", "")

        Handler(Looper.getMainLooper()).postDelayed({
            if (isLoggedIn) {
                when (userType) {
                    "Administration" -> startActivity(Intent(this, PrincipalDashboard::class.java))
                    "Faculty" -> startActivity(Intent(this, FacultyModuleActivity::class.java))
                    "Doctor" -> startActivity(Intent(this, DocterDashboard::class.java))
                    "Student" -> startActivity(Intent(this, StudentDashboard::class.java))
                    else -> startActivity(Intent(this, Login::class.java))
                }
            } else {
                startActivity(Intent(this, Login::class.java))
            }
            finish()
        }, 1000)
    }
}
