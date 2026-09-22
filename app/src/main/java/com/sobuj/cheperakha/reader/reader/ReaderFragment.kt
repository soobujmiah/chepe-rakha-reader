package com.sobuj.cheperakha.reader.reader

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import com.sobuj.cheperakha.reader.databinding.FragmentReaderBinding
import com.sobuj.cheperakha.reader.model.ReadingSettings
import java.net.URLEncoder

/**
 * Reader fragment that displays EPUB content using a WebView with custom CSS.
 * Supports Bengali text rendering with proper typography.
 */
class ReaderFragment : Fragment() {

    private var _binding: FragmentReaderBinding? = null
    private val binding get() = _binding!!

    var chapterContents: List<String> = emptyList()
        private set
    var chapterTitles: List<String> = emptyList()
        private set
    var cssStylesheet = ""
        private set
    private var currentChapterIndex = 0
    private var readingSettings = ReadingSettings()

    /**
     * Called by ReaderActivity after EPUB is parsed. Safe to call before or after onViewCreated().
     */
    fun setBookData(contents: List<String>, titles: List<String>, stylesheet: String) {
        android.util.Log.d("ReaderFragment", "setBookData called: contents.size=${contents.size}, titles.size=${titles.size}")
        // Store first so loadChapters can read it
        chapterContents = contents
        chapterTitles = titles
        cssStylesheet = stylesheet
        // Only load if view exists; otherwise onViewCreated() will load later
        if (_binding != null) {
            android.util.Log.d("ReaderFragment", "View is ready, loading chapter $currentChapterIndex")
            loadCurrentChapter()
        } else {
            android.util.Log.d("ReaderFragment", "View NOT ready yet, will load in onViewCreated")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReaderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupWebView()
        // Load chapters now that _binding is non-null
        android.util.Log.d("ReaderFragment", "onViewCreated: chapterContents.size=${chapterContents.size}, isEmpty=${chapterContents.isEmpty()}")
        if (chapterContents.isNotEmpty()) {
            loadChapters(chapterContents, chapterTitles, cssStylesheet)
        }
    }

    private fun setupWebView() {
        val webView = binding.webReader

        val settings: WebSettings = webView.settings
        settings.javaScriptEnabled = false // Disable JS for security
        settings.domStorageEnabled = true
        settings.useWideViewPort = true
        settings.loadWithOverviewMode = true
        settings.cacheMode = WebSettings.LOAD_NO_CACHE
        settings.setSupportZoom(false)
        settings.builtInZoomControls = false
        settings.displayZoomControls = false

        webView.isVerticalScrollBarEnabled = false
        webView.isHorizontalScrollBarEnabled = false

        // Handle links within the book
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                url?.let {
                    if (it.startsWith("chapter://")) {
                        val index = it.split("/").lastOrNull()?.toIntOrNull()
                        if (index != null && index in chapterContents.indices) {
                            currentChapterIndex = index
                            loadCurrentChapter()
                            parentFragmentManager.setFragmentResult("chapter_change", Bundle().apply {
                                putInt("chapter", index)
                                putString("title", chapterTitles.getOrNull(index) ?: "")
                            })
                            return true
                        }
                    }
                }
                return super.shouldOverrideUrlLoading(view, url)
            }
        }
    }

    private fun loadCurrentChapter() {
        if (chapterContents.isEmpty() || currentChapterIndex >= chapterContents.size) {
            Log.e("ReaderFragment", "No chapters loaded or invalid index: $currentChapterIndex")
            return
        }

        val htmlContent = chapterContents[currentChapterIndex]
        val chapterTitle = chapterTitles.getOrNull(currentChapterIndex) ?: "Chapter ${currentChapterIndex + 1}"
        val isFirstChapter = currentChapterIndex == 0
        val isLastChapter = currentChapterIndex == chapterContents.size - 1

        // Build complete HTML with styles
        val fullHtml = buildHtmlContent(htmlContent)

        // Set theme colors via CSS variables
        val bgColor = getBackgroundColor()
        val textColor = getTextColor()
        val accentColor = getAccentColor()

        val themedHtml = """
            <html>
            <head>
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <style>
                    :root {
                        --bg-color: $bgColor;
                        --text-color: $textColor;
                        --accent-color: $accentColor;
                    }
                    body {
                        background-color: var(--bg-color);
                        color: var(--text-color);
                        font-family: '${if (readingSettings.fontFamily == ReadingSettings.FontFamily.SANS_SERIF) "Noto Sans Bengali, sans-serif" else "Noto Serif Bengali, serif"}';
                        font-size: ${readingSettings.fontSize}px;
                        line-height: ${readingSettings.lineSpacing};
                        text-align: ${readingSettings.textAlign.cssValue};
                        margin: ${readingSettings.getMarginDp()}px;
                        padding: 20px;
                        -webkit-text-size-adjust: 100%;
                    }
                    h1, h2, h3, h4, h5, h6 {
                        font-weight: bold;
                        margin-top: 1.5em;
                        margin-bottom: 0.5em;
                        line-height: 1.3;
                        color: var(--text-color);
                    }
                    p {
                        margin-bottom: ${readingSettings.paragraphSpacing}em;
                        text-indent: 1.5em;
                    }
                    p:first-child {
                        text-indent: 0;
                    }
                    img {
                        max-width: 100%;
                        height: auto;
                        display: block;
                        margin: 1em auto;
                    }
                    blockquote {
                        border-left: 3px solid var(--accent-color);
                        margin-left: 0;
                        padding-left: 1em;
                        font-style: italic;
                        color: ${getSecondaryColor()};
                    }
                    a {
                        color: var(--accent-color);
                        text-decoration: none;
                    }
                    .calibre {
                        margin: 0 !important;
                        padding: 0 !important;
                    }
                </style>
            </head>
            <body>
                $htmlContent
            </body>
            </html>
        """.trimIndent()

        binding.webReader.loadDataWithBaseURL(
            "file:///android_asset/book/",
            themedHtml,
            "text/html; charset=utf-8",
            "UTF-8",
            null
        )

        // Notify parent of current chapter
        parentFragmentManager.setFragmentResult("chapter_info", Bundle().apply {
            putString("title", chapterTitle)
            putInt("total", chapterContents.size)
            putInt("current", currentChapterIndex + 1)
            putBoolean("isFirst", isFirstChapter)
            putBoolean("isLast", isLastChapter)
        })
    }

    private fun buildHtmlContent(chapterHtml: String): String {
        // Extract body content if present
        val bodyStart = chapterHtml.indexOf("<body")
        val bodyEnd = chapterHtml.indexOf("</body>")

        return if (bodyStart != -1 && bodyEnd != -1) {
            chapterHtml.substring(bodyStart, bodyEnd + 7)
        } else {
            chapterHtml
        }
    }

    private fun getBackgroundColor(): String {
        return when (readingSettings.theme) {
            ReadingSettings.Theme.LIGHT -> "#FAF8F5"
            ReadingSettings.Theme.SEPIA -> "#F5F0E8"
            ReadingSettings.Theme.DARK -> "#1A1A1A"
        }
    }

    private fun getTextColor(): String {
        return when (readingSettings.theme) {
            ReadingSettings.Theme.LIGHT -> "#1A1A1A"
            ReadingSettings.Theme.SEPIA -> "#3A2E1A"
            ReadingSettings.Theme.DARK -> "#E8E6E3"
        }
    }

    private fun getAccentColor(): String {
        return when (readingSettings.theme) {
            ReadingSettings.Theme.LIGHT -> "#8B6914"
            ReadingSettings.Theme.SEPIA -> "#6B4F1D"
            ReadingSettings.Theme.DARK -> "#C4A35A"
        }
    }

    private fun getSecondaryColor(): String {
        return when (readingSettings.theme) {
            ReadingSettings.Theme.LIGHT -> "#6B6B6B"
            ReadingSettings.Theme.SEPIA -> "#7A6B52"
            ReadingSettings.Theme.DARK -> "#9E9E9E"
        }
    }

    /**
     * Update settings and reload current chapter.
     */
    fun updateSettings(newSettings: ReadingSettings) {
        readingSettings = newSettings
        loadCurrentChapter()
    }

    /**
     * Navigate to specific chapter.
     */
    fun goToChapter(index: Int) {
        if (index in chapterContents.indices) {
            currentChapterIndex = index
            loadCurrentChapter()
            // Scroll to top
            binding.webReader.scrollTo(0, 0)
        }
    }

    /**
     * Get current chapter index.
     */
    fun getCurrentChapterIndex(): Int = currentChapterIndex

    /**
     * Get total number of chapters.
     */
    fun getTotalChapters(): Int = chapterContents.size

    /**
     * Load chapters into the reader.
     */
    fun loadChapters(
        contents: List<String>,
        titles: List<String>,
        stylesheet: String
    ) {
        chapterContents = contents
        chapterTitles = titles
        cssStylesheet = stylesheet
        loadCurrentChapter()

        // Notify parent of chapter info
        parentFragmentManager.setFragmentResult("chapter_info", Bundle().apply {
            putString("title", chapterTitles.getOrNull(currentChapterIndex) ?: "")
            putInt("total", chapterContents.size)
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

// Extension for margin calculation based on font size
private fun ReadingSettings.getMarginDp(): Int {
    return when {
        fontSize < 18 -> 16
        fontSize < 22 -> 24
        else -> 32
    }
}

// Extension to get CSS value for text alignment
private val ReadingSettings.TextAlign.cssValue: String
    get() = when (this) {
        ReadingSettings.TextAlign.LEFT -> "left"
        ReadingSettings.TextAlign.JUSTIFY -> "justify"
    }
