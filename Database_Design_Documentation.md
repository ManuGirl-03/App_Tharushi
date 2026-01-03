# TechCare Services - Database Design Documentation

## Normalized Relational Schema (Third Normal Form - 3NF)

### Overview
This database design follows the principles of normalization to eliminate redundancy and ensure data integrity. All tables are designed to be in Third Normal Form (3NF), which means:

1. **First Normal Form (1NF):** All attributes contain atomic (indivisible) values
2. **Second Normal Form (2NF):** All non-key attributes depend on the entire primary key
3. **Third Normal Form (3NF):** No transitive dependencies exist between non-key attributes

---

## Database Schema SQL Scripts

### 1. Users Table
**Purpose:** Base table for all system users (customers and admins)
**Design Decision:** Single table inheritance pattern to avoid duplication

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

-- Indexes for performance
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_phone ON users(phone);
CREATE INDEX idx_users_type ON users(user_type);
```

**Attributes Explanation:**
- `user_id`: Auto-incrementing primary key
- `email`: Unique identifier for login
- `phone`: Unique contact number
- `password`: Encrypted password for authentication
- `user_type`: Discriminator for customer vs admin
- `is_active`: Soft delete flag

---

### 2. Customers Table
**Purpose:** Extended information specific to customer users
**Design Decision:** Separate table to avoid null values in user table

```sql
CREATE TABLE customers (
    customer_id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    address TEXT,
    preferred_contact TEXT CHECK(preferred_contact IN ('email', 'phone', 'both')) DEFAULT 'both',
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- Indexes
CREATE INDEX idx_customers_user_id ON customers(user_id);
```

**Attributes Explanation:**
- `customer_id`: Primary key for customer-specific data
- `user_id`: Foreign key linking to users table
- `address`: Customer's service address
- `preferred_contact`: How customer prefers to be contacted

---

### 3. Admins Table
**Purpose:** Extended information specific to admin users
**Design Decision:** Separate table for admin-specific attributes

```sql
CREATE TABLE admins (
    admin_id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    department TEXT,
    role TEXT,
    last_login DATETIME,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- Indexes
CREATE INDEX idx_admins_user_id ON admins(user_id);
```

**Attributes Explanation:**
- `admin_id`: Primary key for admin-specific data
- `user_id`: Foreign key linking to users table
- `department`: Admin's department (e.g., "Technical", "Customer Service")
- `role`: Admin's role (e.g., "Manager", "Technician")
- `last_login`: Track admin activity

---

### 4. Services Table
**Purpose:** Catalog of repair services offered by TechCare
**Design Decision:** Separate table to allow dynamic service management

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

-- Indexes for search and filtering
CREATE INDEX idx_services_category ON services(category);
CREATE INDEX idx_services_active ON services(is_active);
CREATE INDEX idx_services_price ON services(price);
```

**Attributes Explanation:**
- `service_id`: Primary key for service identification
- `service_name`: Name of the repair service
- `description`: Detailed description of what's included
- `price`: Service cost in LKR (Sri Lankan Rupees)
- `category`: Service category (e.g., "Mobile", "Laptop", "TV")
- `estimated_days`: Expected completion time
- `is_active`: Soft delete flag for discontinued services

---

### 5. Devices Table
**Purpose:** Customer's device inventory for repair tracking
**Design Decision:** Normalize device information to avoid repetition in bookings

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

-- Indexes
CREATE INDEX idx_devices_customer_id ON devices(customer_id);
CREATE INDEX idx_devices_type ON devices(device_type);
```

**Attributes Explanation:**
- `device_id`: Primary key for device identification
- `customer_id`: Links device to its owner
- `device_type`: Type of device (e.g., "Smartphone", "Laptop")
- `brand`: Device manufacturer (e.g., "Samsung", "Apple")
- `model`: Specific model (e.g., "Galaxy S21", "MacBook Pro")
- `serial_number`: Unique device identifier

---

### 6. Bookings Table (Core Business Entity)
**Purpose:** Central table managing all repair requests and their lifecycle
**Design Decision:** Contains foreign keys to all related entities for complete booking context

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

-- Indexes for efficient querying
CREATE INDEX idx_bookings_customer_id ON bookings(customer_id);
CREATE INDEX idx_bookings_status ON bookings(status);
CREATE INDEX idx_bookings_request_date ON bookings(request_date);
CREATE INDEX idx_bookings_service_id ON bookings(service_id);
```

**Attributes Explanation:**
- `booking_id`: Primary key for booking identification
- `customer_id`: Links to customer who made the booking
- `service_id`: Links to the requested service
- `device_id`: Links to the device being repaired
- `issue_description`: Customer's description of the problem
- `service_type`: Whether customer wants pickup or will drop-off
- `status`: Current booking status in the repair workflow
- `total_cost`: Final cost including any additional charges
- `scheduled_date`: When service is scheduled to start
- `completion_date`: When repair was completed
- `technician_notes`: Internal notes from repair technician

---

### 7. FAQs Table
**Purpose:** Store frequently asked questions for customer self-service
**Design Decision:** Separate table to allow dynamic FAQ management

```sql
CREATE TABLE faqs (
    faq_id INTEGER PRIMARY KEY AUTOINCREMENT,
    question TEXT NOT NULL,
    answer TEXT NOT NULL,
    category TEXT,
    is_active BOOLEAN DEFAULT 1,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- Indexes
CREATE INDEX idx_faqs_category ON faqs(category);
CREATE INDEX idx_faqs_active ON faqs(is_active);
```

**Attributes Explanation:**
- `faq_id`: Primary key for FAQ identification
- `question`: Customer's question
- `answer`: Detailed answer/solution
- `category`: FAQ category for organization
- `is_active`: Flag to hide outdated FAQs

---

### 8. Contact Support Table
**Purpose:** Track customer support requests and responses
**Design Decision:** Separate table to maintain support ticket history

```sql
CREATE TABLE contact_support (
    contact_id INTEGER PRIMARY KEY AUTOINCREMENT,
    customer_id INTEGER NOT NULL,
    subject TEXT NOT NULL,
    message TEXT NOT NULL,
    status TEXT CHECK(status IN ('open', 'in_progress', 'resolved', 'closed')) DEFAULT 'open',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    resolved_at DATETIME,
    admin_response TEXT,
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE CASCADE
);

-- Indexes
CREATE INDEX idx_contact_customer_id ON contact_support(customer_id);
CREATE INDEX idx_contact_status ON contact_support(status);
CREATE INDEX idx_contact_created_at ON contact_support(created_at);
```

**Attributes Explanation:**
- `contact_id`: Primary key for support ticket identification
- `customer_id`: Links to customer who submitted the request
- `subject`: Brief summary of the issue
- `message`: Detailed description of the problem
- `status`: Current status of the support ticket
- `resolved_at`: Timestamp when issue was resolved
- `admin_response`: Admin's response to the customer

---

## Sample Data for Testing

### Default Admin Account
```sql
INSERT INTO users (email, phone, password, name, user_type) 
VALUES ('admin@techcare.com', '+94771234567', 'admin123', 'System Administrator', 'admin');

INSERT INTO admins (user_id, department, role) 
VALUES (1, 'Technical', 'System Administrator');
```

### Sample Services
```sql
INSERT INTO services (service_name, description, price, category, estimated_days) VALUES
('Mobile Screen Replacement', 'Complete screen replacement for smartphones', 8500.00, 'Mobile', 1),
('Laptop Battery Replacement', 'Replace faulty laptop battery with genuine parts', 12000.00, 'Laptop', 2),
('TV Display Repair', 'Fix display issues including dead pixels and color problems', 25000.00, 'Television', 3),
('AC Cooling Issue Fix', 'Repair cooling problems and gas refill', 15000.00, 'Air Conditioner', 2),
('Refrigerator Compressor Repair', 'Fix compressor and cooling system issues', 20000.00, 'Refrigerator', 4),
('Washing Machine Drum Repair', 'Fix drum rotation and washing issues', 18000.00, 'Washing Machine', 3);
```

### Sample FAQs
```sql
INSERT INTO faqs (question, answer, category) VALUES
('How long does a typical repair take?', 'Most repairs are completed within 1-5 business days depending on the complexity and parts availability.', 'General'),
('Do you provide warranty on repairs?', 'Yes, we provide 6 months warranty on all repairs and replaced parts.', 'Warranty'),
('Can I track my repair status?', 'Yes, you can track your repair status in real-time through our mobile app.', 'Tracking'),
('What payment methods do you accept?', 'We accept cash, bank transfer, and mobile payment options.', 'Payment'),
('Do you offer home pickup service?', 'Yes, we offer free home pickup service within Colombo city limits.', 'Pickup');
```

---

## Database Relationships Summary

### Primary Relationships:
1. **Users → Customers/Admins**: One-to-One (Inheritance)
2. **Customers → Devices**: One-to-Many (A customer can own multiple devices)
3. **Customers → Bookings**: One-to-Many (A customer can have multiple repair requests)
4. **Services → Bookings**: One-to-Many (A service can be used in multiple bookings)
5. **Devices → Bookings**: One-to-Many (A device can have multiple repair history)
6. **Customers → Contact Support**: One-to-Many (A customer can submit multiple support requests)

### Key Business Rules:
- A user can only be either a customer OR an admin (enforced by user_type constraint)
- A booking must reference existing customer, service, and device
- Device deletion cascades to related bookings (maintains referential integrity)
- Service costs are stored in Sri Lankan Rupees (LKR)
- All timestamps use ISO format for consistency

### Performance Optimizations:
- Strategic indexing on frequently queried columns
- Foreign key constraints for referential integrity
- Check constraints for data validation
- Default values to reduce null handling

This normalized database design ensures data integrity, eliminates redundancy, and provides a solid foundation for the TechCare Services mobile application.
