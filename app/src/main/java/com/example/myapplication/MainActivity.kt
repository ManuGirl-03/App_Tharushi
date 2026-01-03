package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            Log.d("MainActivity", "Starting MainActivity")

            // Small delay to ensure proper initialization
            android.os.Handler(mainLooper).postDelayed({
                navigateToLogin()
            }, 100)

        } catch (e: Exception) {
            Log.e("MainActivity", "Error in onCreate: ${e.message}")
            // Fallback - set a simple layout if there's an issue
            setContentView(R.layout.activity_main)

            // Try navigation again after a delay
            android.os.Handler(mainLooper).postDelayed({
                navigateToLogin()
            }, 1000)
        }
    }

    private fun navigateToLogin() {
        try {
            Log.d("MainActivity", "Attempting to navigate to LoginActivity")
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish() // Close MainActivity so user can't go back to it
        } catch (e: Exception) {
            Log.e("MainActivity", "Error navigating to LoginActivity: ${e.message}")
            Toast.makeText(this, "Error starting app: ${e.message}", Toast.LENGTH_LONG).show()
            // Keep the main layout visible as fallback
            setContentView(R.layout.activity_main)
        }
    }
}