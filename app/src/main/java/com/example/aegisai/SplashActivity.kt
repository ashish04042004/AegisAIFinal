package com.example.aegisai

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.aegisai.ui.auth.AuthActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Check SharedPreferences for an auth token
        val sharedPrefs = getSharedPreferences("AegisAiPrefs", Context.MODE_PRIVATE)
        val authToken = sharedPrefs.getString("AUTH_TOKEN", null)

        // Decide where to navigate
        if (authToken != null && authToken.isNotBlank()) {
            // User is already logged in, go to MainActivity
            goToMainActivity()
        } else {
            // User is not logged in, go to AuthActivity
            goToAuthActivity()
        }
    }

    private fun goToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish() // Close the SplashActivity so the user can't navigate back to it
    }

    private fun goToAuthActivity() {
        val intent = Intent(this, AuthActivity::class.java)
        startActivity(intent)
        finish() // Close the SplashActivity
    }
}