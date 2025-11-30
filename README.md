# README: Compilation, Installation, and Running Instructions

This repository contains **two mobile application implementations** based on the same coursework requirements:

- **Project A:** Native Android application (Java / Android Studio)  
- **Project B:** Cross-platform application (React Native / VS Code / Expo)

The goal of this README is to provide the necessary instructions to **compile, install, and run both projects successfully**.

---

## Project Structure Overview

The project is organised into two main directories:

```text
COMP1786_001340185_Coursework
├─ coursework_java          <-- Native Android App (Java / Android Studio)
└─ coursework_react_native  <-- Cross-Platform App (React Native / VS Code / Expo)
```

---

## 1. Project A: Native Android Application (`coursework_java`)

This application was developed using **Android Studio** and **Java**.

### 1.1. Prerequisites

To run the Java-based Android application, the following are required:

1. **Android Studio**  
   - IDE used for development and required for compilation.
2. **Java Development Kit (JDK)**  
   - Required for building Android projects.
3. **Android SDK**  
   - Necessary for the target Android version configured in the project.
4. **Android Emulator or Physical Device**  
   - Required to run the compiled application.

### 1.2. Compilation and Installation Instructions

1. **Open Project in Android Studio**
   - Start Android Studio.
   - Choose **“Open an existing project”**.
   - Select the `coursework_java` folder.

2. **Wait for Gradle Sync**
   - Allow Android Studio to synchronise Gradle build files and resolve all dependencies.
   - This may take a few minutes the first time.

3. **Build and Run**
   - Connect a physical Android device **(with USB debugging enabled)**, or start an **Android Virtual Device (AVD)**.
   - In Android Studio, select the target device from the device dropdown.
   - Click the **Run** button (▶) or go to **Run > Run 'app'**.

4. **Running the App**
   - Android Studio will compile the Java code and deploy the application to the selected device/emulator.
   - The **main entry point** is `MainActivity`, which displays the **list of hikes**.

---

## 2. Project B: Cross-Platform Application (`coursework_react_native`)

This application was developed using **Visual Studio Code (VS Code)** and **React Native / Expo**.

### 2.1. Prerequisites and Installation

To run this React Native project, you need a suitable environment and the dependencies defined in `package.json`.

1. **Node.js and npm**
   - Required for managing dependencies and running scripts.
   - You can verify installation with:
     ```bash
     node -v
     npm -v
     ```

2. **Visual Studio Code (VS Code)**
   - Recommended IDE used during development.

3. **Expo CLI or Expo Go**
   - **Expo Go app (iOS/Android)** is required on the target device to run the project easily.
   - Alternatively, **Expo CLI** is needed if running on local simulators or compiling standalone apps.
   - To install Expo CLI globally (optional):
     ```bash
     npm install -g expo-cli
     ```

### 2.2. Running Instructions

1. **Navigate to Project Directory**

   Open a terminal or command prompt and change into the React Native project folder:

   ```bash
   cd coursework_react_native
   ```

2. **Install Dependencies**

   Install all necessary libraries listed in `package.json` (for example: `expo-sqlite`, `@react-navigation/native`, etc.):

   ```bash
   npm install
   ```

3. **Start the Application (Expo Server)**

   Start the local development server using one of the following commands:

   ```bash
   npm start
   # or
   expo start
   ```

4. **Database Initialization**

   - The application's entry point, `App.js`, automatically calls **`initDatabase()`** when it starts.
   - This function creates the required **`hikes`** and **`observations`** SQLite tables if they do not already exist.
   - No manual database setup is required.

5. **Access the App**

   - **Simulator / Emulator**
     - Use the options provided in the Expo Developer Tools (opened in your browser or terminal).
     - Typically:
       - Press **`a`** for Android emulator.
       - Press **`i`** for iOS simulator (macOS with Xcode installed).
   - **Physical Device (Expo Go)**
     - Install the **Expo Go** app from Google Play Store or Apple App Store.
     - Scan the **QR code** displayed in the terminal or browser when you run `npm start` / `expo start`.
     - The application will load on your device, starting with the **`HikeListScreen`**.

---

## 3. Troubleshooting

- **Gradle Sync Issues (Android Studio)**
  - Ensure you have a stable internet connection for dependency resolution.
  - Check that the correct JDK is configured in **Project Structure > SDK Location**.

- **Expo or npm Errors**
  - Try clearing the cache:
    ```bash
    npm cache clean --force
    npx expo start -c
    ```
  - Delete `node_modules` and reinstall:
    ```bash
    rm -rf node_modules
    npm install
    ```

- **Device Connection Problems**
  - For Android:
    - Make sure **USB debugging** is enabled.
    - Verify the device is recognised using:
      ```bash
      adb devices
      ```
  - For Expo Go:
    - Ensure your computer and phone are on the **same Wi-Fi network**.

---


