package eu.kanade.tachiyomi.util.system

import androidx.appcompat.app.AppCompatDelegate

object ThemeUtil {
    
    fun nightMode(theme: Int): Int {
        return when (theme) {
            2 -> AppCompatDelegate.MODE_NIGHT_NO
            3 -> AppCompatDelegate.MODE_NIGHT_YES
            else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }
    }
}
