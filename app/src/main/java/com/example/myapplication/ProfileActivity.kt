package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText

class ProfileActivity : AppCompatActivity() {

    private lateinit var btnBack: Button
    private lateinit var etProfileName: TextInputEditText
    private lateinit var etProfileEmail: TextInputEditText
    private lateinit var etProfilePhone: TextInputEditText
    private lateinit var btnUpdateProfile: Button
    private lateinit var etCurrentPassword: TextInputEditText
    private lateinit var etNewPassword: TextInputEditText
    private lateinit var etConfirmPassword: TextInputEditText
    private lateinit var btnChangePassword: Button
    private lateinit var btnViewAllBookings: Button
    private lateinit var llServiceHistory: LinearLayout

    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var userSession: UserSession

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Initialize database and session
        databaseHelper = DatabaseHelper(this)
        userSession = UserSession(this)

        // Initialize views
        initViews()

        // Load profile data
        loadProfileData()

        // Load service history
        loadServiceHistory()

        // Set click listeners
        setClickListeners()
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btnBack)
        etProfileName = findViewById(R.id.etProfileName)
        etProfileEmail = findViewById(R.id.etProfileEmail)
        etProfilePhone = findViewById(R.id.etProfilePhone)
        btnUpdateProfile = findViewById(R.id.btnUpdateProfile)
        etCurrentPassword = findViewById(R.id.etCurrentPassword)
        etNewPassword = findViewById(R.id.etNewPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        btnChangePassword = findViewById(R.id.btnChangePassword)
        btnViewAllBookings = findViewById(R.id.btnViewAllBookings)
        llServiceHistory = findViewById(R.id.llServiceHistory)
    }

    private fun loadProfileData() {
        etProfileName.setText(userSession.getUserName())
        etProfileEmail.setText(userSession.getUserEmail())
        etProfilePhone.setText(userSession.getUserPhone())
    }

    private fun loadServiceHistory() {
        val userId = userSession.getUserId()
        if (userId > 0) {
            val serviceRequests = databaseHelper.getUserServiceRequests(userId)

            llServiceHistory.removeAllViews()

            if (serviceRequests.isEmpty()) {
                val noHistoryView = LayoutInflater.from(this).inflate(android.R.layout.simple_list_item_1, llServiceHistory, false)
                val textView = noHistoryView.findViewById<TextView>(android.R.id.text1)
                textView.text = "No service history found"
                textView.textSize = 14f
                textView.setPadding(0, 16, 0, 16)
                llServiceHistory.addView(noHistoryView)
            } else {
                // Show only the 2 most recent requests
                serviceRequests.take(2).forEach { request ->
                    addServiceHistoryItem(request)
                }
            }
        }
    }

    private fun addServiceHistoryItem(request: Map<String, String>) {
        // Create a simple text view for service history item
        val historyView = TextView(this)

        val serviceName = request["service_name"] ?: "Unknown Service"
        val status = request["status"] ?: "Unknown"
        val date = request["request_date"] ?: ""
        val price = request["estimated_price"] ?: ""

        historyView.text = "$serviceName\nStatus: $status | $price\n$date"
        historyView.textSize = 14f
        historyView.setPadding(16, 12, 16, 12)
        historyView.setBackgroundColor(resources.getColor(android.R.color.white, null))

        // Set status color
        when (status) {
            "Received" -> historyView.setTextColor(resources.getColor(android.R.color.holo_orange_dark, null))
            "Under Repair" -> historyView.setTextColor(resources.getColor(android.R.color.holo_blue_dark, null))
            "Ready for Pickup" -> historyView.setTextColor(resources.getColor(android.R.color.holo_green_dark, null))
            else -> historyView.setTextColor(resources.getColor(android.R.color.black, null))
        }

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(0, 0, 0, 16)
        historyView.layoutParams = params

        llServiceHistory.addView(historyView)
    }

    private fun setClickListeners() {
        btnBack.setOnClickListener {
            finish()
        }

        btnUpdateProfile.setOnClickListener {
            updateProfile()
        }

        btnChangePassword.setOnClickListener {
            changePassword()
        }

        btnViewAllBookings.setOnClickListener {
            val intent = Intent()
            intent.setClassName(this, "com.example.myapplication.BookingsActivity")
            startActivity(intent)
        }
    }

    private fun updateProfile() {
        val name = etProfileName.text.toString().trim()
        val email = etProfileEmail.text.toString().trim()
        val phone = etProfilePhone.text.toString().trim()

        if (name.isEmpty()) {
            etProfileName.error = "Please enter your name"
            return
        }

        if (email.isEmpty()) {
            etProfileEmail.error = "Please enter email"
            return
        }

        if (phone.isEmpty()) {
            etProfilePhone.error = "Please enter phone"
            return
        }

        val userId = userSession.getUserId()
        if (databaseHelper.updateUserProfile(userId, name, email, phone)) {
            userSession.saveUserSession(userId, email, name, phone)
            Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Failed to update profile. Email or phone may already exist.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun changePassword() {
        val currentPassword = etCurrentPassword.text.toString().trim()
        val newPassword = etNewPassword.text.toString().trim()
        val confirmPassword = etConfirmPassword.text.toString().trim()

        if (currentPassword.isEmpty()) {
            etCurrentPassword.error = "Please enter current password"
            return
        }

        if (newPassword.isEmpty()) {
            etNewPassword.error = "Please enter new password"
            return
        }

        if (newPassword.length < 6) {
            etNewPassword.error = "Password must be at least 6 characters"
            return
        }

        if (confirmPassword != newPassword) {
            etConfirmPassword.error = "Passwords do not match"
            return
        }

        val userId = userSession.getUserId()
        if (databaseHelper.changePassword(userId, currentPassword, newPassword)) {
            Toast.makeText(this, "Password changed successfully!", Toast.LENGTH_SHORT).show()
            etCurrentPassword.setText("")
            etNewPassword.setText("")
            etConfirmPassword.setText("")
        } else {
            Toast.makeText(this, "Current password is incorrect.", Toast.LENGTH_SHORT).show()
        }
    }
}
