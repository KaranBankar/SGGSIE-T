package com.example.sggsiet

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.sggsiet.AdminModule.PrincipalDashboard
import com.example.sggsiet.DocterModule.DocterDashboard
import com.example.sggsiet.GardModule.GardDashBoardActivity
import com.example.sggsiet.StudentModule.StudentDashboard
import com.example.sggsiet.StudentModule.GardLogin

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val sharedPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val isLoggedIn = sharedPref.getBoolean("isLoggedIn", false)
        val userType = sharedPref.getString("userType", "")

        val gardSharedPref = getSharedPreferences("GardLoginPrefs", Context.MODE_PRIVATE)
        val isGardLoggedIn = gardSharedPref.getBoolean("isLoggedIn", false)

        Handler(Looper.getMainLooper()).postDelayed({
            when {
                // ✅ If Gard is logged in, redirect to GardDashBoard
                isGardLoggedIn -> startActivity(Intent(this, GardDashBoardActivity::class.java))

                // ✅ If any other user is logged in, redirect to respective dashboard
                isLoggedIn -> {
                    when (userType) {
                        "Administration" -> startActivity(Intent(this, PrincipalDashboard::class.java))
                        "Faculty" -> startActivity(Intent(this, FacultyModuleActivity::class.java))
                        "Doctor" -> startActivity(Intent(this, DocterDashboard::class.java))
                        "Student" -> startActivity(Intent(this, StudentDashboard::class.java))
                        else -> startActivity(Intent(this, Login::class.java))
                    }
                }

                // ✅ If no user is logged in, go to login screen
                else -> startActivity(Intent(this, Login::class.java))
            }
            finish()
        }, 1000)
    }
}
