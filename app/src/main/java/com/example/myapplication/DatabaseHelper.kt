package com.example.myapplication

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "techcare.db"
        private const val DATABASE_VERSION = 3

        // User table
        private const val TABLE_USERS = "users"
        private const val COLUMN_ID = "id"
        private const val COLUMN_EMAIL = "email"
        private const val COLUMN_PHONE = "phone"
        private const val COLUMN_PASSWORD = "password"
        private const val COLUMN_NAME = "name"
        private const val COLUMN_USER_TYPE = "user_type" // 'customer' or 'admin'

        // Service requests table
        private const val TABLE_SERVICE_REQUESTS = "service_requests"
        private const val COLUMN_REQUEST_ID = "request_id"
        private const val COLUMN_USER_ID = "user_id"
        private const val COLUMN_SERVICE_NAME = "service_name"
        private const val COLUMN_SERVICE_CATEGORY = "service_category"
        private const val COLUMN_ISSUE_DESCRIPTION = "issue_description"
        private const val COLUMN_SERVICE_METHOD = "service_method"
        private const val COLUMN_USER_ADDRESS = "user_address"
        private const val COLUMN_USER_PHONE_REQ = "user_phone_req"
        private const val COLUMN_STATUS = "status"
        private const val COLUMN_ESTIMATED_PRICE = "estimated_price"
        private const val COLUMN_ESTIMATED_TIME = "estimated_time"
        private const val COLUMN_PICKUP_TIME = "pickup_time"
        private const val COLUMN_REQUEST_DATE = "request_date"
        private const val COLUMN_COMPLETION_DATE = "completion_date"
        private const val COLUMN_TECHNICIAN_NAME = "technician_name"
        private const val COLUMN_NOTES = "notes"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createUserTable = """
            CREATE TABLE $TABLE_USERS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_EMAIL TEXT UNIQUE,
                $COLUMN_PHONE TEXT UNIQUE,
                $COLUMN_PASSWORD TEXT NOT NULL,
                $COLUMN_NAME TEXT NOT NULL,
                $COLUMN_USER_TYPE TEXT DEFAULT 'customer'
            )
        """
        db?.execSQL(createUserTable)

        val createServiceRequestsTable = """
            CREATE TABLE $TABLE_SERVICE_REQUESTS (
                $COLUMN_REQUEST_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_USER_ID INTEGER,
                $COLUMN_SERVICE_NAME TEXT NOT NULL,
                $COLUMN_SERVICE_CATEGORY TEXT NOT NULL,
                $COLUMN_ISSUE_DESCRIPTION TEXT NOT NULL,
                $COLUMN_SERVICE_METHOD TEXT NOT NULL,
                $COLUMN_USER_ADDRESS TEXT,
                $COLUMN_USER_PHONE_REQ TEXT NOT NULL,
                $COLUMN_STATUS TEXT DEFAULT 'Received',
                $COLUMN_ESTIMATED_PRICE TEXT,
                $COLUMN_ESTIMATED_TIME TEXT,
                $COLUMN_PICKUP_TIME TEXT,
                $COLUMN_REQUEST_DATE TEXT NOT NULL,
                $COLUMN_COMPLETION_DATE TEXT,
                $COLUMN_TECHNICIAN_NAME TEXT,
                $COLUMN_NOTES TEXT,
                FOREIGN KEY($COLUMN_USER_ID) REFERENCES $TABLE_USERS($COLUMN_ID)
            )
        """
        db?.execSQL(createServiceRequestsTable)

        // Insert demo user
        val demoUser = ContentValues().apply {
            put(COLUMN_EMAIL, "demo@techcare.com")
            put(COLUMN_PHONE, "1234567890")
            put(COLUMN_PASSWORD, "demo123")
            put(COLUMN_NAME, "Demo User")
            put(COLUMN_USER_TYPE, "customer")
        }
        val userId = db?.insert(TABLE_USERS, null, demoUser)

        // Insert admin user
        val adminUser = ContentValues().apply {
            put(COLUMN_EMAIL, "admin@techcare.com")
            put(COLUMN_PHONE, "0771234567")
            put(COLUMN_PASSWORD, "admin123")
            put(COLUMN_NAME, "TechCare Admin")
            put(COLUMN_USER_TYPE, "admin")
        }
        db?.insert(TABLE_USERS, null, adminUser)

        // Insert demo service requests
        if (userId != null && userId > 0) {
            val demoRequest1 = ContentValues().apply {
                put(COLUMN_USER_ID, userId)
                put(COLUMN_SERVICE_NAME, "Mobile Screen Replacement")
                put(COLUMN_SERVICE_CATEGORY, "Smartphones & Tablets")
                put(COLUMN_ISSUE_DESCRIPTION, "Cracked screen due to accidental drop")
                put(COLUMN_SERVICE_METHOD, "Home Pickup")
                put(COLUMN_USER_ADDRESS, "123 Main St, City")
                put(COLUMN_USER_PHONE_REQ, "1234567890")
                put(COLUMN_STATUS, "Under Repair")
                put(COLUMN_ESTIMATED_PRICE, "Rs. 18,000")
                put(COLUMN_ESTIMATED_TIME, "2 hours")
                put(COLUMN_PICKUP_TIME, "2026-01-03 10:00 AM")
                put(COLUMN_REQUEST_DATE, "2026-01-01 09:30 AM")
                put(COLUMN_TECHNICIAN_NAME, "John Smith")
                put(COLUMN_NOTES, "Screen replacement in progress")
            }
            db?.insert(TABLE_SERVICE_REQUESTS, null, demoRequest1)

            val demoRequest2 = ContentValues().apply {
                put(COLUMN_USER_ID, userId)
                put(COLUMN_SERVICE_NAME, "Laptop Not Charging")
                put(COLUMN_SERVICE_CATEGORY, "Laptops & Computers")
                put(COLUMN_ISSUE_DESCRIPTION, "Laptop battery not charging, power adapter seems fine")
                put(COLUMN_SERVICE_METHOD, "Service Center Drop-off")
                put(COLUMN_USER_PHONE_REQ, "1234567890")
                put(COLUMN_STATUS, "Ready for Pickup")
                put(COLUMN_ESTIMATED_PRICE, "Rs. 12,000")
                put(COLUMN_ESTIMATED_TIME, "3 hours")
                put(COLUMN_REQUEST_DATE, "2025-12-30 02:15 PM")
                put(COLUMN_COMPLETION_DATE, "2026-01-02 11:00 AM")
                put(COLUMN_TECHNICIAN_NAME, "Sarah Johnson")
                put(COLUMN_NOTES, "Charging port replaced successfully")
            }
            db?.insert(TABLE_SERVICE_REQUESTS, null, demoRequest2)
        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_SERVICE_REQUESTS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        onCreate(db)
    }

    fun registerUser(email: String, phone: String, password: String, name: String): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_EMAIL, email)
            put(COLUMN_PHONE, phone)
            put(COLUMN_PASSWORD, password)
            put(COLUMN_NAME, name)
        }

        return try {
            val result = db.insert(TABLE_USERS, null, values)
            result != -1L
        } catch (e: Exception) {
            false
        } finally {
            db.close()
        }
    }

    fun loginUser(emailOrPhone: String, password: String): Boolean {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_USERS,
            null,
            "$COLUMN_EMAIL = ? OR $COLUMN_PHONE = ?",
            arrayOf(emailOrPhone, emailOrPhone),
            null,
            null,
            null
        )

        var isValid = false
        if (cursor.moveToFirst()) {
            val storedPassword = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD))
            isValid = storedPassword == password
        }

        cursor.close()
        db.close()
        return isValid
    }

    fun getCurrentUserId(emailOrPhone: String): Long {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_USERS,
            arrayOf(COLUMN_ID),
            "$COLUMN_EMAIL = ? OR $COLUMN_PHONE = ?",
            arrayOf(emailOrPhone, emailOrPhone),
            null,
            null,
            null
        )

        var userId = -1L
        if (cursor.moveToFirst()) {
            userId = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID))
        }

        cursor.close()
        db.close()
        return userId
    }

    fun getUserProfile(userId: Long): Map<String, String>? {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_USERS,
            null,
            "$COLUMN_ID = ?",
            arrayOf(userId.toString()),
            null,
            null,
            null
        )

        var userProfile: Map<String, String>? = null
        if (cursor.moveToFirst()) {
            userProfile = mapOf(
                "name" to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                "email" to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)),
                "phone" to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHONE))
            )
        }

        cursor.close()
        db.close()
        return userProfile
    }

    fun updateUserProfile(userId: Long, name: String, email: String, phone: String): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, name)
            put(COLUMN_EMAIL, email)
            put(COLUMN_PHONE, phone)
        }

        val result = db.update(TABLE_USERS, values, "$COLUMN_ID = ?", arrayOf(userId.toString()))
        db.close()
        return result > 0
    }

    fun changePassword(userId: Long, currentPassword: String, newPassword: String): Boolean {
        val db = writableDatabase

        // Verify current password
        val cursor = db.query(
            TABLE_USERS,
            arrayOf(COLUMN_PASSWORD),
            "$COLUMN_ID = ?",
            arrayOf(userId.toString()),
            null,
            null,
            null
        )

        var isValid = false
        if (cursor.moveToFirst()) {
            val storedPassword = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD))
            isValid = storedPassword == currentPassword
        }
        cursor.close()

        if (isValid) {
            val values = ContentValues().apply {
                put(COLUMN_PASSWORD, newPassword)
            }
            val result = db.update(TABLE_USERS, values, "$COLUMN_ID = ?", arrayOf(userId.toString()))
            db.close()
            return result > 0
        }

        db.close()
        return false
    }

    fun insertServiceRequest(
        userId: Long, serviceName: String, serviceCategory: String, issueDescription: String,
        serviceMethod: String, userAddress: String?, userPhone: String, estimatedPrice: String,
        estimatedTime: String, pickupTime: String?
    ): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_USER_ID, userId)
            put(COLUMN_SERVICE_NAME, serviceName)
            put(COLUMN_SERVICE_CATEGORY, serviceCategory)
            put(COLUMN_ISSUE_DESCRIPTION, issueDescription)
            put(COLUMN_SERVICE_METHOD, serviceMethod)
            put(COLUMN_USER_ADDRESS, userAddress)
            put(COLUMN_USER_PHONE_REQ, userPhone)
            put(COLUMN_STATUS, "Received")
            put(COLUMN_ESTIMATED_PRICE, estimatedPrice)
            put(COLUMN_ESTIMATED_TIME, estimatedTime)
            put(COLUMN_PICKUP_TIME, pickupTime)
            put(COLUMN_REQUEST_DATE, java.text.SimpleDateFormat("yyyy-MM-dd HH:mm a", java.util.Locale.getDefault()).format(java.util.Date()))
        }

        val requestId = db.insert(TABLE_SERVICE_REQUESTS, null, values)
        db.close()
        return requestId
    }

    fun getUserServiceRequests(userId: Long): List<Map<String, String>> {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_SERVICE_REQUESTS,
            null,
            "$COLUMN_USER_ID = ?",
            arrayOf(userId.toString()),
            null,
            null,
            "$COLUMN_REQUEST_ID DESC"
        )

        val requests = mutableListOf<Map<String, String>>()
        while (cursor.moveToNext()) {
            val request = mutableMapOf<String, String>()
            for (i in 0 until cursor.columnCount) {
                val columnName = cursor.getColumnName(i)
                val value = cursor.getString(i) ?: ""
                request[columnName] = value
            }
            requests.add(request)
        }

        cursor.close()
        db.close()
        return requests
    }

    fun deleteServiceRequest(requestId: Long): Boolean {
        val db = writableDatabase
        val result = db.delete(TABLE_SERVICE_REQUESTS, "$COLUMN_REQUEST_ID = ?", arrayOf(requestId.toString()))
        db.close()
        return result > 0
    }

    fun getUserType(emailOrPhone: String): String {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_USERS,
            arrayOf(COLUMN_USER_TYPE),
            "$COLUMN_EMAIL = ? OR $COLUMN_PHONE = ?",
            arrayOf(emailOrPhone, emailOrPhone),
            null,
            null,
            null
        )

        var userType = "customer"
        if (cursor.moveToFirst()) {
            userType = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_TYPE)) ?: "customer"
        }

        cursor.close()
        db.close()
        return userType
    }

    fun getAllServiceRequests(): List<Map<String, String>> {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_SERVICE_REQUESTS,
            null,
            null,
            null,
            null,
            null,
            "$COLUMN_REQUEST_ID DESC"
        )

        val requests = mutableListOf<Map<String, String>>()
        while (cursor.moveToNext()) {
            val request = mutableMapOf<String, String>()
            for (i in 0 until cursor.columnCount) {
                val columnName = cursor.getColumnName(i)
                val value = cursor.getString(i) ?: ""
                request[columnName] = value
            }
            requests.add(request)
        }

        cursor.close()
        db.close()
        return requests
    }

    fun updateServiceRequestStatus(requestId: Long, status: String, technicianName: String? = null, notes: String? = null): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_STATUS, status)
            if (technicianName != null) {
                put(COLUMN_TECHNICIAN_NAME, technicianName)
            }
            if (notes != null) {
                put(COLUMN_NOTES, notes)
            }
            if (status == "Ready for Pickup" || status == "Completed") {
                put(COLUMN_COMPLETION_DATE, java.text.SimpleDateFormat("yyyy-MM-dd HH:mm a", java.util.Locale.getDefault()).format(java.util.Date()))
            }
        }

        val result = db.update(TABLE_SERVICE_REQUESTS, values, "$COLUMN_REQUEST_ID = ?", arrayOf(requestId.toString()))
        db.close()
        return result > 0
    }
}
