package view

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import viewmodel.EdgeViewModel
import java.awt.Toolkit
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun <E, V> edgeViewDirected(viewModel: EdgeViewModel<E, V>) {
    val width = Toolkit.getDefaultToolkit().screenSize.width
    val height = Toolkit.getDefaultToolkit().screenSize.height
    Canvas(modifier = Modifier.fillMaxSize()) {
        val start =
            Offset(
                x =
                    viewModel.from.x.value.dp
                        .toPx() +
                        viewModel.from.radius.value.dp
                            .toPx() + width / 2,
                y =
                    viewModel.from.y.value.dp
                        .toPx() +
                        viewModel.from.radius.value.dp
                            .toPx() + height / 2,
            )
        val end =
            Offset(
                x =
                    viewModel.to.x.value.dp
                        .toPx() +
                        viewModel.to.radius.value.dp
                            .toPx() + width / 2,
                y =
                    viewModel.to.y.value.dp
                        .toPx() +
                        viewModel.to.radius.value.dp
                            .toPx() + height / 2,
            )

        val arrowSize = 20f
        val angle = atan2(end.y - start.y, end.x - start.x)

        val xpos = (start.x + end.x * 2) / 3
        val ypos = (start.y + end.y * 2) / 3
        val x1 = xpos - arrowSize * cos(angle + PI / 4)
        val y1 = ypos - arrowSize * sin(angle + PI / 4)
        val x2 = xpos - arrowSize * cos(angle - PI / 4)
        val y2 = ypos - arrowSize * sin(angle - PI / 4)

        val path =
            Path().apply {
                moveTo(start.x, start.y)
                lineTo(end.x, end.y)
                moveTo(xpos, ypos)
                lineTo(x1.toFloat(), y1.toFloat())
                moveTo(xpos, ypos)
                lineTo(x2.toFloat(), y2.toFloat())
            }

        drawPath(
            path = path,
            color = viewModel.color.value,
            style = Stroke(width = 2.dp.toPx()),
        )
    }
    if (viewModel.isVisible.value) {
        Text(
            modifier =
                Modifier.offset(
                    x = (viewModel.from.x.value.dp + viewModel.to.x.value.dp) / 2 + width.dp / 2,
                    y = (viewModel.from.y.value.dp + viewModel.to.y.value.dp) / 2 + height.dp / 2,
                ),
            text = viewModel.edge.weight.toString(),
        )
    }
}

@Composable
fun <E, V> edgeViewUndirected(viewModel: EdgeViewModel<E, V>) {
    val width = Toolkit.getDefaultToolkit().screenSize.width
    val height = Toolkit.getDefaultToolkit().screenSize.height
    Canvas(modifier = Modifier.fillMaxSize()) {
        drawLine(
            strokeWidth = 2f,
            start =
                Offset(
                    x =
                        viewModel.from.x.value.dp
                            .toPx() +
                            viewModel.from.radius.value.dp
                                .toPx() + width / 2,
                    y =
                        viewModel.from.y.value.dp
                            .toPx() +
                            viewModel.from.radius.value.dp
                                .toPx() + height / 2,
                ),
            end =
                Offset(
                    x =
                        viewModel.to.x.value.dp
                            .toPx() +
                            viewModel.to.radius.value.dp
                                .toPx() + width / 2,
                    y =
                        viewModel.to.y.value.dp
                            .toPx() +
                            viewModel.to.radius.value.dp
                                .toPx() + height / 2,
                ),
            color = viewModel.color.value,
        )
    }
    if (viewModel.isVisible.value) {
        Text(
            modifier =
                Modifier.offset(
                    x = (viewModel.from.x.value.dp + viewModel.to.x.value.dp) / 2 + width.dp / 2,
                    y = (viewModel.from.y.value.dp + viewModel.to.y.value.dp) / 2 + height.dp / 2,
                ),
            text = viewModel.edge.weight.toString(),
        )
    }
}
