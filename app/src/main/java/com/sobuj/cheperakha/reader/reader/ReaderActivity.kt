package com.sobuj.cheperakha.reader.reader

import android.os.Bundle
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebViewClient
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.sobuj.cheperakha.reader.R
import com.sobuj.cheperakha.reader.databinding.ActivityReaderBinding
import com.sobuj.cheperakha.reader.epub.EpubParser
import com.sobuj.cheperakha.reader.settings.SettingsStorage
import com.sobuj.cheperakha.reader.viewmodel.ReaderViewModel

class ReaderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReaderBinding
    private val viewModel: ReaderViewModel by viewModels()
    private lateinit var epubParser: EpubParser
    private lateinit var settingsStorage: SettingsStorage
    private var isControlsVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReaderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        settingsStorage = SettingsStorage(this)
        epubParser = EpubParser(this)

        setupViewer()
        loadBookContent()
        setupGestureControls()
        observeViewModel()

        // Auto-hide controls after delay
        startAutoHideTimer()
    }

    private fun setupViewer() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.readerContainer, ReaderFragment())
            .commitNow()
    }

    private fun loadBookContent() {
        binding.progressBar.visibility = View.VISIBLE
        binding.errorMessage.visibility = View.GONE

        try {
            epubParser.openEpubFromAssets("book/book.epub")

            val contents = epubParser.getChapterContents()
            val toc = epubParser.readTableOfContents()

            val chapterContents = mutableListOf<String>()
            val chapterTitles = mutableListOf<String>()

            // Sort by TOC order if available, otherwise by filename
            val sortedHrefs = toc.map { it.href }.toList()
            val orderedKeys = contents.keys.sortedBy { href ->
                sortedHrefs.indexOf(href).takeIf { it >= 0 } ?: Int.MAX_VALUE
            }

            orderedKeys.forEach { key ->
                chapterContents.add(contents[key] ?: "")
                val title = toc.firstOrNull { it.href == key }?.title
                    ?: key.replace(".html", "").replace(".xhtml", "")
                        .split("_").lastOrNull()?.trim() ?: key
                chapterTitles.add(title)
            }

            val stylesheet = epubParser.getStylesheets().values.firstOrNull() ?: ""

            // Find and pass to fragment
            val fragment = supportFragmentManager.findFragmentById(R.id.readerContainer) as? ReaderFragment
            fragment?.loadChapters(chapterContents, chapterTitles, stylesheet)

            binding.progressBar.visibility = View.GONE

        } catch (e: Exception) {
            binding.progressBar.visibility = View.GONE
            binding.errorMessage.visibility = View.VISIBLE
            binding.errorMessageText.text = getString(R.string.error_message)
            e.printStackTrace()
        }
    }

    private fun setupGestureControls() {
        // Show controls on any tap
        binding.root.setOnClickListener {
            toggleControls()
        }

        // Bottom bar clicks
        binding.chapterName.setOnClickListener {
            showTableOfContents()
        }
    }

    private fun toggleControls() {
        isControlsVisible = !isControlsVisible
        if (isControlsVisible) {
            showControls()
        } else {
            hideControls()
        }
        restartAutoHideTimer()
    }

    private fun showControls() {
        binding.bottomBar.animate()
            .alpha(1f)
            .setDuration(200)
            .start()
    }

    private fun hideControls() {
        binding.bottomBar.animate()
            .alpha(0f)
            .setDuration(200)
            .start()
    }

    private fun startAutoHideTimer() {
        binding.bottomBar.postDelayed({
            if (isControlsVisible) {
                hideControls()
                isControlsVisible = false
            }
        }, 5000)
    }

    private fun restartAutoHideTimer() {
        binding.bottomBar.removeCallbacks(null)
        startAutoHideTimer()
    }

    private fun showTableOfContents() {
        val dialog = BottomSheetDialog(this)
        val contentView = layoutInflater.inflate(R.layout.dialog_toc, null)
        dialog.setContentView(contentView)

        // TODO: Populate TOC RecyclerView
        dialog.show()
    }

    private fun showSettings() {
        val dialog = BottomSheetDialog(this)
        val contentView = layoutInflater.inflate(R.layout.dialog_settings, null)
        dialog.setContentView(contentView)

        // TODO: Setup settings controls

        dialog.show()
    }

    private fun observeViewModel() {
        viewModel.progress.observe(this) { percent ->
            binding.progressPct.text = "${percent.toInt()}%"
        }
    }

    override fun onPause() {
        super.onPause()
        // Save reading position
        val fragment = supportFragmentManager.findFragmentById(R.id.readerContainer) as? ReaderFragment
        fragment?.let {
            viewModel.saveReadingPosition(it.getCurrentChapterIndex(), 0)
        }
    }

    override fun onResume() {
        super.onResume()
        // Restore reading position
        val fragment = supportFragmentManager.findFragmentById(R.id.readerContainer) as? ReaderFragment
        fragment?.let {
            val savedPosition = viewModel.getLastReadingPosition()
            if (savedPosition.chapterIndex != it.getCurrentChapterIndex()) {
                it.goToChapter(savedPosition.chapterIndex)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        epubParser.close()
    }
}
