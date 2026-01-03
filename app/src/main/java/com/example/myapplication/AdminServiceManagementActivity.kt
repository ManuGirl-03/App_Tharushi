package com.example.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AdminServiceManagementActivity : AppCompatActivity() {

    private lateinit var btnBack: Button
    private lateinit var btnAllServiceRequests: Button
    private lateinit var btnReceivedRequests: Button
    private lateinit var btnUnderRepairRequests: Button
    private lateinit var btnReadyRequests: Button
    private lateinit var llServiceRequests: LinearLayout

    private lateinit var databaseHelper: DatabaseHelper
    private var allRequests: List<Map<String, String>> = emptyList()
    private var currentFilter = "All"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_service_management)

        // Initialize database
        databaseHelper = DatabaseHelper(this)

        // Initialize views
        initViews()

        // Load requests data
        loadRequestsData()

        // Display all requests initially
        displayRequests("All")

        // Set click listeners
        setClickListeners()
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btnBack)
        btnAllServiceRequests = findViewById(R.id.btnAllServiceRequests)
        btnReceivedRequests = findViewById(R.id.btnReceivedRequests)
        btnUnderRepairRequests = findViewById(R.id.btnUnderRepairRequests)
        btnReadyRequests = findViewById(R.id.btnReadyRequests)
        llServiceRequests = findViewById(R.id.llServiceRequests)
    }

    private fun loadRequestsData() {
        allRequests = databaseHelper.getAllServiceRequests()
    }

    private fun setClickListeners() {
        btnBack.setOnClickListener {
            finish()
        }

        btnAllServiceRequests.setOnClickListener {
            displayRequests("All")
            updateFilterButtons("All")
        }

        btnReceivedRequests.setOnClickListener {
            displayRequests("Received")
            updateFilterButtons("Received")
        }

        btnUnderRepairRequests.setOnClickListener {
            displayRequests("Under Repair")
            updateFilterButtons("Under Repair")
        }

        btnReadyRequests.setOnClickListener {
            displayRequests("Ready for Pickup")
            updateFilterButtons("Ready for Pickup")
        }
    }

    private fun updateFilterButtons(selectedFilter: String) {
        currentFilter = selectedFilter

        // Reset all button colors
        btnAllServiceRequests.backgroundTintList = resources.getColorStateList(android.R.color.darker_gray, null)
        btnReceivedRequests.backgroundTintList = resources.getColorStateList(android.R.color.darker_gray, null)
        btnUnderRepairRequests.backgroundTintList = resources.getColorStateList(android.R.color.darker_gray, null)
        btnReadyRequests.backgroundTintList = resources.getColorStateList(android.R.color.darker_gray, null)

        // Highlight selected button
        when (selectedFilter) {
            "All" -> btnAllServiceRequests.backgroundTintList = resources.getColorStateList(android.R.color.holo_blue_bright, null)
            "Received" -> btnReceivedRequests.backgroundTintList = resources.getColorStateList(android.R.color.holo_orange_dark, null)
            "Under Repair" -> btnUnderRepairRequests.backgroundTintList = resources.getColorStateList(android.R.color.holo_purple, null)
            "Ready for Pickup" -> btnReadyRequests.backgroundTintList = resources.getColorStateList(android.R.color.holo_green_dark, null)
        }
    }

    private fun displayRequests(filter: String) {
        llServiceRequests.removeAllViews()

        val filteredRequests = when (filter) {
            "Received" -> allRequests.filter { it["status"] == "Received" }
            "Under Repair" -> allRequests.filter { it["status"] == "Under Repair" }
            "Ready for Pickup" -> allRequests.filter { it["status"] == "Ready for Pickup" }
            else -> allRequests
        }

        if (filteredRequests.isEmpty()) {
            val noRequestsView = TextView(this)
            noRequestsView.text = when (filter) {
                "Received" -> "No received requests"
                "Under Repair" -> "No requests under repair"
                "Ready for Pickup" -> "No requests ready for pickup"
                else -> "No service requests found"
            }
            noRequestsView.textSize = 16f
            noRequestsView.setPadding(16, 32, 16, 32)
            noRequestsView.gravity = android.view.Gravity.CENTER
            llServiceRequests.addView(noRequestsView)
        } else {
            filteredRequests.forEach { request ->
                addRequestItem(request)
            }
        }
    }

    private fun addRequestItem(request: Map<String, String>) {
        val requestView = LinearLayout(this)
        requestView.orientation = LinearLayout.VERTICAL
        requestView.setPadding(16, 16, 16, 16)
        requestView.setBackgroundColor(resources.getColor(android.R.color.white, null))

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(0, 0, 0, 16)
        requestView.layoutParams = params

        // Request details
        val requestId = request["request_id"] ?: "N/A"
        val serviceName = request["service_name"] ?: "Unknown Service"
        val status = request["status"] ?: "Unknown"
        val customerName = "Customer ID: ${request["user_id"]}"
        val issue = request["issue_description"] ?: ""
        val method = request["service_method"] ?: ""
        val price = request["estimated_price"] ?: ""
        val date = request["request_date"] ?: ""
        val technician = request["technician_name"] ?: "Not assigned"

        // Header with request info
        val headerText = TextView(this)
        headerText.text = "Request #$requestId - $serviceName\n$customerName | Status: $status\n$method | $price | $date"
        headerText.textSize = 14f
        headerText.setTextColor(resources.getColor(android.R.color.black, null))
        headerText.setPadding(0, 0, 0, 8)
        requestView.addView(headerText)

        // Issue description
        val issueText = TextView(this)
        issueText.text = "Issue: $issue"
        issueText.textSize = 12f
        issueText.setTextColor(resources.getColor(android.R.color.darker_gray, null))
        issueText.setPadding(0, 0, 0, 8)
        requestView.addView(issueText)

        // Technician info
        val technicianText = TextView(this)
        technicianText.text = "Technician: $technician"
        technicianText.textSize = 12f
        technicianText.setTextColor(resources.getColor(android.R.color.holo_blue_dark, null))
        technicianText.setPadding(0, 0, 0, 12)
        requestView.addView(technicianText)

        // Action buttons
        val buttonLayout = LinearLayout(this)
        buttonLayout.orientation = LinearLayout.HORIZONTAL

        val updateStatusBtn = Button(this)
        updateStatusBtn.text = "Update Status"
        updateStatusBtn.textSize = 12f
        updateStatusBtn.backgroundTintList = resources.getColorStateList(android.R.color.holo_orange_dark, null)
        updateStatusBtn.setOnClickListener {
            showUpdateStatusDialog(request)
        }

        val assignTechBtn = Button(this)
        assignTechBtn.text = "Assign Tech"
        assignTechBtn.textSize = 12f
        assignTechBtn.backgroundTintList = resources.getColorStateList(android.R.color.holo_green_dark, null)
        assignTechBtn.setOnClickListener {
            showAssignTechnicianDialog(request)
        }

        val buttonParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT)
        buttonParams.weight = 1f
        buttonParams.setMargins(4, 0, 4, 0)

        updateStatusBtn.layoutParams = buttonParams
        assignTechBtn.layoutParams = buttonParams

        buttonLayout.addView(updateStatusBtn)
        buttonLayout.addView(assignTechBtn)
        requestView.addView(buttonLayout)

        llServiceRequests.addView(requestView)
    }

    private fun showUpdateStatusDialog(request: Map<String, String>) {
        val requestId = request["request_id"]?.toLongOrNull() ?: return

        val statuses = arrayOf("Received", "Under Repair", "Ready for Pickup", "Completed")
        val currentStatus = request["status"] ?: "Received"
        var selectedStatus = currentStatus

        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        builder.setTitle("Update Status for Request #${request["request_id"]}")
        builder.setSingleChoiceItems(statuses, statuses.indexOf(currentStatus)) { _, which ->
            selectedStatus = statuses[which]
        }
        builder.setPositiveButton("Update") { _, _ ->
            if (databaseHelper.updateServiceRequestStatus(requestId, selectedStatus)) {
                Toast.makeText(this, "Status updated successfully", Toast.LENGTH_SHORT).show()
                loadRequestsData()
                displayRequests(currentFilter)
            } else {
                Toast.makeText(this, "Failed to update status", Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton("Cancel", null)
        builder.show()
    }

    private fun showAssignTechnicianDialog(request: Map<String, String>) {
        val requestId = request["request_id"]?.toLongOrNull() ?: return

        val technicians = arrayOf("John Smith", "Sarah Johnson", "Mike Wilson", "Emma Davis", "David Brown")
        val currentTechnician = request["technician_name"] ?: ""
        var selectedTechnician = currentTechnician

        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        builder.setTitle("Assign Technician for Request #${request["request_id"]}")

        val currentIndex = if (currentTechnician.isNotEmpty() && technicians.contains(currentTechnician)) {
            technicians.indexOf(currentTechnician)
        } else {
            -1
        }

        builder.setSingleChoiceItems(technicians, currentIndex) { _, which ->
            selectedTechnician = technicians[which]
        }
        builder.setPositiveButton("Assign") { _, _ ->
            if (databaseHelper.updateServiceRequestStatus(requestId, request["status"] ?: "Received", selectedTechnician)) {
                Toast.makeText(this, "Technician assigned successfully", Toast.LENGTH_SHORT).show()
                loadRequestsData()
                displayRequests(currentFilter)
            } else {
                Toast.makeText(this, "Failed to assign technician", Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton("Cancel", null)
        builder.show()
    }
}
