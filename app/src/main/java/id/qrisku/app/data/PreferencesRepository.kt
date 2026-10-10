package id.qrisku.app.data

import android.content.Context
import androidx.core.content.edit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class PreferencesRepository(context: Context, storeName: String = "qrisku_voice") {
    private val preferences = context.getSharedPreferences(storeName, Context.MODE_PRIVATE)
    private val mutableState = MutableStateFlow(read())
    val state = mutableState.asStateFlow()

    fun setAutomatic(enabled: Boolean) {
        preferences.edit { putBoolean("automatic", enabled) }
        mutableState.value = read()
    }

    fun setSpeechRate(rate: Float) {
        val safeRate = rate.coerceIn(0.8f, 1.2f)
        preferences.edit { putFloat("speech_rate", safeRate) }
        mutableState.value = read()
    }

    fun completeOnboarding(enableAutomatic: Boolean) {
        preferences.edit {
            putBoolean("onboarding_complete", true)
            putBoolean("automatic", enableAutomatic)
        }
        mutableState.value = read()
    }

    private fun read() = VoicePreferences(
        automatic = preferences.getBoolean("automatic", false),
        speechRate = preferences.getFloat("speech_rate", 1f).coerceIn(0.8f, 1.2f),
        onboardingComplete = preferences.getBoolean("onboarding_complete", false)
    )
}
