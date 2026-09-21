package com.sobuj.cheperakha.reader.settings

import android.content.Context
import com.sobuj.cheperakha.reader.model.ReadingSettings

/**
 * Persists and loads user reading preferences using SharedPreferences.
 */
class SettingsStorage(private val context: Context) {

    companion object {
        private const val PREFS_NAME = "reading_settings"
        private const val KEY_THEME = "theme"
        private const val KEY_FONT_FAMILY = "font_family"
        private const val KEY_FONT_SIZE = "font_size"
        private const val KEY_LINE_SPACING = "line_spacing"
        private const val KEY_PARAGRAPH_SPACING = "paragraph_spacing"
        private const val KEY_TEXT_ALIGN = "text_align"
        private const val KEY_SAVED_POSITION = "saved_position"
        private const val KEY_SAVED_CHAPTER = "saved_chapter"
    }

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    /**
     * Get current reading settings or defaults.
     */
    fun getSettings(): ReadingSettings {
        val themeStr = prefs.getString(KEY_THEME, ReadingSettings.Theme.LIGHT.name)
        val fontStr = prefs.getString(KEY_FONT_FAMILY, ReadingSettings.FontFamily.SANS_SERIF.name)
        
        return ReadingSettings(
            theme = ReadingSettings.Theme.valueOf(themeStr ?: ReadingSettings.Theme.LIGHT.name),
            fontFamily = ReadingSettings.FontFamily.valueOf(fontStr ?: ReadingSettings.FontFamily.SANS_SERIF.name),
            fontSize = prefs.getInt(KEY_FONT_SIZE, 17),
            lineSpacing = prefs.getFloat(KEY_LINE_SPACING, 1.7f),
            paragraphSpacing = prefs.getFloat(KEY_PARAGRAPH_SPACING, 1.0f),
            textAlign = ReadingSettings.TextAlign.valueOf(
                prefs.getString(KEY_TEXT_ALIGN, ReadingSettings.TextAlign.JUSTIFY.name) 
                    ?: ReadingSettings.TextAlign.JUSTIFY.name
            )
        )
    }

    /**
     * Save updated settings.
     */
    fun saveSettings(settings: ReadingSettings) {
        prefs.edit()
            .putString(KEY_THEME, settings.theme.name)
            .putString(KEY_FONT_FAMILY, settings.fontFamily.name)
            .putInt(KEY_FONT_SIZE, settings.fontSize)
            .putFloat(KEY_LINE_SPACING, settings.lineSpacing)
            .putFloat(KEY_PARAGRAPH_SPACING, settings.paragraphSpacing)
            .putString(KEY_TEXT_ALIGN, settings.textAlign.name)
            .apply()
    }

    /**
     * Save current reading position.
     */
    fun saveReadingPosition(chapterIndex: Int, scrollOffset: Int) {
        prefs.edit()
            .putInt(KEY_SAVED_CHAPTER, chapterIndex)
            .putInt(KEY_SAVED_POSITION, scrollOffset)
            .apply()
    }

    /**
     * Get last reading position.
     */
    fun getSavedReadingPosition(): Pair<Int, Int> {
        val chapter = prefs.getInt(KEY_SAVED_CHAPTER, 0)
        val offset = prefs.getInt(KEY_SAVED_POSITION, 0)
        return Pair(chapter, offset)
    }

    /**
     * Clear all saved data.
     */
    fun clearAll() {
        prefs.edit().clear().apply()
    }
}
