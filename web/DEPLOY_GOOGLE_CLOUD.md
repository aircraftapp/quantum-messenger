# Deploying Quantum Messenger Marketing Landing Page to Google Cloud ☁️

This guide explains how to publish the static marketing website (`/web/index.html`) to Google Cloud so users can visit your website and download the standalone `.apk` directly off-store.

---

## 🚀 Option 1: Deploying via Firebase Hosting (Recommended)

Firebase Hosting provides free SSL, fast global CDN, and a custom domain.

### Step 1: Install Firebase CLI
```bash
npm install -g firebase-tools
```

### Step 2: Initialize Firebase Hosting
In your project directory:
```bash
firebase login
firebase init hosting
```
* Select your Google Cloud / Firebase project.
* When asked for the public directory, enter: **`web`**
* Configure as a single-page app: **No**

### Step 3: Deploy to Google Cloud
```bash
firebase deploy --only hosting
```
Your landing page will be instantly live at `https://<your-project-id>.web.app` or your custom domain!

---

## 🪣 Option 2: Hosting on Google Cloud Storage (GCS Static Website)

### Step 1: Create a GCS Bucket
```bash
gcloud storage buckets create gs://your-domain.com --location=US
```

### Step 2: Make Bucket Publicly Accessible
```bash
gcloud storage buckets add-iam-policy-binding gs://your-domain.com \
    --member=allUsers \
    --role=roles/storage.objectViewer
```

### Step 3: Set Website Configuration
```bash
gcloud storage buckets update gs://your-domain.com \
    --web-main-page-suffix=index.html \
    --web-error-page-suffix=index.html
```

### Step 4: Upload Landing Page & APK File
```bash
gcloud storage cp web/index.html gs://your-domain.com/
# Copy built APK for direct user download
gcloud storage cp app/build/outputs/apk/debug/app-debug.apk gs://your-domain.com/QuantumMessenger-v2.4-PQC.apk
```

---

## 🐳 Option 3: Deploying on Google Cloud Run (Containerized)

If you prefer Cloud Run, you can run an Nginx container serving `/web`:

```dockerfile
# Dockerfile
FROM nginx:alpine
COPY web /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

Build and deploy to Cloud Run:
```bash
gcloud run deploy quantum-marketing-landing \
    --source . \
    --platform managed \
    --allow-unauthenticated \
    --region us-central1
```

---

## 📱 Note on Kotlin / Jetpack Compose vs Web Stack
* **Android App Runtime:** Built with **Kotlin & Jetpack Compose** (`com.example.ui.screens.LandingPageScreen`), allowing in-app viewing of marketing features and APK checksum verification.
* **Web Landing Page:** Located in `/web/index.html` (Tailwind CSS HTML/JS) ready for 1-click deployment to Google Cloud Storage or Firebase Hosting.
