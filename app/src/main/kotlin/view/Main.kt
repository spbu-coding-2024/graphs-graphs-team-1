package view

import androidx.compose.material.MaterialTheme
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import model.Vertex


val vertices = arrayOf(
    Vertex(4,5),
    Vertex(5,5)
)


fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        MaterialTheme {
            mainScreen<Int, Int>(vertices)
        }
    }
}