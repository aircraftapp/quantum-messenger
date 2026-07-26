# Installation Guide - Quantum Messenger 📦

This guide covers installing Quantum Messenger on Android devices, building from source, and deploying via Enterprise MDM suites.

---

## 📱 Option 1: Direct Standalone .APK Installation (Android)

1. Download the latest signed `.apk` file from the **Releases** tab or the app's in-app **Landing Page**.
2. Verify the package checksum using `sha256sum`:
   ```bash
   sha256sum QuantumMessenger-v2.4-PQC-signed.apk
   # Output should match: 8f92a1c0d3e5b74f9a0c1e2d3b4a5f6e7c8d9a0b1c2d3e4f5a6b7c8d9e0f1a2b
   ```
3. Enable **Install Unknown Apps** for your browser or file manager in Android Settings.
4. Open the `.apk` file and confirm installation.

---

## 🛠️ Option 2: Building From Source Code

### Prerequisites
* **Android Studio** Jellyfish / Ladybug or newer
* **JDK 17** or higher
* **Android SDK** API level 34+

### Build Steps

1. Clone the repository:
   ```bash
   git clone https://github.com/quantum-messenger/quantum-messenger-android.git
   cd quantum-messenger-android
   ```

2. Compile the debug APK using Gradle:
   ```bash
   gradle :app:assembleDebug
   ```

3. Output binary location:
   `app/build/outputs/apk/debug/app-debug.apk`

---

## 🏢 Option 3: Enterprise MDM / EMM Deployment

For enterprise security administrators managing Android Enterprise or Samsung Knox device fleets:

1. Upload the signed `.apk` or Google Play Private App bundle to your MDM console (e.g., **Microsoft Intune**, **VMware Workspace ONE**, or **Samsung Knox Manage**).
2. Configure App Protection Policies:
   * **Prevent Screen Capture:** Enabled
   * **Require Hardware Keystore Attestation:** Enabled
   * **Allow Clipboard Copy/Paste:** Restricted to Work Profile
3. Push silent enterprise installation to managed devices.
