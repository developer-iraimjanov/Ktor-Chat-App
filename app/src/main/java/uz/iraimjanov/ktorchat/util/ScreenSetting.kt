package uz.iraimjanov.ktorchat.util

import android.app.Activity
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

@Composable
fun ScreenSetting(boolean: Boolean) {
    val view = LocalView.current
    val window = (view.context as Activity).window
    WindowCompat.setDecorFitsSystemWindows(window, boolean)
}
