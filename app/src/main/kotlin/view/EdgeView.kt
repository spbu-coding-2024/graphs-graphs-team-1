package view

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import viewmodel.EdgeViewModel

@Composable
fun <E, V> EdgeView(
    viewModel: EdgeViewModel<E, V>,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        drawLine(
            strokeWidth = 2f,
            start = Offset(
                x = viewModel.from.x.value.dp.toPx()+ viewModel.from.radius.dp.toPx(),
                y = viewModel.from.y.value.dp.toPx()+ viewModel.from.radius.dp.toPx(),
            ),
            end = Offset(
                x = viewModel.to.x.value.dp.toPx()+ viewModel.to.radius.dp.toPx(),
                y = viewModel.to.y.value.dp.toPx()+ viewModel.to.radius.dp.toPx(),
            ),
            color = viewModel.color.value
        )
    }
    if (viewModel.isVisible.value) {
        Text(
            modifier = modifier.offset(
                x = (viewModel.from.x.value.dp + viewModel.to.x.value.dp) / 2,
                y = (viewModel.from.y.value.dp + viewModel.to.y.value.dp) / 2
            ), text = viewModel.edge.weight.toString()
        )
    }

}