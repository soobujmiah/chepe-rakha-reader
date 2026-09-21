# Chepe Rakha Reader — Project Complete

## Summary

**Project Location**: `/home/sbj/epub-reader-android/`

**EPUB Analyzed**: "Chepe Rakha Ityhash" by Allama Golam Ahmad Mortaza
- 25 chapters, Bengali text, EPUB 2.0, 532KB
- No rendering blockers — WebView approach suitable

## Project Structure

```
epub-reader-android/
├── .github/workflows/build.yml      # GitHub Actions CI/CD
├── app/
│   ├── src/main/
│   │   ├── assets/book/book.epub    # Embedded EPUB
│   │   ├── java/com/sobuj/.../
│   │   │   ├── MainActivity.kt      # Book cover/home screen
│   │   │   ├── ReaderActivity.kt    # Reader activity
│   │   │   ├── ReaderFragment.kt    # WebView-based chapter renderer
│   │   │   ├── EpubParser.kt        # EPUB metadata/chapter parser
│   │   │   ├── AdsManager.kt        # AdMob integration (test IDs)
│   │   │   ├── SettingsStorage.kt   # SharedPreferences persistence
│   │   │   └── viewmodel/*.kt       # MVVM ViewModels
│   │   ├── res/                     # Layouts, themes, colors, strings
│   │   └── AndroidManifest.xml
│   └── build.gradle.kts
├── gradle/wrapper/                  # Gradle 8.9 wrapper
├── gradlew                          # Wrapper script
└── Documentation:
    ├── BUILD_INSTRUCTIONS.md        # Build & deploy guide
    ├── PUBLISH_CHECKLIST.md         # Pre-launch checklist
    ├── PRIVACY_POLICY_TEMPLATE.md   # Privacy policy draft
    ├── PLAY_STORE_CONFIG.md         # Store listing info
    └── docs/EPUB_ANALYSIS.md        # EPUB analysis report
```

## What's Implemented

| Feature | Status |
|---------|--------|
| EPUB parsing & chapter extraction | ✅ |
| NCX/HTML TOC navigation | ✅ |
| WebView-based Bengali text rendering | ✅ |
| Light/Sepia/Dark themes | ✅ |
| Font size, line spacing, margin controls | ✅ |
| Reading progress persistence | ✅ |
| Bookmarks (local) | ✅ (stub) |
| Chapter search | ✅ (basic) |
| AdMob test mode | ✅ |
| AdMob production mode | ⏳ (via CI secrets) |
| UMP Consent SDK | ✅ (ready) |

## How to Build

### Via GitHub Actions (Recommended)
1. Push repo to GitHub
2. Add secret `ADMOB_APP_ID` (optional)
3. Optionally add keystore secrets for signed release
4. Builds trigger automatically on push

### Output Artifacts
- Debug APK: `app/build/outputs/apk/debug/app-debug.apk`
- Release AAB: `app/build/outputs/bundle/release/app-release.aab`

## What You Need to Provide Before Publishing

| Item | Where to Get |
|------|-------------|
| AdMob App ID | https://admob.google.com |
| Ad Unit IDs | AdMob dashboard after app creation |
| Privacy Policy URL | Host yourself or use a generator |
| Play Developer Account | https://play.google.com/console ($25 one-time) |
| App Icon (512x512 PNG) | Design or generate |
| Screenshots | Capture from test device |
| Content Rating | Determine from book content |

## Package & Version
- **Package**: `com.sobuj.cheperakha.reader`
- **Min SDK**: 26 (Android 8.0)
- **Target SDK**: 35 (Android 15)
- **Version**: 1.0.0
