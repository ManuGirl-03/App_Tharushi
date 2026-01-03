package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class DashboardActivity : AppCompatActivity() {

    private lateinit var btnLogout: Button
    private lateinit var btnBrowseServices: Button
    private lateinit var btnNewRequest: Button
    private lateinit var btnMyBookings: Button
    private lateinit var btnProfile: Button
    private lateinit var btnSupport: Button
    private lateinit var btnEmergency: Button
    private lateinit var userSession: UserSession

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        // Initialize user session
        userSession = UserSession(this)

        // Initialize views
        initViews()

        // Set click listeners
        setClickListeners()
    }

    private fun initViews() {
        btnLogout = findViewById(R.id.btnLogout)
        btnBrowseServices = findViewById(R.id.btnBrowseServices)
        btnNewRequest = findViewById(R.id.btnNewRequest)
        btnMyBookings = findViewById(R.id.btnMyBookings)
        btnProfile = findViewById(R.id.btnProfile)
        btnSupport = findViewById(R.id.btnSupport)
        btnEmergency = findViewById(R.id.btnEmergency)
    }

    private fun setClickListeners() {
        btnLogout.setOnClickListener {
            logout()
        }

        btnBrowseServices.setOnClickListener {
            // Navigate to browse services activity
            val intent = Intent(this, BrowseServicesActivity::class.java)
            startActivity(intent)
        }

        btnNewRequest.setOnClickListener {
            // Navigate to browse services for new request
            val intent = Intent(this, BrowseServicesActivity::class.java)
            startActivity(intent)
        }

        btnMyBookings.setOnClickListener {
            val intent = Intent(this, BookingsActivity::class.java)
            startActivity(intent)
        }

        btnProfile.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        btnSupport.setOnClickListener {
            val intent = Intent(this, SupportActivity::class.java)
            startActivity(intent)
        }

        btnEmergency.setOnClickListener {
            // Direct emergency call
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = android.net.Uri.parse("tel:+94117778888")
            startActivity(intent)
        }
    }

    private fun logout() {
        // Clear user session
        userSession.clearSession()

        Toast.makeText(this, "Logged out successfully!", Toast.LENGTH_SHORT).show()

        // Navigate back to login
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        // Prevent going back to login after successful login
        // Instead show a toast or exit app
        Toast.makeText(this, "Press logout to exit", Toast.LENGTH_SHORT).show()
        super.onBackPressed()
    }
}
