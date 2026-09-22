package com.sobuj.cheperakha.reader.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sobuj.cheperakha.reader.model.BookMetadata
import com.sobuj.cheperakha.reader.model.ReadingState
import com.sobuj.cheperakha.reader.settings.SettingsStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val settingsStorage = SettingsStorage(application)

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _readingState = MutableStateFlow(ReadingState())
    val readingState: StateFlow<ReadingState> = _readingState.asStateFlow()

    fun loadBook() {
        _uiState.value = UiState.Success
    }

    fun getSavedReadingState(): ReadingState {
        return _readingState.value
    }

    fun saveReadingState(state: ReadingState) {
        _readingState.value = state
    }

    fun getSettings() = settingsStorage.getSettings()

    fun updateSettings(settings: com.sobuj.cheperakha.reader.model.ReadingSettings) {
        settingsStorage.saveSettings(settings)
    }

    sealed class UiState {
        object Loading : UiState()
        object Success : UiState()
        data class Error(val message: String) : UiState()
    }
}
