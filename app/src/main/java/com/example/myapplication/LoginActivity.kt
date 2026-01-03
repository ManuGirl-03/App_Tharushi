package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText

class LoginActivity : AppCompatActivity() {

    private lateinit var etEmailPhone: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var btnLogin: Button
    private lateinit var tvRegister: TextView
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var userSession: UserSession

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialize database helper and user session
        databaseHelper = DatabaseHelper(this)
        userSession = UserSession(this)

        // Initialize views
        initViews()

        // Set click listeners
        setClickListeners()
    }

    private fun initViews() {
        etEmailPhone = findViewById(R.id.etEmailPhone)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        tvRegister = findViewById(R.id.tvRegister)
    }

    private fun setClickListeners() {
        btnLogin.setOnClickListener {
            loginUser()
        }

        tvRegister.setOnClickListener {
            // Navigate to register activity
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loginUser() {
        val emailOrPhone = etEmailPhone.text.toString().trim()
        val password = etPassword.text.toString().trim()

        // Validate input
        if (emailOrPhone.isEmpty()) {
            etEmailPhone.error = "Please enter email or phone number"
            return
        }

        if (password.isEmpty()) {
            etPassword.error = "Please enter password"
            return
        }

        // Check credentials
        if (databaseHelper.loginUser(emailOrPhone, password)) {
            // Get user details and save session
            val userId = databaseHelper.getCurrentUserId(emailOrPhone)
            val userProfile = databaseHelper.getUserProfile(userId)
            val userType = databaseHelper.getUserType(emailOrPhone)

            if (userProfile != null) {
                userSession.saveUserSession(
                    userId,
                    userProfile["email"] ?: "",
                    userProfile["name"] ?: "",
                    userProfile["phone"] ?: "",
                    userType
                )
            }

            Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()

            // Navigate to appropriate dashboard based on user type
            val intent = if (userType == "admin") {
                Intent(this, AdminDashboardActivity::class.java)
            } else {
                Intent(this, DashboardActivity::class.java)
            }
            startActivity(intent)
            finish() // Close login activity
        } else {
            Toast.makeText(this, "Invalid credentials. Please try again.", Toast.LENGTH_SHORT).show()
        }
    }
}
