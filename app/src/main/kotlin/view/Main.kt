package view

import androidx.compose.material.MaterialTheme
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "Graph Application") {
        MaterialTheme {
            mainScreen<Int, Int>()
        }
    }
}