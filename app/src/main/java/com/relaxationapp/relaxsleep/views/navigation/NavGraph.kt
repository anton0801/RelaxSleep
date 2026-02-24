package com.relaxationapp.relaxsleep.views.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.relaxationapp.relaxsleep.Models.Routes
import com.relaxationapp.relaxsleep.viewmodel.MainViewModel
import com.relaxationapp.relaxsleep.views.BreathingScreen
import com.relaxationapp.relaxsleep.views.HomeScreen
import com.relaxationapp.relaxsleep.views.OnboardingScreen
import com.relaxationapp.relaxsleep.views.SettingsScreen
import com.relaxationapp.relaxsleep.views.SleepTimerScreen
import com.relaxationapp.relaxsleep.views.SoundsScreen
import com.relaxationapp.relaxsleep.views.SplashScreen

@Composable
fun RelaxSleepNavGraph(vm: MainViewModel = viewModel()) {
    val navController = rememberNavController()
    val settings by vm.settings.collectAsState()
    val breathing by vm.breathing.collectAsState()
    val sleepTimer by vm.sleepTimer.collectAsState()
    val sound by vm.sound.collectAsState()

    // Determine start destination
    val startDest = if (settings.onboardingDone) Routes.HOME else Routes.ONBOARDING

    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH,
        enterTransition = {
            fadeIn(tween(400)) + slideInHorizontally(tween(400)) { it / 4 }
        },
        exitTransition = {
            fadeOut(tween(300)) + slideOutHorizontally(tween(300)) { -it / 4 }
        },
        popEnterTransition = {
            fadeIn(tween(400)) + slideInHorizontally(tween(400)) { -it / 4 }
        },
        popExitTransition = {
            fadeOut(tween(300)) + slideOutHorizontally(tween(300)) { it / 4 }
        },
    ) {
        composable(Routes.SPLASH) {
            SplashScreen(
                onFinished = {
                    navController.navigate(startDest) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.ONBOARDING) {
            OnboardingScreen(
                onFinished = {
                    vm.completeOnboarding()
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.ONBOARDING) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.HOME) {
            HomeScreen(
                theme = settings.appTheme,
                onBreathing = { navController.navigate(Routes.BREATHING) },
                onSounds = { navController.navigate(Routes.SOUNDS) },
                onTimer = { navController.navigate(Routes.SLEEP_TIMER) },
                onSettings = { navController.navigate(Routes.SETTINGS) },
            )
        }

        composable(Routes.BREATHING) {
            BreathingScreen(
                state = breathing,
                theme = settings.appTheme,
                onStart = { vm.startBreathing() },
                onPause = { vm.pauseBreathing() },
                onStop = { vm.stopBreathing() },
                onBack = { navController.popBackStack() },
            )
        }

        composable(Routes.SOUNDS) {
            SoundsScreen(
                state = sound,
                theme = settings.appTheme,
                onSelectSound = { vm.selectSound(it) },
                onVolumeChange = { vm.setVolume(it) },
                onStop = { vm.stopSound() },
                onBack = { navController.popBackStack() },
            )
        }

        composable(Routes.SLEEP_TIMER) {
            SleepTimerScreen(
                state = sleepTimer,
                theme = settings.appTheme,
                onSetDuration = { vm.setTimerDuration(it) },
                onStart = { vm.startTimer() },
                onStop = { vm.stopTimer() },
                onToggleFade = { vm.toggleFade(it) },
                onBack = { navController.popBackStack() },
            )
        }

        composable(Routes.SETTINGS) {
            SettingsScreen(
                settings = settings,
                onSleepReminder = { vm.setSleepReminder(it) },
                onVibration = { vm.setVibration(it) },
                onAutoBreathing = { vm.setAutoBreathing(it) },
                onTheme = { vm.setAppTheme(it) },
                onBack = { navController.popBackStack() },
            )
        }
    }
}