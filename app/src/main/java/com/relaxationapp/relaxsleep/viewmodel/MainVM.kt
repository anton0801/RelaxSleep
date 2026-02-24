package com.relaxationapp.relaxsleep.viewmodel

import android.app.Application
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.content.Context
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.relaxationapp.relaxsleep.Models.AppSettings
import com.relaxationapp.relaxsleep.Models.AppTheme
import com.relaxationapp.relaxsleep.Models.BreathPhase
import com.relaxationapp.relaxsleep.Models.SleepSound
import com.relaxationapp.relaxsleep.data.repository.SettingsRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

// ── Breathing State ──────────────────────────────────────────────────────────
data class BreathingState(
    val phase: BreathPhase = BreathPhase.INHALE,
    val phaseProgress: Float = 0f,  // 0..1 within current phase
    val sessionSeconds: Int = 0,
    val isRunning: Boolean = false,
    val cycleCount: Int = 0,
)

// ── Sleep Timer State ────────────────────────────────────────────────────────
data class SleepTimerState(
    val durationMinutes: Int = 30,
    val remainingSeconds: Int = 0,
    val isRunning: Boolean = false,
    val fadeEnabled: Boolean = true,
    val fadeAlpha: Float = 1f,       // 1 = full brightness, 0 = faded out
)

// ── Sound State ──────────────────────────────────────────────────────────────
data class SoundState(
    val selected: SleepSound? = null,
    val volume: Float = 0.7f,
    val isPlaying: Boolean = false,
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = SettingsRepository(application)

    // ── Settings ─────────────────────────────────────────────────────────────
    val settings: StateFlow<AppSettings> = repo.settingsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppSettings())

    // ── Breathing ────────────────────────────────────────────────────────────
    private val _breathing = MutableStateFlow(BreathingState())
    val breathing: StateFlow<BreathingState> = _breathing.asStateFlow()

    private var breathingJob: Job? = null

    fun startBreathing() {
        breathingJob?.cancel()
        _breathing.value = _breathing.value.copy(isRunning = true, sessionSeconds = 0, cycleCount = 0)
        breathingJob = viewModelScope.launch {
            val phases = BreathPhase.values()
            var phaseIndex = 0
            var totalSeconds = 0
            while (_breathing.value.isRunning) {
                val phase = phases[phaseIndex % phases.size]
                val phaseDuration = phase.durationSec * 10 // ticks of 100ms
                for (tick in 0 until phaseDuration) {
                    if (!_breathing.value.isRunning) break
                    _breathing.value = _breathing.value.copy(
                        phase = phase,
                        phaseProgress = tick.toFloat() / phaseDuration,
                        sessionSeconds = totalSeconds,
                    )
                    delay(100)
                }
                phaseIndex++
                if (phaseIndex % phases.size == 0) {
                    totalSeconds += phases.sumOf { it.durationSec }
                    _breathing.value = _breathing.value.copy(
                        cycleCount = _breathing.value.cycleCount + 1
                    )
                }
            }
        }
    }

    fun pauseBreathing() {
        _breathing.value = _breathing.value.copy(isRunning = false)
        breathingJob?.cancel()
    }

    fun stopBreathing() {
        breathingJob?.cancel()
        _breathing.value = BreathingState()
    }

    // ── Sleep Timer ──────────────────────────────────────────────────────────
    private val _sleepTimer = MutableStateFlow(SleepTimerState())
    val sleepTimer: StateFlow<SleepTimerState> = _sleepTimer.asStateFlow()

    private var timerJob: Job? = null

    fun setTimerDuration(minutes: Int) {
        _sleepTimer.value = _sleepTimer.value.copy(
            durationMinutes = minutes,
            remainingSeconds = minutes * 60,
        )
    }

    fun startTimer() {
        timerJob?.cancel()
        val totalSeconds = _sleepTimer.value.durationMinutes * 60
        _sleepTimer.value = _sleepTimer.value.copy(
            isRunning = true,
            remainingSeconds = totalSeconds,
            fadeAlpha = 1f,
        )
        timerJob = viewModelScope.launch {
            while (_sleepTimer.value.remainingSeconds > 0 && _sleepTimer.value.isRunning) {
                delay(1000)
                val remaining = _sleepTimer.value.remainingSeconds - 1
                val fadeAlpha = if (_sleepTimer.value.fadeEnabled) {
                    (remaining.toFloat() / totalSeconds).coerceIn(0f, 1f)
                } else 1f
                _sleepTimer.value = _sleepTimer.value.copy(
                    remainingSeconds = remaining,
                    fadeAlpha = fadeAlpha,
                )
                // Fade out sound when timer ends
                if (remaining == 0) {
                    stopSound()
                    _sleepTimer.value = _sleepTimer.value.copy(isRunning = false)
                }
            }
        }
    }

    fun stopTimer() {
        timerJob?.cancel()
        _sleepTimer.value = _sleepTimer.value.copy(isRunning = false, fadeAlpha = 1f)
    }

    fun toggleFade(enabled: Boolean) {
        _sleepTimer.value = _sleepTimer.value.copy(fadeEnabled = enabled)
    }

    // ── Sound ────────────────────────────────────────────────────────────────
    private val _sound = MutableStateFlow(SoundState())
    val sound: StateFlow<SoundState> = _sound.asStateFlow()

    fun selectSound(sound: SleepSound) {
        viewModelScope.launch {
            repo.setSelectedSound(sound)
        }
        _sound.value = _sound.value.copy(selected = sound, isPlaying = true)
        vibrate()
    }

    fun stopSound() {
        _sound.value = _sound.value.copy(isPlaying = false)
    }

    fun setVolume(volume: Float) {
        viewModelScope.launch { repo.setVolume(volume) }
        _sound.value = _sound.value.copy(volume = volume)
    }

    // ── Settings Actions ─────────────────────────────────────────────────────
    fun setSleepReminder(enabled: Boolean) = viewModelScope.launch { repo.setSleepReminder(enabled) }
    fun setVibration(enabled: Boolean)     = viewModelScope.launch { repo.setVibration(enabled) }
    fun setAutoBreathing(enabled: Boolean) = viewModelScope.launch { repo.setAutoBreathing(enabled) }
    fun setAppTheme(theme: AppTheme)       = viewModelScope.launch { repo.setAppTheme(theme) }
    fun completeOnboarding()               = viewModelScope.launch { repo.setOnboardingDone(true) }

    // ── Utilities ────────────────────────────────────────────────────────────
    private fun vibrate() {
        if (!settings.value.vibrationEnabled) return
        try {
            val ctx = getApplication<Application>()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vm = ctx.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                vm.defaultVibrator.vibrate(VibrationEffect.createOneShot(40, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                val vibrator = ctx.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createOneShot(40, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(40)
                }
            }
        } catch (_: Exception) {}
    }

    override fun onCleared() {
        super.onCleared()
        breathingJob?.cancel()
        timerJob?.cancel()
    }
}