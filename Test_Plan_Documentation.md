# TechCare Services Mobile Application - Test Plan

## 1. Test Plan Overview

### 1.1 Introduction
This document outlines the comprehensive testing strategy for the TechCare Services mobile application, which allows customers to request repair services for electronic devices and home appliances.

### 1.2 Test Objectives
- Verify user authentication and profile management functionality
- Validate service browsing and booking submission processes
- Ensure booking management and tracking features work correctly
- Test admin dashboard functionality
- Verify database operations and data integrity
- Ensure proper error handling and user experience

### 1.3 Scope of Testing
**In Scope:**
- User registration and login functionality
- Customer profile management
- Service browsing and selection
- Booking creation and management
- Admin dashboard operations
- Database CRUD operations
- Input validation and error handling
- Navigation between screens

**Out of Scope:**
- Performance testing under heavy load
- Security penetration testing
- Third-party payment gateway integration
- SMS/Email notification services
- Image upload functionality (if not implemented)

### 1.4 Test Environment
- **Platform:** Android (API Level 21+)
- **Database:** SQLite
- **Testing Device:** Android Emulator/Physical Device
- **Test Data:** Predefined test datasets
- **Testing Period:** January 3-10, 2026

### 1.5 Test Approach
- **Manual Testing:** Functional testing of user interfaces
- **Database Testing:** Verification of data operations
- **Integration Testing:** Testing component interactions
- **User Experience Testing:** Navigation and usability validation

### 1.6 Test Deliverables
- Test cases with execution results
- Test data sets
- Defect reports (if any)
- Test completion report

---

## 2. Test Data Sets

### 2.1 Valid User Test Data
```
Customer Accounts:
- Email: john.doe@email.com, Phone: +94771234567, Password: Test@123, Name: John Doe
- Email: mary.jane@email.com, Phone: +94779876543, Password: Mary@456, Name: Mary Jane
- Email: customer1@test.com, Phone: +94712345678, Password: Customer@1, Name: Test Customer

Admin Account:
- Email: admin@techcare.com, Phone: +94771111111, Password: admin123, Name: System Admin
```

### 2.2 Service Test Data
```
Services Available:
- Mobile Screen Replacement - Rs. 8,500 - Mobile Category
- Laptop Battery Replacement - Rs. 12,000 - Laptop Category  
- TV Display Repair - Rs. 25,000 - Television Category
- AC Cooling Issue Fix - Rs. 15,000 - Air Conditioner Category
```

### 2.3 Device Test Data
```
Customer Devices:
- Samsung Galaxy S21, Smartphone, Serial: SN123456789
- Apple MacBook Pro, Laptop, Serial: MB987654321
- Sony Bravia TV, Television, Serial: TV456789123
```

### 2.4 Invalid Test Data
```
Invalid Credentials:
- Email: invalid@email, Phone: 123, Password: weak
- Email: test@test.com, Phone: +94771234567, Password: "" (empty)
- Email: "", Phone: "", Password: "" (all empty)
```

---

## 3. Test Cases

### Test Case 1: User Registration
| Field | Details |
|-------|---------|
| **Test ID** | TC001 |
| **Test Name** | Customer Registration with Valid Data |
| **Test Data** | Email: newuser@test.com<br>Phone: +94712223334<br>Password: NewUser@123<br>Name: New User<br>Address: 123 Colombo Street |
| **Expected Result** | User account created successfully<br>Navigation to login screen<br>Success message displayed<br>User data saved to database |
| **Actual Result** | _To be filled during execution_ |
| **Remarks** | _To be filled during execution_ |

### Test Case 2: User Login Authentication
| Field | Details |
|-------|---------|
| **Test ID** | TC002 |
| **Test Name** | Customer Login with Valid Credentials |
| **Test Data** | Email: john.doe@email.com<br>Password: Test@123 |
| **Expected Result** | Successful login<br>Navigation to dashboard<br>User session established<br>Welcome message displayed |
| **Actual Result** | _To be filled during execution_ |
| **Remarks** | _To be filled during execution_ |

### Test Case 3: Invalid Login Attempt
| Field | Details |
|-------|---------|
| **Test ID** | TC003 |
| **Test Name** | Login with Invalid Credentials |
| **Test Data** | Email: wrong@email.com<br>Password: wrongpassword |
| **Expected Result** | Login failed<br>Error message: "Invalid email or password"<br>User remains on login screen<br>No navigation to dashboard |
| **Actual Result** | _To be filled during execution_ |
| **Remarks** | _To be filled during execution_ |

### Test Case 4: Service Browsing
| Field | Details |
|-------|---------|
| **Test ID** | TC004 |
| **Test Name** | Browse Available Services by Category |
| **Test Data** | User logged in as: john.doe@email.com<br>Navigate to Services<br>Filter by: "Mobile" category |
| **Expected Result** | Services displayed correctly<br>Mobile category services shown<br>Service details visible (name, price, description)<br>Prices shown in LKR/Rs. |
| **Actual Result** | _To be filled during execution_ |
| **Remarks** | _To be filled during execution_ |

### Test Case 5: Booking Creation
| Field | Details |
|-------|---------|
| **Test ID** | TC005 |
| **Test Name** | Submit Repair Request with Complete Information |
| **Test Data** | Service: Mobile Screen Replacement<br>Device: Samsung Galaxy S21<br>Issue: "Screen cracked after drop"<br>Service Type: Home Pickup<br>Scheduled Date: Tomorrow |
| **Expected Result** | Booking created successfully<br>Booking ID generated<br>Status set to "Pending"<br>Confirmation message displayed<br>Data saved to database |
| **Actual Result** | _To be filled during execution_ |
| **Remarks** | _To be filled during execution_ |

### Test Case 6: Booking Status Tracking
| Field | Details |
|-------|---------|
| **Test ID** | TC006 |
| **Test Name** | View Booking Status and Details |
| **Test Data** | User: john.doe@email.com<br>Booking ID: From TC005<br>Navigate to "My Bookings" |
| **Expected Result** | Booking details displayed<br>Current status visible<br>Service information shown<br>Device details displayed<br>Cost information visible |
| **Actual Result** | _To be filled during execution_ |
| **Remarks** | _To be filled during execution_ |

### Test Case 7: Profile Management
| Field | Details |
|-------|---------|
| **Test ID** | TC007 |
| **Test Name** | Update Customer Profile Information |
| **Test Data** | User: john.doe@email.com<br>New Name: "John Updated Doe"<br>New Phone: "+94712345999"<br>New Address: "456 Updated Street" |
| **Expected Result** | Profile updated successfully<br>Success message displayed<br>Updated data saved to database<br>Changes reflected in profile screen |
| **Actual Result** | _To be filled during execution_ |
| **Remarks** | _To be filled during execution_ |

### Test Case 8: Password Change
| Field | Details |
|-------|---------|
| **Test ID** | TC008 |
| **Test Name** | Change User Password |
| **Test Data** | User: mary.jane@email.com<br>Current Password: Mary@456<br>New Password: NewMary@789<br>Confirm Password: NewMary@789 |
| **Expected Result** | Password changed successfully<br>Success message displayed<br>New password saved to database<br>User can login with new password |
| **Actual Result** | _To be filled during execution_ |
| **Remarks** | _To be filled during execution_ |

### Test Case 9: Admin Dashboard Access
| Field | Details |
|-------|---------|
| **Test ID** | TC009 |
| **Test Name** | Admin Login and Dashboard Access |
| **Test Data** | Email: admin@techcare.com<br>Password: admin123 |
| **Expected Result** | Admin login successful<br>Admin dashboard displayed<br>Admin-specific menu options visible<br>Customer bookings list accessible<br>Service management options available |
| **Actual Result** | _To be filled during execution_ |
| **Remarks** | _To be filled during execution_ |

### Test Case 10: Booking Cancellation
| Field | Details |
|-------|---------|
| **Test ID** | TC010 |
| **Test Name** | Cancel Existing Booking |
| **Test Data** | User: john.doe@email.com<br>Booking ID: Active booking<br>Action: Cancel booking |
| **Expected Result** | Booking status changed to "Cancelled"<br>Confirmation dialog displayed<br>Cancellation confirmed<br>Booking removed from active bookings<br>Database updated with new status |
| **Actual Result** | _To be filled during execution_ |
| **Remarks** | _To be filled during execution_ |

---

## 4. Test Execution Guidelines

### 4.1 Pre-Execution Setup
1. Install the TechCare Services application on test device
2. Ensure database is initialized with test data
3. Verify network connectivity (if applicable)
4. Clear application data to start with clean state

### 4.2 Test Execution Process
1. **Prepare Test Environment:** Set up devices and test data
2. **Execute Test Cases:** Follow test steps in sequential order
3. **Record Results:** Document actual results and any deviations
4. **Log Defects:** Report any bugs or issues found
5. **Cleanup:** Reset application state between test cases

### 4.3 Pass/Fail Criteria
- **PASS:** Actual result matches expected result completely
- **FAIL:** Any deviation from expected result
- **BLOCKED:** Unable to execute due to previous test failure
- **SKIP:** Test case not applicable in current build

### 4.4 Defect Reporting
When a test case fails, report defects with:
- Test Case ID
- Steps to reproduce
- Expected vs Actual result
- Screenshots (if applicable)
- Device and OS information
- Severity level (Critical/High/Medium/Low)

---

## 5. Test Schedule

| Phase | Duration | Activities |
|-------|----------|------------|
| Test Preparation | Jan 3, 2026 | Setup test environment, prepare test data |
| Test Execution | Jan 4-7, 2026 | Execute all test cases, record results |
| Defect Resolution | Jan 8-9, 2026 | Report and fix any issues found |
| Test Completion | Jan 10, 2026 | Final verification, test report |

---

## 6. Test Completion Criteria
Testing will be considered complete when:
- All 10 test cases have been executed
- All critical and high severity defects are resolved
- Application meets functional requirements
- Database operations work correctly
- User experience is acceptable

---

## 7. Risk Assessment

| Risk | Impact | Mitigation Strategy |
|------|--------|-------------------|
| Database corruption | High | Regular backups, data validation |
| Authentication bypass | High | Thorough security testing |
| Data loss during operations | Medium | Transaction management, rollback capabilities |
| Poor user experience | Medium | Usability testing, user feedback |
| Performance issues | Low | Basic performance validation |

---

## 8. Test Tools and Resources

### 8.1 Tools Required
- Android Studio for application debugging
- SQLite Database Browser for database verification
- Screen recording software for issue reproduction
- Test management tool (Excel/Google Sheets) for tracking

### 8.2 Human Resources
- Test Lead: 1 person
- Test Executors: 2 people
- Developer (for defect fixes): 1 person

### 8.3 Test Environment Requirements
- Android device/emulator (API 21+)
- Test data sets loaded in database
- Network connectivity (if required)
- Storage space for test artifacts

---

## 9. Test Metrics

The following metrics will be tracked:
- **Test Case Coverage:** Number of test cases executed / Total test cases
- **Pass Rate:** Number of passed test cases / Total executed test cases
- **Defect Density:** Number of defects / Total test cases
- **Test Execution Progress:** Daily progress tracking

---

## 10. Conclusion

This test plan provides comprehensive coverage of the TechCare Services mobile application's core functionality. The 10 test cases cover critical user flows from registration to booking management, ensuring the application meets its functional requirements and provides a quality user experience.

Regular execution of these test cases will help maintain application quality throughout the development lifecycle and ensure customer satisfaction with the TechCare Services platform.
