package com.example.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class BookingsActivity : AppCompatActivity() {

    private lateinit var btnBack: Button
    private lateinit var btnAllBookings: Button
    private lateinit var btnActiveBookings: Button
    private lateinit var btnCompletedBookings: Button
    private lateinit var llBookings: LinearLayout

    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var userSession: UserSession
    private var allBookings: List<Map<String, String>> = emptyList()
    private var currentFilter = "All"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bookings)

        // Initialize database and session
        databaseHelper = DatabaseHelper(this)
        userSession = UserSession(this)

        // Initialize views
        initViews()

        // Load bookings data
        loadBookingsData()

        // Display all bookings initially
        displayBookings("All")

        // Set click listeners
        setClickListeners()
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btnBack)
        btnAllBookings = findViewById(R.id.btnAllBookings)
        btnActiveBookings = findViewById(R.id.btnActiveBookings)
        btnCompletedBookings = findViewById(R.id.btnCompletedBookings)
        llBookings = findViewById(R.id.llBookings)
    }

    private fun loadBookingsData() {
        val userId = userSession.getUserId()
        if (userId > 0) {
            allBookings = databaseHelper.getUserServiceRequests(userId)
        }
    }

    private fun setClickListeners() {
        btnBack.setOnClickListener {
            finish()
        }

        btnAllBookings.setOnClickListener {
            displayBookings("All")
            updateFilterButtons("All")
        }

        btnActiveBookings.setOnClickListener {
            displayBookings("Active")
            updateFilterButtons("Active")
        }

        btnCompletedBookings.setOnClickListener {
            displayBookings("Completed")
            updateFilterButtons("Completed")
        }
    }

    private fun updateFilterButtons(selectedFilter: String) {
        currentFilter = selectedFilter

        // Reset all button colors
        btnAllBookings.backgroundTintList = resources.getColorStateList(android.R.color.darker_gray, null)
        btnActiveBookings.backgroundTintList = resources.getColorStateList(android.R.color.darker_gray, null)
        btnCompletedBookings.backgroundTintList = resources.getColorStateList(android.R.color.darker_gray, null)

        // Highlight selected button
        when (selectedFilter) {
            "All" -> btnAllBookings.backgroundTintList = resources.getColorStateList(android.R.color.holo_blue_bright, null)
            "Active" -> btnActiveBookings.backgroundTintList = resources.getColorStateList(android.R.color.holo_orange_dark, null)
            "Completed" -> btnCompletedBookings.backgroundTintList = resources.getColorStateList(android.R.color.holo_green_dark, null)
        }
    }

    private fun displayBookings(filter: String) {
        llBookings.removeAllViews()

        val filteredBookings = when (filter) {
            "Active" -> allBookings.filter {
                it["status"] in listOf("Received", "Under Repair")
            }
            "Completed" -> allBookings.filter {
                it["status"] in listOf("Ready for Pickup", "Completed")
            }
            else -> allBookings
        }

        if (filteredBookings.isEmpty()) {
            val noBookingsView = LayoutInflater.from(this).inflate(android.R.layout.simple_list_item_1, llBookings, false)
            val textView = noBookingsView.findViewById<TextView>(android.R.id.text1)
            textView.text = when (filter) {
                "Active" -> "No active bookings"
                "Completed" -> "No completed bookings"
                else -> "No bookings found"
            }
            textView.textSize = 16f
            textView.setPadding(16, 32, 16, 32)
            textView.gravity = android.view.Gravity.CENTER
            llBookings.addView(noBookingsView)
        } else {
            filteredBookings.forEach { booking ->
                addBookingItem(booking)
            }
        }
    }

    private fun addBookingItem(booking: Map<String, String>) {
        try {
            val bookingView = LayoutInflater.from(this).inflate(R.layout.item_booking_detail, llBookings, false)

            // Find all views
            val tvBookingId = bookingView.findViewById<TextView>(R.id.tvBookingId)
            val tvBookingStatus = bookingView.findViewById<TextView>(R.id.tvBookingStatus)
            val tvBookingServiceName = bookingView.findViewById<TextView>(R.id.tvBookingServiceName)
            val tvBookingCategory = bookingView.findViewById<TextView>(R.id.tvBookingCategory)
            val tvBookingIssue = bookingView.findViewById<TextView>(R.id.tvBookingIssue)
            val tvBookingMethod = bookingView.findViewById<TextView>(R.id.tvBookingMethod)
            val tvBookingPrice = bookingView.findViewById<TextView>(R.id.tvBookingPrice)
            val tvPickupTime = bookingView.findViewById<TextView>(R.id.tvPickupTime)
            val tvTechnician = bookingView.findViewById<TextView>(R.id.tvTechnician)
            val tvRequestDate = bookingView.findViewById<TextView>(R.id.tvRequestDate)
            val tvEstimatedTime = bookingView.findViewById<TextView>(R.id.tvEstimatedTime)
            val tvBookingNotes = bookingView.findViewById<TextView>(R.id.tvBookingNotes)

            val llPickupTime = bookingView.findViewById<LinearLayout>(R.id.llPickupTime)
            val llTechnicianInfo = bookingView.findViewById<LinearLayout>(R.id.llTechnicianInfo)
            val llNotes = bookingView.findViewById<LinearLayout>(R.id.llNotes)

            // Set basic information
            tvBookingId.text = "Request #${booking["request_id"]}"
            tvBookingStatus.text = booking["status"]
            tvBookingServiceName.text = booking["service_name"]

            // Add emoji to category
            val categoryIcon = when (booking["service_category"]) {
                "Smartphones & Tablets" -> "📱"
                "Laptops & Computers" -> "💻"
                "Televisions" -> "📺"
                "Air Conditioners" -> "❄️"
                "Refrigerators" -> "🧊"
                "Washing Machines" -> "👔"
                else -> "🔧"
            }
            tvBookingCategory.text = "$categoryIcon ${booking["service_category"]}"

            tvBookingIssue.text = booking["issue_description"]

            // Service method with icon
            val methodIcon = if (booking["service_method"] == "Home Pickup") "🚚" else "🏪"
            tvBookingMethod.text = "$methodIcon ${booking["service_method"]}"

            tvBookingPrice.text = "💰 ${booking["estimated_price"]}"
            tvRequestDate.text = booking["request_date"]
            tvEstimatedTime.text = "⏱️ ${booking["estimated_time"]}"

            // Show pickup time if available
            booking["pickup_time"]?.let { pickupTime ->
                if (pickupTime.isNotEmpty()) {
                    llPickupTime.visibility = View.VISIBLE
                    tvPickupTime.text = pickupTime
                }
            }

            // Show technician info if available
            booking["technician_name"]?.let { technician ->
                if (technician.isNotEmpty()) {
                    llTechnicianInfo.visibility = View.VISIBLE
                    tvTechnician.text = technician
                }
            }

            // Show notes if available
            booking["notes"]?.let { notes ->
                if (notes.isNotEmpty()) {
                    llNotes.visibility = View.VISIBLE
                    tvBookingNotes.text = notes
                }
            }

            // Set status color and background
            setStatusStyling(tvBookingStatus, booking["status"] ?: "")

            // Add long press listener for delete functionality
            bookingView.setOnLongClickListener {
                showDeleteDialog(booking["request_id"]?.toLongOrNull() ?: -1, booking["service_name"] ?: "")
                true
            }

            llBookings.addView(bookingView)

        } catch (e: Exception) {
            // Fallback to simple text view if layout fails
            addSimpleBookingItem(booking)
        }
    }

    private fun addSimpleBookingItem(booking: Map<String, String>) {
        val bookingView = TextView(this)

        val requestId = booking["request_id"] ?: "N/A"
        val serviceName = booking["service_name"] ?: "Unknown Service"
        val status = booking["status"] ?: "Unknown"
        val price = booking["estimated_price"] ?: ""
        val method = booking["service_method"] ?: ""
        val date = booking["request_date"] ?: ""
        val issue = booking["issue_description"] ?: ""

        val methodIcon = if (method == "Home Pickup") "🚚" else "🏪"

        bookingView.text = """
            Request #$requestId - $serviceName
            Status: $status | $price
            $methodIcon $method
            Issue: $issue
            Date: $date
        """.trimIndent()

        bookingView.textSize = 14f
        bookingView.setPadding(16, 16, 16, 16)
        bookingView.setBackgroundColor(resources.getColor(android.R.color.white, null))

        // Set status color
        when (status) {
            "Received" -> bookingView.setTextColor(resources.getColor(android.R.color.holo_orange_dark, null))
            "Under Repair" -> bookingView.setTextColor(resources.getColor(android.R.color.holo_blue_dark, null))
            "Ready for Pickup" -> bookingView.setTextColor(resources.getColor(android.R.color.holo_green_dark, null))
            else -> bookingView.setTextColor(resources.getColor(android.R.color.black, null))
        }

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(0, 0, 0, 16)
        bookingView.layoutParams = params

        // Add long press listener for delete functionality
        bookingView.setOnLongClickListener {
            showDeleteDialog(booking["request_id"]?.toLongOrNull() ?: -1, booking["service_name"] ?: "")
            true
        }

        llBookings.addView(bookingView)
    }

    private fun setStatusStyling(statusView: TextView, status: String) {
        when (status) {
            "Received" -> {
                statusView.setTextColor(resources.getColor(android.R.color.holo_orange_dark, null))
                statusView.setBackgroundColor(resources.getColor(android.R.color.holo_orange_light, null))
            }
            "Under Repair" -> {
                statusView.setTextColor(resources.getColor(android.R.color.holo_blue_dark, null))
                statusView.setBackgroundColor(resources.getColor(android.R.color.holo_blue_light, null))
            }
            "Ready for Pickup" -> {
                statusView.setTextColor(resources.getColor(android.R.color.holo_green_dark, null))
                statusView.setBackgroundColor(resources.getColor(android.R.color.holo_green_light, null))
            }
            "Completed" -> {
                statusView.setTextColor(resources.getColor(android.R.color.darker_gray, null))
                statusView.setBackgroundColor(resources.getColor(android.R.color.background_light, null))
            }
            else -> {
                statusView.setTextColor(resources.getColor(android.R.color.black, null))
                statusView.setBackgroundColor(resources.getColor(android.R.color.background_light, null))
            }
        }
    }

    private fun showDeleteDialog(requestId: Long, serviceName: String) {
        if (requestId <= 0) return

        val dialog = androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Delete Booking")
            .setMessage("Are you sure you want to delete the booking for '$serviceName'?\n\nThis action cannot be undone.")
            .setPositiveButton("Delete") { _, _ ->
                deleteBooking(requestId)
            }
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()

        // Customize button colors
        dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE)?.setTextColor(resources.getColor(android.R.color.holo_red_dark, null))
    }

    private fun deleteBooking(requestId: Long) {
        if (databaseHelper.deleteServiceRequest(requestId)) {
            android.widget.Toast.makeText(this, "Booking deleted successfully", android.widget.Toast.LENGTH_SHORT).show()

            // Refresh the bookings list
            loadBookingsData()
            displayBookings(currentFilter)
        } else {
            android.widget.Toast.makeText(this, "Failed to delete booking", android.widget.Toast.LENGTH_SHORT).show()
        }
    }
}

