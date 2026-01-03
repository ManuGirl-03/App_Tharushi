package com.example.myapplication

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText

class RegisterActivity : AppCompatActivity() {

    private lateinit var etName: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var etPhone: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var btnRegister: Button
    private lateinit var tvLogin: TextView
    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Initialize database helper
        databaseHelper = DatabaseHelper(this)

        // Initialize views
        initViews()

        // Set click listeners
        setClickListeners()
    }

    private fun initViews() {
        etName = findViewById(R.id.etName)
        etEmail = findViewById(R.id.etEmail)
        etPhone = findViewById(R.id.etPhone)
        etPassword = findViewById(R.id.etPassword)
        btnRegister = findViewById(R.id.btnRegister)
        tvLogin = findViewById(R.id.tvLogin)
    }

    private fun setClickListeners() {
        btnRegister.setOnClickListener {
            registerUser()
        }

        tvLogin.setOnClickListener {
            finish() // Go back to login
        }
    }

    private fun registerUser() {
        val name = etName.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val phone = etPhone.text.toString().trim()
        val password = etPassword.text.toString().trim()

        // Validate input
        if (name.isEmpty()) {
            etName.error = "Please enter your name"
            return
        }

        if (email.isEmpty()) {
            etEmail.error = "Please enter email address"
            return
        }

        if (phone.isEmpty()) {
            etPhone.error = "Please enter phone number"
            return
        }

        if (password.isEmpty()) {
            etPassword.error = "Please enter password"
            return
        }

        if (password.length < 6) {
            etPassword.error = "Password must be at least 6 characters"
            return
        }

        // Register user
        if (databaseHelper.registerUser(email, phone, password, name)) {
            Toast.makeText(this, "Registration successful! Please login.", Toast.LENGTH_LONG).show()
            finish() // Go back to login
        } else {
            Toast.makeText(this, "Registration failed. Email or phone already exists.", Toast.LENGTH_LONG).show()
        }
    }
}
