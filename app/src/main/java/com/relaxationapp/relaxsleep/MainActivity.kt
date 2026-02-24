package com.relaxationapp.relaxsleep

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.relaxationapp.relaxsleep.views.navigation.RelaxSleepNavGraph
import com.relaxationapp.relaxsleep.views.components.RelaxSleepTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // Keep screen on while app is active (for breathing/timer use)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        setContent {
            RelaxSleepTheme {
                RelaxSleepNavGraph()
            }
        }
    }
}