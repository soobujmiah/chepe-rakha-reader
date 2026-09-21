package com.sobuj.cheperakha.reader.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sobuj.cheperakha.reader.settings.SettingsStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ReaderViewModel(application: Application) : AndroidViewModel(application) {

    private val settingsStorage = SettingsStorage(application)
    
    private val _progress = MutableLiveData<Float>()
    val progress: LiveData<Float> = _progress

    private val _currentChapter = MutableStateFlow(0)
    val currentChapter: StateFlow<Int> = _currentChapter.asStateFlow()

    private var savedChapterIndex = 0
    private var savedScrollY = 0

    fun saveReadingPosition(chapterIndex: Int, scrollY: Int) {
        savedChapterIndex = chapterIndex
        savedScrollY = scrollY
        _currentChapter.value = chapterIndex
    }

    fun getLastReadingPosition(): ReadingPosition {
        return ReadingPosition(savedChapterIndex, savedScrollY)
    }

    fun updateProgress(percent: Float) {
        _progress.value = percent
    }

    fun getCurrentChapter(): Int = _currentChapter.value

    data class ReadingPosition(val chapterIndex: Int, val scrollY: Int)
}
