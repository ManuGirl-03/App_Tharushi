# TechCare Services Mobile Application - Technical Documentation

## 📋 Project Overview

**Application Name:** TechCare Services Mobile App  
**Version:** 1.0  
**Platform:** Android (API Level 21+)  
**Development Language:** Kotlin  
**Database:** SQLite  
**Architecture Pattern:** MVVM (Model-View-ViewModel)  
**Minimum SDK:** 21 (Android 5.0)  
**Target SDK:** 33 (Android 13)  

---

## 🏗️ System Architecture

### High-Level Architecture
The TechCare Services application follows a layered architecture pattern with clear separation of concerns:

```
┌─────────────────────────────────────┐
│        Presentation Layer           │
│  (Activities, Fragments, Adapters)  │
├─────────────────────────────────────┤
│         Business Logic Layer        │
│     (ViewModels, Use Cases)         │
├─────────────────────────────────────┤
│         Data Access Layer           │
│    (Repository, DAO, Models)        │
├─────────────────────────────────────┤
│         Data Storage Layer          │
│          (SQLite Database)          │
└─────────────────────────────────────┘
```

### Architecture Components Used
- **ViewModel:** Manages UI-related data lifecycle-aware
- **LiveData:** Observable data holder for UI updates
- **Repository Pattern:** Abstracts data source access
- **DAO Pattern:** Database access objects for SQL operations
- **Singleton Pattern:** Database helper and shared preferences

---

## 📁 Project Structure

### Directory Organization
```
app/
├── src/main/
│   ├── java/com/example/myapplication/
│   │   ├── activities/
│   │   │   ├── LoginActivity.kt
│   │   │   ├── RegisterActivity.kt
│   │   │   ├── MainActivity.kt (Dashboard)
│   │   │   ├── ProfileActivity.kt
│   │   │   ├── ServicesActivity.kt
│   │   │   ├── BookingActivity.kt
│   │   │   ├── BookingDetailActivity.kt
│   │   │   ├── ChangePasswordActivity.kt
│   │   │   ├── SupportActivity.kt
│   │   │   ├── FaqActivity.kt
│   │   │   └── AdminDashboardActivity.kt
│   │   ├── models/
│   │   │   ├── User.kt
│   │   │   ├── Customer.kt
│   │   │   ├── Admin.kt
│   │   │   ├── Service.kt
│   │   │   ├── Device.kt
│   │   │   ├── Booking.kt
│   │   │   ├── FAQ.kt
│   │   │   └── ContactSupport.kt
│   │   ├── database/
│   │   │   ├── DatabaseHelper.kt
│   │   │   ├── UserDAO.kt
│   │   │   ├── ServiceDAO.kt
│   │   │   ├── BookingDAO.kt
│   │   │   └── DeviceDAO.kt
│   │   ├── adapters/
│   │   │   ├── ServicesAdapter.kt
│   │   │   ├── BookingsAdapter.kt
│   │   │   ├── DevicesAdapter.kt
│   │   │   └── FAQAdapter.kt
│   │   ├── utils/
│   │   │   ├── Constants.kt
│   │   │   ├── ValidationUtils.kt
│   │   │   ├── DateUtils.kt
│   │   │   └── PreferenceManager.kt
│   │   └── MainActivity.kt
│   ├── res/
│   │   ├── layout/
│   │   ├── values/
│   │   ├── drawable/
│   │   └── mipmap/
│   └── AndroidManifest.xml
├── build.gradle.kts
└── proguard-rules.pro
```

---

## 🗃️ Database Design

### Database Schema Version: 1

#### Table Structures

**1. Users Table**
```sql
CREATE TABLE users (
    user_id INTEGER PRIMARY KEY AUTOINCREMENT,
    email TEXT UNIQUE NOT NULL,
    phone TEXT UNIQUE NOT NULL,
    password TEXT NOT NULL,
    name TEXT NOT NULL,
    user_type TEXT CHECK(user_type IN ('customer', 'admin')) NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT 1
);
```

**2. Customers Table**
```sql
CREATE TABLE customers (
    customer_id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    address TEXT,
    preferred_contact TEXT CHECK(preferred_contact IN ('email', 'phone', 'both')),
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);
```

**3. Services Table**
```sql
CREATE TABLE services (
    service_id INTEGER PRIMARY KEY AUTOINCREMENT,
    service_name TEXT NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    category TEXT NOT NULL,
    estimated_days INTEGER DEFAULT 1,
    is_active BOOLEAN DEFAULT 1,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP
);
```

**4. Devices Table**
```sql
CREATE TABLE devices (
    device_id INTEGER PRIMARY KEY AUTOINCREMENT,
    customer_id INTEGER NOT NULL,
    device_type TEXT NOT NULL,
    brand TEXT,
    model TEXT,
    serial_number TEXT,
    added_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE CASCADE
);
```

**5. Bookings Table**
```sql
CREATE TABLE bookings (
    booking_id INTEGER PRIMARY KEY AUTOINCREMENT,
    customer_id INTEGER NOT NULL,
    service_id INTEGER NOT NULL,
    device_id INTEGER NOT NULL,
    issue_description TEXT NOT NULL,
    service_type TEXT CHECK(service_type IN ('pickup', 'drop-off')) NOT NULL,
    status TEXT CHECK(status IN ('pending', 'confirmed', 'in_progress', 'completed', 'cancelled')) DEFAULT 'pending',
    total_cost DECIMAL(10,2),
    request_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    scheduled_date DATETIME,
    completion_date DATETIME,
    technician_notes TEXT,
    payment_status TEXT CHECK(payment_status IN ('pending', 'paid', 'refunded')) DEFAULT 'pending',
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE CASCADE,
    FOREIGN KEY (service_id) REFERENCES services(service_id),
    FOREIGN KEY (device_id) REFERENCES devices(device_id)
);
```

### Database Helper Implementation

#### DatabaseHelper.kt Key Methods
```kotlin
class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    
    companion object {
        private const val DATABASE_NAME = "techcare_services.db"
        private const val DATABASE_VERSION = 1
        
        @Volatile
        private var INSTANCE: DatabaseHelper? = null
        
        fun getInstance(context: Context): DatabaseHelper {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: DatabaseHelper(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
    
    override fun onCreate(db: SQLiteDatabase) {
        // Create all tables
        createUsersTable(db)
        createCustomersTable(db)
        createServicesTable(db)
        createDevicesTable(db)
        createBookingsTable(db)
        createFAQsTable(db)
        createContactSupportTable(db)
        
        // Insert default data
        insertDefaultServices(db)
        insertDefaultAdmin(db)
        insertDefaultFAQs(db)
    }
    
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Handle database schema updates
        when (oldVersion) {
            1 -> {
                // Upgrade from version 1 to 2
                // Add migration logic here
            }
        }
    }
}
```

---

## 🔐 Authentication System

### Password Security
- **Hashing Algorithm:** BCrypt with salt
- **Minimum Requirements:** 8 characters, letters and numbers
- **Session Management:** Shared preferences with timeout
- **Failed Login Protection:** 3 attempts lockout

#### Authentication Implementation
```kotlin
object AuthManager {
    private const val PREF_NAME = "techcare_auth"
    private const val KEY_USER_ID = "user_id"
    private const val KEY_USER_TYPE = "user_type"
    private const val KEY_LOGIN_TIME = "login_time"
    private const val SESSION_TIMEOUT = 24 * 60 * 60 * 1000 // 24 hours
    
    fun login(email: String, password: String, context: Context): AuthResult {
        val dbHelper = DatabaseHelper.getInstance(context)
        val hashedPassword = hashPassword(password)
        
        return dbHelper.authenticateUser(email, hashedPassword)
    }
    
    fun isUserLoggedIn(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val loginTime = prefs.getLong(KEY_LOGIN_TIME, 0)
        val currentTime = System.currentTimeMillis()
        
        return (currentTime - loginTime) < SESSION_TIMEOUT
    }
    
    private fun hashPassword(password: String): String {
        return BCrypt.hashpw(password, BCrypt.gensalt())
    }
}
```

---

## 📱 Activity Lifecycle Management

### Activity Flow Diagram
```
LoginActivity → RegisterActivity
     ↓
MainActivity (Dashboard)
     ├── ProfileActivity
     ├── ServicesActivity → BookingActivity
     ├── BookingDetailActivity
     ├── ChangePasswordActivity
     ├── SupportActivity
     ├── FaqActivity
     └── AdminDashboardActivity (Admin only)
```

### Key Activities Implementation

#### MainActivity.kt (Dashboard)
```kotlin
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var dbHelper: DatabaseHelper
    private var currentUser: User? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        dbHelper = DatabaseHelper.getInstance(this)
        
        checkAuthenticationStatus()
        setupNavigationView()
        loadDashboardData()
    }
    
    private fun checkAuthenticationStatus() {
        if (!AuthManager.isUserLoggedIn(this)) {
            redirectToLogin()
            return
        }
        
        currentUser = AuthManager.getCurrentUser(this)
        setupUserInterface()
    }
    
    private fun setupNavigationView() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> showHomeFragment()
                R.id.nav_services -> showServicesFragment()
                R.id.nav_bookings -> showBookingsFragment()
                R.id.nav_profile -> showProfileFragment()
                else -> false
            }
        }
    }
}
```

---

## 🔄 Data Flow Architecture

### MVVM Pattern Implementation

#### ViewModel Example (BookingViewModel.kt)
```kotlin
class BookingViewModel(private val repository: BookingRepository) : ViewModel() {
    
    private val _bookings = MutableLiveData<List<Booking>>()
    val bookings: LiveData<List<Booking>> = _bookings
    
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading
    
    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error
    
    fun loadCustomerBookings(customerId: Int) {
        _loading.value = true
        
        viewModelScope.launch {
            try {
                val result = repository.getBookingsByCustomer(customerId)
                _bookings.value = result
                _loading.value = false
            } catch (e: Exception) {
                _error.value = "Failed to load bookings: ${e.message}"
                _loading.value = false
            }
        }
    }
    
    fun createBooking(booking: Booking) {
        viewModelScope.launch {
            try {
                val bookingId = repository.insertBooking(booking)
                if (bookingId > 0) {
                    // Refresh bookings list
                    loadCustomerBookings(booking.customerId)
                }
            } catch (e: Exception) {
                _error.value = "Failed to create booking: ${e.message}"
            }
        }
    }
}
```

#### Repository Pattern (BookingRepository.kt)
```kotlin
class BookingRepository(private val bookingDAO: BookingDAO) {
    
    suspend fun getBookingsByCustomer(customerId: Int): List<Booking> {
        return withContext(Dispatchers.IO) {
            bookingDAO.getBookingsByCustomer(customerId)
        }
    }
    
    suspend fun insertBooking(booking: Booking): Long {
        return withContext(Dispatchers.IO) {
            bookingDAO.insertBooking(booking)
        }
    }
    
    suspend fun updateBookingStatus(bookingId: Int, status: String): Boolean {
        return withContext(Dispatchers.IO) {
            bookingDAO.updateBookingStatus(bookingId, status) > 0
        }
    }
}
```

---

## 🎨 UI/UX Implementation

### Material Design Components Used
- **Material Theme:** Custom color scheme for TechCare branding
- **RecyclerView:** For lists (services, bookings, devices)
- **CardView:** For item containers and information display
- **TextInputLayout:** For form inputs with validation
- **FloatingActionButton:** For primary actions
- **BottomNavigationView:** For main navigation
- **Toolbar:** For screen titles and actions

### Custom Styling
```xml
<!-- res/values/colors.xml -->
<resources>
    <color name="primary_color">#4CAF50</color>
    <color name="primary_dark">#45a049</color>
    <color name="accent_color">#FF9800</color>
    <color name="background_color">#F5F5F5</color>
    <color name="surface_color">#FFFFFF</color>
    <color name="error_color">#F44336</color>
    <color name="text_primary">#212121</color>
    <color name="text_secondary">#757575</color>
</resources>
```

### Responsive Design
- **Multiple Screen Sizes:** Layout optimized for phones and tablets
- **Orientation Support:** Portrait and landscape modes
- **Accessibility:** Content descriptions, proper focus handling
- **Font Scaling:** Support for user-defined text sizes

---

## 🔧 Utility Classes and Helper Functions

### ValidationUtils.kt
```kotlin
object ValidationUtils {
    
    fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
    
    fun isValidPhone(phone: String): Boolean {
        val phonePattern = "^\\+94[0-9]{9}$".toRegex()
        return phonePattern.matches(phone)
    }
    
    fun isValidPassword(password: String): Boolean {
        return password.length >= 8 && 
               password.any { it.isLetter() } && 
               password.any { it.isDigit() }
    }
    
    fun validateBookingData(booking: Booking): ValidationResult {
        val errors = mutableListOf<String>()
        
        if (booking.issueDescription.isBlank()) {
            errors.add("Issue description is required")
        }
        
        if (booking.serviceType.isBlank()) {
            errors.add("Service type must be selected")
        }
        
        return ValidationResult(errors.isEmpty(), errors)
    }
}
```

### DateUtils.kt
```kotlin
object DateUtils {
    private const val DATE_FORMAT = "yyyy-MM-dd HH:mm:ss"
    private const val DISPLAY_DATE_FORMAT = "MMM dd, yyyy"
    private const val DISPLAY_TIME_FORMAT = "hh:mm a"
    
    fun getCurrentTimestamp(): String {
        val sdf = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
        return sdf.format(Date())
    }
    
    fun formatDateForDisplay(dateString: String): String {
        try {
            val inputFormat = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
            val outputFormat = SimpleDateFormat(DISPLAY_DATE_FORMAT, Locale.getDefault())
            val date = inputFormat.parse(dateString)
            return outputFormat.format(date ?: Date())
        } catch (e: ParseException) {
            return dateString
        }
    }
    
    fun isDateInFuture(dateString: String): Boolean {
        try {
            val sdf = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
            val date = sdf.parse(dateString)
            return date?.after(Date()) ?: false
        } catch (e: ParseException) {
            return false
        }
    }
}
```

---

## 📊 Performance Optimization

### Database Optimization
- **Indexes:** Strategic indexing on frequently queried columns
- **Connection Pooling:** Singleton database helper instance
- **Query Optimization:** Efficient SQL queries with proper WHERE clauses
- **Lazy Loading:** Load data only when needed

### Memory Management
- **ViewBinding:** Replaces findViewById for better performance
- **Image Loading:** Efficient bitmap handling and recycling
- **Resource Management:** Proper cleanup in onDestroy()
- **Background Tasks:** Use of Coroutines for non-blocking operations

### Battery Optimization
- **Background Tasks:** Minimal background processing
- **Network Calls:** Efficient data fetching strategies
- **Wake Lock Management:** Proper handling of device wake states

---

## 🔒 Security Implementation

### Data Protection
```kotlin
object SecurityManager {
    
    fun encryptSensitiveData(data: String): String {
        // Implementation of encryption for sensitive user data
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val keyGen = KeyGenerator.getInstance("AES")
        keyGen.init(256)
        val secretKey = keyGen.generateKey()
        
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        val encryptedData = cipher.doFinal(data.toByteArray())
        
        return Base64.encodeToString(encryptedData, Base64.DEFAULT)
    }
    
    fun sanitizeInput(input: String): String {
        return input.replace(Regex("[<>\"'%;()&+]"), "")
    }
    
    fun validateSession(context: Context): Boolean {
        return AuthManager.isUserLoggedIn(context) && 
               !hasSecurityViolation(context)
    }
}
```

### SQL Injection Prevention
- **Prepared Statements:** All database queries use parameterized statements
- **Input Sanitization:** User inputs are validated and sanitized
- **Parameter Binding:** Use of SQLite parameter binding for safe queries

---

## 🧪 Testing Strategy

### Unit Testing Framework
```kotlin
// Example Unit Test for ValidationUtils
@Test
fun testEmailValidation() {
    assertTrue(ValidationUtils.isValidEmail("test@example.com"))
    assertFalse(ValidationUtils.isValidEmail("invalid-email"))
    assertFalse(ValidationUtils.isValidEmail(""))
}

@Test
fun testPhoneValidation() {
    assertTrue(ValidationUtils.isValidPhone("+94771234567"))
    assertFalse(ValidationUtils.isValidPhone("1234567890"))
    assertFalse(ValidationUtils.isValidPhone("+1234567890"))
}
```

### Integration Testing
- **Database Operations:** Test CRUD operations
- **Authentication Flow:** Test login/logout processes
- **Business Logic:** Test booking creation and management

### UI Testing with Espresso
```kotlin
@Test
fun testLoginFlow() {
    onView(withId(R.id.etEmail))
        .perform(typeText("test@example.com"))
    
    onView(withId(R.id.etPassword))
        .perform(typeText("password123"))
    
    onView(withId(R.id.btnLogin))
        .perform(click())
    
    onView(withId(R.id.tvWelcome))
        .check(matches(isDisplayed()))
}
```

---

## 🚀 Build Configuration

### Gradle Configuration (build.gradle.kts)
```kotlin
android {
    compileSdk 33
    
    defaultConfig {
        applicationId "com.techcare.services"
        minSdk 21
        targetSdk 33
        versionCode 1
        versionName "1.0"
        
        testInstrumentationRunner "androidx.test.runner.AndroidJUnitRunner"
    }
    
    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isMinifyEnabled = false
            isDebuggable = true
        }
    }
    
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.0")
    implementation("com.google.android.material:material:1.7.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")
    
    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}
```

---

## 📦 Deployment and Distribution

### APK Generation
1. **Debug Build:** For development and testing
2. **Release Build:** Optimized for production
3. **Code Obfuscation:** ProGuard rules for code protection
4. **Signing Configuration:** Digital signing for app authenticity

### Distribution Channels
- **Internal Testing:** Direct APK installation
- **Google Play Store:** Production release (future)
- **Enterprise Distribution:** Corporate app stores

---

## 🔧 Maintenance and Updates

### Version Control
- **Git Repository:** Source code management
- **Branching Strategy:** Feature branches, develop, and main
- **Code Review Process:** Pull request reviews before merge

### Update Strategy
- **Database Migrations:** Handled in onUpgrade() method
- **Feature Flags:** Toggle new features for gradual rollout
- **Backward Compatibility:** Support for older app versions

### Monitoring and Logging
```kotlin
object Logger {
    private const val TAG = "TechCareApp"
    
    fun logInfo(message: String) {
        Log.i(TAG, message)
    }
    
    fun logError(message: String, throwable: Throwable? = null) {
        Log.e(TAG, message, throwable)
    }
    
    fun logDebug(message: String) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, message)
        }
    }
}
```

---

## 🚨 Error Handling and Exception Management

### Global Exception Handling
```kotlin
class GlobalExceptionHandler : Thread.UncaughtExceptionHandler {
    
    override fun uncaughtException(t: Thread, e: Throwable) {
        Logger.logError("Uncaught exception in thread ${t.name}", e)
        
        // Save crash report
        saveCrashReport(e)
        
        // Restart application
        restartApplication()
    }
    
    private fun saveCrashReport(exception: Throwable) {
        // Implementation to save crash details
    }
    
    private fun restartApplication() {
        // Implementation to restart app gracefully
    }
}
```

### User-Friendly Error Messages
- **Network Errors:** "Please check your internet connection"
- **Database Errors:** "Unable to save data. Please try again"
- **Validation Errors:** Specific field-level error messages
- **Authentication Errors:** "Invalid login credentials"

---

## 📚 API Documentation (Future Enhancement)

### RESTful API Design (if backend is added)
```
Authentication:
POST /api/auth/login
POST /api/auth/register
POST /api/auth/logout

Services:
GET /api/services
GET /api/services/{category}
GET /api/services/{id}

Bookings:
GET /api/bookings/customer/{customerId}
POST /api/bookings
PUT /api/bookings/{id}
DELETE /api/bookings/{id}

Admin:
GET /api/admin/bookings
PUT /api/admin/bookings/{id}/status
GET /api/admin/services
POST /api/admin/services
```

---

## 🔍 Code Quality Standards

### Kotlin Coding Standards
- **Naming Conventions:** CamelCase for functions, PascalCase for classes
- **Code Organization:** Logical grouping of functions and properties
- **Documentation:** KDoc comments for public APIs
- **Null Safety:** Proper use of nullable and non-null types

### Code Review Checklist
- ✅ Proper error handling
- ✅ Input validation
- ✅ Memory leak prevention
- ✅ Performance optimization
- ✅ Security considerations
- ✅ Test coverage
- ✅ Documentation updates

---

## 📞 Technical Support

### Development Team Contacts
- **Lead Developer:** +94 77 XXX XXXX
- **Database Architect:** +94 77 XXX XXXX
- **UI/UX Developer:** +94 77 XXX XXXX

### Technical Documentation Updates
This documentation is maintained alongside the codebase. For technical questions or clarifications:

1. Check inline code comments
2. Review unit test cases
3. Consult system design documentation
4. Contact the development team

---

## 📈 Performance Metrics

### Key Performance Indicators
- **App Launch Time:** < 3 seconds
- **Database Query Time:** < 100ms average
- **Memory Usage:** < 150MB peak usage
- **Crash Rate:** < 0.1% of sessions
- **Battery Usage:** Minimal background consumption

### Monitoring Tools
- **Android Profiler:** Memory and CPU monitoring
- **Database Inspector:** SQLite query analysis
- **Layout Inspector:** UI performance analysis

---

**Technical Documentation Version:** 1.0  
**Last Updated:** January 3, 2026  
**Next Review Date:** March 3, 2026  

*This document serves as the complete technical reference for the TechCare Services mobile application. For updates or technical queries, please contact the development team.*
