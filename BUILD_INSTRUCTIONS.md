# Chepe Rakha Reader — Build Instructions

## Quick Start (GitHub Actions)

This is the recommended build method. No local Android development environment needed.

### Step 1: Create GitHub Repository

```bash
cd /home/sbj/epub-reader-android
git init
git add .
git commit -m "Initial commit: Chepe Rakha Reader"
git branch -M main
git remote add origin https://github.com/YOUR_USERNAME/chepe-rakha-reader.git
git push -u origin main
```

### Step 2: Add GitHub Secrets

Go to your repository → Settings → Secrets and Variables → Actions

Add these secrets:
- `ADMOB_APP_ID` — Your AdMob App ID (e.g., `ca-app-pub-XXXXXXXX~YYYYYYYY`)
- `KEYSTORE_FILE` — Base64 encoded keystore.jks file (optional, for release signing)
- `KEYSTORE_PASSWORD` — Keystore password (optional)
- `KEY_ALIAS` — Key alias (optional)
- `KEY_PASSWORD` — Key password (optional)

### Step 3: Commit EPUB File

Place your EPUB at the repository root as `book.epub` before pushing:

```bash
cp "/home/sbj/Desktop/primary/Chepe Rakha Itihas.epub" /home/sbj/epub-reader-android/book.epub
git add book.epub
git commit -m "Add EPUB book file"
git push
```

### Step 4: Trigger Build

The workflow runs automatically on:
- Push to `main` branch
- Pull requests to `main`
- Tags starting with `v` (creates GitHub Release)

Download artifacts from the Actions tab.

## Local Build (Optional)

If you want to build locally:

### Prerequisites
- JDK 17+
- Android SDK with API 35
- Gradle 8.9+

### Commands

```bash
# Debug APK (unsigned, test builds)
./gradlew assembleDebug

# Release AAB (requires keystore configuration)
./gradlew bundleRelease

# With custom AdMob ID
echo 'admobAppId=ca-app-pub-YOUR_ID' > local.properties
./gradlew assembleDebug
```

## Output Files

| Artifact | Path | Purpose |
|----------|------|---------|
| Debug APK | `app/build/outputs/apk/debug/app-debug.apk` | Testing |
| Release AAB | `app/build/outputs/bundle/release/app-release.aab` | Play Store |

## EPUB Configuration

The EPUB is located at: `app/src/main/assets/book/book.epub`

To use a different EPUB:
1. Replace the file at the above path
2. Ensure it follows standard EPUB 2.0 or 3.0 format
3. Rebuild the app

## AdMob Integration

### Test Mode (Default)
Uses Google's test ad unit IDs. Safe for development.

### Production Mode
Replace test IDs in `app/src/main/res/values/ads.xml` with your real Ad Unit IDs.

## Privacy Compliance

Before publishing to Google Play:
1. Update `PRIVACY_POLICY_TEMPLATE.md` with your details
2. Host the privacy policy at a public URL
3. Add the URL to Play Console listing
4. Complete Data Safety form in Play Console

## Required Play Console Information

When submitting to Google Play:

| Field | Value |
|-------|-------|
| App Name | Chepe Rakha Reader |
| Package Name | com.sobuj.cheperakha.reader |
| Minimum SDK | 26 (Android 8.0) |
| Target SDK | 35 (Android 15) |
| Content Rating | Determine from book content |
| Ads Declaration | Yes - contains ads |
| Data Collection | No personal data collected |
| Category | Books & Reference |

## Troubleshooting

### Build fails with "sdk.dir not found"
Set ANDROID_HOME environment variable:
```bash
export ANDROID_HOME=$HOME/Android/Sdk
```

### Missing gradle-wrapper.jar
Download from: https://services.gradle.org/distributions/gradle-8.9-bin.zip
Extract and place `gradle/wrapper/gradle-wrapper.jar` in the project root.

### EPUB parsing errors
Verify the EPUB is valid using an EPUB checker tool.
