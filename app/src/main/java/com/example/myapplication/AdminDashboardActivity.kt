package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AdminDashboardActivity : AppCompatActivity() {

    private lateinit var btnAdminLogout: Button
    private lateinit var tvTotalRequests: TextView
    private lateinit var tvActiveRequests: TextView
    private lateinit var tvCompletedRequests: TextView
    private lateinit var btnManageRequests: Button
    private lateinit var btnViewReports: Button
    private lateinit var btnTechnicianAssign: Button
    private lateinit var btnUpdateStatus: Button
    private lateinit var llRecentRequests: LinearLayout

    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var userSession: UserSession

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)

        // Initialize database and session
        databaseHelper = DatabaseHelper(this)
        userSession = UserSession(this)

        // Check if user is actually admin
        if (!userSession.isAdmin()) {
            Toast.makeText(this, "Access denied. Admin privileges required.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Initialize views
        initViews()

        // Load statistics
        loadStatistics()

        // Load recent requests
        loadRecentRequests()

        // Set click listeners
        setClickListeners()
    }

    private fun initViews() {
        btnAdminLogout = findViewById(R.id.btnAdminLogout)
        tvTotalRequests = findViewById(R.id.tvTotalRequests)
        tvActiveRequests = findViewById(R.id.tvActiveRequests)
        tvCompletedRequests = findViewById(R.id.tvCompletedRequests)
        btnManageRequests = findViewById(R.id.btnManageRequests)
        btnViewReports = findViewById(R.id.btnViewReports)
        btnTechnicianAssign = findViewById(R.id.btnTechnicianAssign)
        btnUpdateStatus = findViewById(R.id.btnUpdateStatus)
        llRecentRequests = findViewById(R.id.llRecentRequests)
    }

    private fun loadStatistics() {
        val allRequests = databaseHelper.getAllServiceRequests()
        val activeRequests = allRequests.filter {
            it["status"] in listOf("Received", "Under Repair")
        }
        val completedRequests = allRequests.filter {
            it["status"] in listOf("Ready for Pickup", "Completed")
        }

        tvTotalRequests.text = allRequests.size.toString()
        tvActiveRequests.text = activeRequests.size.toString()
        tvCompletedRequests.text = completedRequests.size.toString()
    }

    private fun loadRecentRequests() {
        val recentRequests = databaseHelper.getAllServiceRequests().take(5)

        llRecentRequests.removeAllViews()

        if (recentRequests.isEmpty()) {
            val noRequestsView = TextView(this)
            noRequestsView.text = "No service requests found"
            noRequestsView.textSize = 16f
            noRequestsView.setPadding(16, 32, 16, 32)
            noRequestsView.gravity = android.view.Gravity.CENTER
            llRecentRequests.addView(noRequestsView)
        } else {
            recentRequests.forEach { request ->
                addRequestPreview(request)
            }
        }
    }

    private fun addRequestPreview(request: Map<String, String>) {
        val requestView = TextView(this)

        val requestId = request["request_id"] ?: "N/A"
        val serviceName = request["service_name"] ?: "Unknown Service"
        val status = request["status"] ?: "Unknown"
        val customerName = "Customer ID: ${request["user_id"]}"
        val date = request["request_date"] ?: ""

        requestView.text = """
            Request #$requestId - $serviceName
            $customerName
            Status: $status
            Date: $date
        """.trimIndent()

        requestView.textSize = 14f
        requestView.setPadding(16, 16, 16, 16)
        requestView.setBackgroundColor(resources.getColor(android.R.color.white, null))

        // Set status color
        when (status) {
            "Received" -> requestView.setTextColor(resources.getColor(android.R.color.holo_orange_dark, null))
            "Under Repair" -> requestView.setTextColor(resources.getColor(android.R.color.holo_blue_dark, null))
            "Ready for Pickup" -> requestView.setTextColor(resources.getColor(android.R.color.holo_green_dark, null))
            else -> requestView.setTextColor(resources.getColor(android.R.color.black, null))
        }

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(0, 0, 0, 16)
        requestView.layoutParams = params

        // Add click listener to open service management
        requestView.setOnClickListener {
            openServiceManagement(request)
        }

        llRecentRequests.addView(requestView)
    }

    private fun setClickListeners() {
        btnAdminLogout.setOnClickListener {
            logout()
        }

        btnManageRequests.setOnClickListener {
            val intent = Intent(this, AdminServiceManagementActivity::class.java)
            startActivity(intent)
        }

        btnViewReports.setOnClickListener {
            Toast.makeText(this, "Reports feature coming soon!", Toast.LENGTH_SHORT).show()
        }

        btnTechnicianAssign.setOnClickListener {
            val intent = Intent(this, AdminServiceManagementActivity::class.java)
            intent.putExtra("focus_mode", "assign_technician")
            startActivity(intent)
        }

        btnUpdateStatus.setOnClickListener {
            val intent = Intent(this, AdminServiceManagementActivity::class.java)
            intent.putExtra("focus_mode", "update_status")
            startActivity(intent)
        }
    }

    private fun openServiceManagement(request: Map<String, String>) {
        val intent = Intent(this, AdminServiceManagementActivity::class.java)
        intent.putExtra("request_id", request["request_id"])
        startActivity(intent)
    }

    private fun logout() {
        // Clear user session
        userSession.clearSession()

        Toast.makeText(this, "Admin logged out successfully!", Toast.LENGTH_SHORT).show()

        // Navigate back to login
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    override fun onResume() {
        super.onResume()
        // Refresh statistics and recent requests when returning to this activity
        loadStatistics()
        loadRecentRequests()
    }
}
