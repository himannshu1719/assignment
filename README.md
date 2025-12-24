# Desktop Idle Detector

Small assignment project to detect when a Windows system is idle using a C++ DLL and show it in a JavaFX UI.

## Tech Stack

- Java 17 (Maven, Spring Boot)
- JavaFX
- C++ (Windows DLL)
- JNA for Java ↔ DLL calls
- Windows OS

## Features

- Login screen with simple hardcoded credentials.
- Dashboard with:
  - Timer that starts after login.
  - Input box to give idle time in seconds.
  - Start button to call the C++ DLL and wait for idle.
  - Stop button to stop the timer and cancel idle check.
- When system becomes idle:
  - JavaFX popup: `Desktop is in idle state`.
  - Two REST API calls (one HTTP, one HTTPS) are sent with:
    ```
    {
      "userEmailId": "test@example.com",
      "idleState": true
    }
    ```

## Project Structure

- `pom.xml` – Maven config  
- `src/main/java/com/assignment` – Java code (main app, controllers, JNA interface)  
- `src/main/resources` – `login.fxml`, `dashboard.fxml`, properties  
- `native/idle-dll` – C++ source and built DLL (`IdleDetector.dll`)  

## How to Run

1. Install JDK 17 and Maven.
2. Clone the repo.
3. Make sure `IdleDetector.dll` is present under `native/idle-dll` (already included).
4. From project root run:

