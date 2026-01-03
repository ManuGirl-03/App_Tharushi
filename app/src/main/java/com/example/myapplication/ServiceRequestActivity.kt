package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText

class ServiceRequestActivity : AppCompatActivity() {

    private lateinit var btnBack: Button
    private lateinit var tvSelectedService: TextView
    private lateinit var tvServiceDescription: TextView
    private lateinit var tvServicePrice: TextView
    private lateinit var tvServiceTime: TextView
    private lateinit var etIssueDetails: TextInputEditText
    private lateinit var btnAddPhoto: Button
    private lateinit var tvPhotoStatus: TextView
    private lateinit var rgServiceMethod: RadioGroup
    private lateinit var rbPickup: RadioButton
    private lateinit var rbDropOff: RadioButton
    private lateinit var etPhone: TextInputEditText
    private lateinit var etAddress: TextInputEditText
    private lateinit var btnSubmitRequest: Button

    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var userSession: UserSession
    private var serviceId: Int = 0
    private var serviceName: String = ""
    private var serviceDescription: String = ""
    private var servicePrice: String = ""
    private var serviceTime: String = ""
    private var photoCount: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_service_request)

        // Get service data from intent
        getServiceDataFromIntent()

        // Initialize database and session
        databaseHelper = DatabaseHelper(this)
        userSession = UserSession(this)

        // Initialize views
        initViews()

        // Display service information
        displayServiceInfo()

        // Set click listeners
        setClickListeners()
    }

    private fun getServiceDataFromIntent() {
        serviceId = intent.getIntExtra("service_id", 0)
        serviceName = intent.getStringExtra("service_name") ?: ""
        serviceDescription = intent.getStringExtra("service_description") ?: ""
        servicePrice = intent.getStringExtra("service_price") ?: ""
        serviceTime = intent.getStringExtra("service_time") ?: ""
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btnBack)
        tvSelectedService = findViewById(R.id.tvSelectedService)
        tvServiceDescription = findViewById(R.id.tvServiceDescription)
        tvServicePrice = findViewById(R.id.tvServicePrice)
        tvServiceTime = findViewById(R.id.tvServiceTime)
        etIssueDetails = findViewById(R.id.etIssueDetails)
        btnAddPhoto = findViewById(R.id.btnAddPhoto)
        tvPhotoStatus = findViewById(R.id.tvPhotoStatus)
        rgServiceMethod = findViewById(R.id.rgServiceMethod)
        rbPickup = findViewById(R.id.rbPickup)
        rbDropOff = findViewById(R.id.rbDropOff)
        etPhone = findViewById(R.id.etPhone)
        etAddress = findViewById(R.id.etAddress)
        btnSubmitRequest = findViewById(R.id.btnSubmitRequest)
    }

    private fun displayServiceInfo() {
        tvSelectedService.text = serviceName
        tvServiceDescription.text = serviceDescription
        tvServicePrice.text = "💰 $servicePrice"
        tvServiceTime.text = "⏱️ $serviceTime"
    }

    private fun setClickListeners() {
        btnBack.setOnClickListener {
            finish()
        }

        btnAddPhoto.setOnClickListener {
            // Simulate photo selection
            photoCount++
            tvPhotoStatus.text = if (photoCount == 1) {
                "1 photo added"
            } else {
                "$photoCount photos added"
            }
            Toast.makeText(this, "Photo added! (Demo functionality)", Toast.LENGTH_SHORT).show()
        }

        rgServiceMethod.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rbPickup -> {
                    etAddress.isEnabled = true
                    etAddress.hint = "Address (Required for pickup)"
                }
                R.id.rbDropOff -> {
                    etAddress.isEnabled = false
                    etAddress.hint = "Address (Not required for drop-off)"
                    etAddress.setText("")
                }
            }
        }

        btnSubmitRequest.setOnClickListener {
            submitServiceRequest()
        }
    }

    private fun submitServiceRequest() {
        val issueDetails = etIssueDetails.text.toString().trim()
        val phone = etPhone.text.toString().trim()
        val address = etAddress.text.toString().trim()
        val isPickup = rbPickup.isChecked

        // Validate input
        if (issueDetails.isEmpty()) {
            etIssueDetails.error = "Please describe your issue"
            return
        }

        if (phone.isEmpty()) {
            etPhone.error = "Please enter your phone number"
            return
        }

        if (isPickup && address.isEmpty()) {
            etAddress.error = "Address is required for pickup service"
            return
        }

        // Get service method and pickup time
        val serviceMethod = if (isPickup) "Home Pickup" else "Service Center Drop-off"
        val pickupTime = if (isPickup) {
            // Generate a pickup time slot (next business day between 9 AM - 5 PM)
            val calendar = java.util.Calendar.getInstance()
            calendar.add(java.util.Calendar.DAY_OF_YEAR, 1)
            calendar.set(java.util.Calendar.HOUR_OF_DAY, 10) // 10 AM pickup
            calendar.set(java.util.Calendar.MINUTE, 0)
            java.text.SimpleDateFormat("yyyy-MM-dd hh:mm a", java.util.Locale.getDefault()).format(calendar.time)
        } else null

        // Find service category
        val serviceCategory = ServiceData.getAllServices().find { category ->
            category.services.any { it.name == serviceName }
        }?.name ?: "General Service"

        // Save to database
        val userId = userSession.getUserId()
        if (userId > 0) {
            val requestId = databaseHelper.insertServiceRequest(
                userId = userId,
                serviceName = serviceName,
                serviceCategory = serviceCategory,
                issueDescription = issueDetails,
                serviceMethod = serviceMethod,
                userAddress = if (isPickup) address else null,
                userPhone = phone,
                estimatedPrice = servicePrice,
                estimatedTime = serviceTime,
                pickupTime = pickupTime
            )

            if (requestId > 0) {
                Toast.makeText(
                    this,
                    "Service request submitted successfully!\n" +
                            "Request ID: #$requestId\n" +
                            "Service: $serviceName\n" +
                            "Method: $serviceMethod\n" +
                            (if (pickupTime != null) "Pickup Time: $pickupTime\n" else "") +
                            "We'll contact you at $phone",
                    Toast.LENGTH_LONG
                ).show()

                // Navigate back to dashboard
                val intent = Intent(this, DashboardActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Error submitting request. Please try again.", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Please login again to submit request.", Toast.LENGTH_SHORT).show()
        }
    }
}
