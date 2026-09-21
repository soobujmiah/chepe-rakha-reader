# Chepe Rakha Reader — Checklist Before Publishing

## EPUB Verification (Completed)
- [x] EPUB version: 2.0
- [x] Chapters: 25 split HTML files
- [x] Navigation: NCX with navMap structure
- [x] Metadata: Title, Author (Allama Golam Ahmad Mortaza), Language
- [x] Cover: cover.jpg (32KB JPEG)
- [x] Images: 1 cover image
- [x] Fonts: None embedded (uses system Noto Bengali fonts)
- [x] CSS: stylesheet.css, page_styles.css
- [x] XHTML: Standard Calibre-generated markup
- [x] No rendering blockers detected

## Code Review Checklist
- [x] EPUB parser reads metadata
- [x] EPUB parser extracts chapters
- [x] EPUB parser parses TOC from NCX
- [x] EPUB parser handles spine fallback
- [x] WebView renders Bengali text correctly
- [x] Theme switching works (Light/Sepia/Dark)
- [x] Settings persist via SharedPreferences
- [x] Reading position saved on pause
- [x] Progress tracking implemented
- [x] AdMob test IDs in place
- [x] Production IDs isolated via CI secrets
- [x] ProGuard rules preserve key classes
- [x] AndroidManifest declares correct permissions

## Build Configuration
- [x] minSdk: 26 (Android 8.0)
- [x] targetSdk: 35 (Android 15)
- [x] compileSdk: 35
- [x] Gradle wrapper: 8.9
- [x] Kotlin: 2.0.21
- [x] AGP: 8.5.2
- [x] ABIs: arm64-v8a only

## AdMob Setup
- [ ] Create AdMob account at https://admob.google.com
- [ ] Add app with package: com.sobuj.cheperakha.reader
- [ ] Get App ID (format: ca-app-pub-XXXXXXXX~YYYYYYYY)
- [ ] Create Banner ad unit (optional)
- [ ] Create Interstitial ad unit (optional)
- [ ] Add App ID as GitHub secret: ADMOB_APP_ID
- [ ] Update ads.xml with production IDs before release

## Google Play Console Setup
- [ ] Create developer account ($25 one-time fee)
- [ ] Create new app
- [ ] Upload AAB from GitHub Actions artifact
- [ ] Fill store listing:
  - [x] App name: চেপে রাখা ইতিহাস (Chepe Rakha Reader)
  - [ ] Short description
  - [ ] Full description (use PLAY_STORE_CONFIG.md)
  - [ ] Screenshots (required: minimum 2)
  - [ ] Feature graphic (1024x500)
- [ ] Complete Data Safety form
- [ ] Set content rating (need to assess book content)
- [ ] Add privacy policy URL
- [ ] Declare ads presence
- [ ] Submit for review

## Privacy & Compliance
- [ ] Customize PRIVACY_POLICY_TEMPLATE.md
- [ ] Host privacy policy at public URL
- [ ] Verify GDPR consent flow works
- [ ] Test with TestFlight/Play Console internal testing

## App Store Assets Needed
- [ ] App icon (512x512 PNG) — replace placeholder
- [ ] Screenshots (various sizes)
- [ ] Feature graphic
- [ ] Promo graphics (optional)

## Pre-Launch Checklist
- [ ] Test on physical device (Redmi Turbo 4 Pro)
- [ ] Test reading Bengali text display
- [ ] Test all three themes
- [ ] Test chapter navigation
- [ ] Test bookmarking
- [ ] Test progress persistence
- [ ] Test offline mode (no internet)
- [ ] Test ad loading (test mode)
- [ ] Verify no crashes on process restart

## Post-Launch
- [ ] Monitor crash reports in Firebase Crashlytics
- [ ] Check user reviews and ratings
- [ ] Update privacy policy if needed
- [ ] Plan future features based on feedback
