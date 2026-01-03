# TechCare Services Mobile App

A simple Android application for TechCare Services - Electronic Device Repair & Service company.

## Features Implemented

### 1. User Authentication System
- **Login Screen**: Users can log in using email or phone number with password
- **Registration Screen**: New users can create accounts with name, email, phone, and password
- **SQLite Database**: User data is stored locally using SQLite database

### 2. Dashboard
- **Welcome Screen**: Shows company branding and service overview
- **Quick Action Buttons**: 
  - Browse Services
  - New Repair Request  
  - My Bookings
  - My Profile
- **Service Categories Display**: Shows available repair services for different devices

## Demo Credentials
For testing the app, use these credentials:
- **Email**: demo@techcare.com
- **Password**: demo123

## App Structure

### Activities
1. **MainActivity**: Entry point that redirects to LoginActivity
2. **LoginActivity**: Handles user authentication 
3. **RegisterActivity**: Handles new user registration
4. **DashboardActivity**: Main app dashboard after login

### Database
- **DatabaseHelper**: SQLite helper class for user management
- **Users Table**: Stores user information (id, email, phone, password, name)

### Layouts
- **activity_login.xml**: Login form with TechCare branding
- **activity_register.xml**: Registration form
- **activity_dashboard.xml**: Dashboard with quick actions and service categories

## Services Offered
- 📱 Smartphones & Tablets
- 💻 Laptops & Computers  
- 📺 Televisions
- ❄️ Air Conditioners
- 🧊 Refrigerators
- 👔 Washing Machines

## Technical Details
- **Language**: Kotlin
- **Database**: SQLite
- **Minimum SDK**: 24
- **Target SDK**: 36

## How to Use
1. Launch the app
2. Use demo credentials to login or register a new account
3. Access the dashboard to view available services
4. Use logout button to return to login screen

## Future Enhancements
- Implement service browsing functionality
- Add repair request submission
- Add booking management
- Add user profile management
- Add real-time status updates
- Add technician assignment features
