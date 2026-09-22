package com.sobuj.cheperakha.reader

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdRequest
import com.google.android.material.button.MaterialButton
import com.sobuj.cheperakha.reader.ads.AdsManager
import com.sobuj.cheperakha.reader.databinding.ActivityMainBinding
import com.sobuj.cheperakha.reader.epub.EpubParser
import com.sobuj.cheperakha.reader.model.ReadingState
import com.sobuj.cheperakha.reader.settings.SettingsStorage
import com.sobuj.cheperakha.reader.model.ReadingSettings
import com.sobuj.cheperakha.reader.viewmodel.MainViewModel
import com.sobuj.cheperakha.reader.reader.ReaderActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private lateinit var adsManager: AdsManager
    private lateinit var epubParser: EpubParser
    private lateinit var settingsStorage: SettingsStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        settingsStorage = SettingsStorage(this)
        adsManager = AdsManager(this)
        epubParser = EpubParser(this)

        setupAds()
        loadBook()
        setupClickListeners()
    }

    private fun setupAds() {
        // Ad unit ID is already set in activity_main.xml via app:adUnitId
        // Do NOT call binding.adView.adUnitId again — it throws IllegalStateException
        if (binding.adView.visibility == View.GONE) return
        val adRequest = AdRequest.Builder().build()
        binding.adView.loadAd(adRequest)
    }

    private fun loadBook() {
        binding.progressBar.visibility = View.VISIBLE
        binding.coverImage.visibility = View.GONE
        binding.bookTitle.text = ""
        binding.authorName.text = ""

        try {
            epubParser.openEpubFromAssets("book/book.epub")
            
            // Load metadata
            val meta = epubParser.readMetadata()
            val coverBitmap = epubParser.getCoverImage()
            
            runOnUiThread {
                binding.bookTitle.text = meta.title
                binding.authorName.text = meta.author
                binding.coverImage.setImageBitmap(coverBitmap)
                binding.coverImage.visibility = View.VISIBLE
                binding.progressBar.visibility = View.GONE
                
                // Load saved reading state
                val savedState = viewModel.getSavedReadingState()
                if (savedState.progressPercent > 0) {
                    updateProgressDisplay(savedState)
                }
            }
        } catch (e: Exception) {
            runOnUiThread {
                showError("Failed to open book: ${e.message}")
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnRead.setOnClickListener {
            startActivity(Intent(this, ReaderActivity::class.java))
        }

        binding.btnToc.setOnClickListener {
            showTableOfContents()
        }

        binding.btnSettings.setOnClickListener {
            showSettings()
        }
    }

    private fun showTableOfContents() {
        Toast.makeText(this, "Table of Contents coming soon", Toast.LENGTH_SHORT).show()
    }

    private fun showSettings() {
        Toast.makeText(this, "Settings coming soon", Toast.LENGTH_SHORT).show()
    }

    private fun updateProgressDisplay(state: ReadingState) {
        val progressText = "Chapter ${state.currentChapterIndex + 1}"
        binding.progressText.text = String.format(getString(R.string.progress_percent), 
            ((state.progressPercent * 100).toInt()))
        binding.progressBar.progress = (state.progressPercent * 100).toInt()
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        epubParser.close()
        adsManager.destroy()
    }
}
