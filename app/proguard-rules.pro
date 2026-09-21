# Chepe Rakha Reader - ProGuard / R8 rules
# Keep EPUB parsing and rendering classes intact.
-keep class com.sobuj.cheperakha.reader.epub.** { *; }
-keep class com.sobuj.cheperakha.reader.model.** { *; }
-keep class com.sobuj.cheperakha.reader.ads.** { *; }

# Google Mobile Ads SDK keep rules (usually handled by the SDK but explicit here)
-keep class com.google.android.gms.ads.** { *; }
-keep class com.google.android.ump.** { *; }

# WebView/Chromium rules (for WebTextProvider fallback)
-dontwarn android.webkit.WebViewFactory
-dontwarn chromium.**
