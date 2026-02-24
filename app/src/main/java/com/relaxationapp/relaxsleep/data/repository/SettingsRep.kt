package com.relaxationapp.relaxsleep.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.relaxationapp.relaxsleep.Models.AppSettings
import com.relaxationapp.relaxsleep.Models.AppTheme
import com.relaxationapp.relaxsleep.Models.SleepSound
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "relax_sleep_prefs")

class SettingsRepository(private val context: Context) {

    companion object {
        val KEY_ONBOARDING_DONE  = booleanPreferencesKey("onboarding_done")
        val KEY_SLEEP_REMINDER   = booleanPreferencesKey("sleep_reminder")
        val KEY_VIBRATION        = booleanPreferencesKey("vibration")
        val KEY_AUTO_BREATHING   = booleanPreferencesKey("auto_breathing")
        val KEY_APP_THEME        = stringPreferencesKey("app_theme")
        val KEY_VOLUME           = floatPreferencesKey("volume")
        val KEY_SELECTED_SOUND   = stringPreferencesKey("selected_sound")
        val KEY_SLEEP_DURATION   = intPreferencesKey("sleep_duration_min")
    }

    val settingsFlow: Flow<AppSettings> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences())
            else throw exception
        }
        .map { prefs ->
            AppSettings(
                sleepReminderEnabled = prefs[KEY_SLEEP_REMINDER] ?: false,
                vibrationEnabled = prefs[KEY_VIBRATION] ?: true,
                autoBreathing = prefs[KEY_AUTO_BREATHING] ?: false,
                appTheme = AppTheme.values()
                    .firstOrNull { it.name == prefs[KEY_APP_THEME] } ?: AppTheme.NIGHT,
                volume = prefs[KEY_VOLUME] ?: 0.7f,
                selectedSound = SleepSound.values()
                    .firstOrNull { it.name == prefs[KEY_SELECTED_SOUND] },
                onboardingDone = prefs[KEY_ONBOARDING_DONE] ?: false,
            )
        }

    suspend fun setOnboardingDone(done: Boolean) {
        context.dataStore.edit { it[KEY_ONBOARDING_DONE] = done }
    }
    suspend fun setSleepReminder(enabled: Boolean) {
        context.dataStore.edit { it[KEY_SLEEP_REMINDER] = enabled }
    }
    suspend fun setVibration(enabled: Boolean) {
        context.dataStore.edit { it[KEY_VIBRATION] = enabled }
    }
    suspend fun setAutoBreathing(enabled: Boolean) {
        context.dataStore.edit { it[KEY_AUTO_BREATHING] = enabled }
    }
    suspend fun setAppTheme(theme: AppTheme) {
        context.dataStore.edit { it[KEY_APP_THEME] = theme.name }
    }
    suspend fun setVolume(volume: Float) {
        context.dataStore.edit { it[KEY_VOLUME] = volume }
    }
    suspend fun setSelectedSound(sound: SleepSound?) {
        context.dataStore.edit { it[KEY_SELECTED_SOUND] = sound?.name ?: "" }
    }
    suspend fun setSleepDuration(minutes: Int) {
        context.dataStore.edit { it[KEY_SLEEP_DURATION] = minutes }
    }
}