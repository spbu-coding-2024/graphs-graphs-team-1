package view

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import viewmodel.EdgeViewModel

@Composable
fun <E, V> EdgeView(
    viewModel: EdgeViewModel<E, V>,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        drawLine(
            start = Offset(
                x = viewModel.from.x.value.dp.toPx()+ 25.dp.toPx(),
                y = viewModel.from.y.value.dp.toPx()+ 25.dp.toPx(),
            ),
            end = Offset(
                x = viewModel.to.x.value.dp.toPx()+ 25.dp.toPx(),
                y = viewModel.to.y.value.dp.toPx()+ 25.dp.toPx(),
            ),
            color = Color.Black
        )
    }
}