package view

import androidx.compose.material.MaterialTheme
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "Graph Application", state = WindowState(
        size = DpSize(1000.dp, 800.dp)
    )) {
        MaterialTheme() {
            mainScreen<Int, Int>()
        }
    }
}