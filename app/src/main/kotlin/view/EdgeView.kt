package view

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import viewmodel.EdgeViewModel
import java.awt.Toolkit

@Composable
fun <E, V> EdgeView(
    viewModel: EdgeViewModel<E, V>,
    modifier: Modifier = Modifier,
) {
    val width = Toolkit.getDefaultToolkit().screenSize.width
    val height = Toolkit.getDefaultToolkit().screenSize.height
    Canvas(modifier = modifier.fillMaxSize()) {
        drawLine(
            strokeWidth = 2f,
            start = Offset(
                x = viewModel.from.x.value.dp.toPx()+ viewModel.from.radius.value.dp.toPx()+width/2,
                y = viewModel.from.y.value.dp.toPx()+ viewModel.from.radius.value.dp.toPx()+height/2,
            ),
            end = Offset(
                x = viewModel.to.x.value.dp.toPx()+ viewModel.to.radius.value.dp.toPx()+width/2,
                y = viewModel.to.y.value.dp.toPx()+ viewModel.to.radius.value.dp.toPx()+height/2,
            ),
            color = viewModel.color.value,
        )
    }
    if (viewModel.isVisible.value) {
        Text(
            modifier = modifier.offset(
                x = (viewModel.from.x.value.dp + viewModel.to.x.value.dp) / 2 +width.dp/2,
                y = (viewModel.from.y.value.dp + viewModel.to.y.value.dp) / 2+height.dp/2
            ), text = viewModel.edge.weight.toString()
        )
    }
}
