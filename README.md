# Rafeeq: Healthcare App

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Android Studio](https://img.shields.io/badge/Android%20Studio-3DDC84?style=for-the-badge&logo=androidstudio&logoColor=white)
![Firebase](https://img.shields.io/badge/Firebase-FFCA28?style=for-the-badge&logo=firebase&logoColor=black)
![Realtime Database](https://img.shields.io/badge/Realtime%20Database-FFCA28?style=for-the-badge&logo=firebase&logoColor=black)

---

## Overview

Rafeeq is an Android healthcare application developed as a *Graduation Project* by a team of five students. The application connects users with caregivers through a simple and user-friendly interface, allowing users to register, log in, browse healthcare services, and book appointments.

The application was built using *Java, **Android Studio, **Firebase Authentication, and **Firebase Realtime Database*.

---

## Features

- User Registration with Role Selection (Client / Caregiver)
- User Authentication (Login / Logout / Forgot Password)
- Role-based System (Different interfaces for Client and Caregiver)
- Profile Management for both Client and Caregiver
- Appointment Booking (Select date, time, and preferred schedule)
- Caregiver Appointment Management (Accept / Reject appointments)
- Appointment Status Tracking
- Reports
- Notifications

---

## Technologies Used

- Java  
- Android Studio  
- XML  
- Firebase Authentication  
- Firebase Realtime Database  
- Gradle  

---

## My Contributions

This project was developed by a team of five students, where I served as the *Team Leader*.

My responsibilities included:

- Led the development process and coordinated tasks among team members  
- Designed and implemented Firebase Realtime Database structure  
- Implemented Firebase Authentication  
- Developed Login screen  
- Developed Registration screen  
- Implemented Forgot Password feature  
- Developed Caregiver Home screen  
- Created Splash Screen  
- Integrated Firebase services with Android application  

---

## How to Run

1. Clone or download this repository  
2. Open the project in Android Studio  
3. Create a Firebase project  
4. Enable *Authentication* and *Realtime Database*  
5. Add your google-services.json file inside the app folder  
6. Sync Gradle  
7. Run the application  

---

## Project Structure

- app/ – Android source code  
- gradle/ – Gradle configuration  
- build.gradle.kts – Project build configuration  
- settings.gradle.kts – Project settings  
- gradle.properties – Gradle properties  

---

## Screenshots

### Splash Screen
![Splash Screen](screenshots/splash.png)

### Login
![Login](screenshots/login.png)

### Register
![Register](screenshots/register.png)

### Caregiver Home
![Caregiver Home](screenshots/caregiver-home.png)

### Client Home
![Client Home](screenshots/client.png)

---

## Database

This project uses Firebase Realtime Database to store and manage:

- Client and Caregiver user data
- Appointment bookings
- Notifications (reports-related only)
- User reports
---

## Note

The file google-services.json is not included for security reasons.

To run the project, create your own Firebase project and place the file inside the app/ directory.
