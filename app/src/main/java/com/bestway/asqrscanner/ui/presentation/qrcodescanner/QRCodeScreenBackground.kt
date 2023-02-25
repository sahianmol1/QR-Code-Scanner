package com.bestway.asqrscanner.ui.presentation

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bestway.asqrscanner.R
import com.bestway.asqrscanner.ui.theme.transparentBlackColor
import kotlin.math.min

@Composable
fun QRCodeBackground(
    modifier: Modifier = Modifier,
    backgroundColor: Color = transparentBlackColor,
    scannerColor: Color = Color.Transparent,
    boundaryColor: Color = Color.Green,
    boundaryWidth: Dp = 2.dp,
    cornerRadius: Float = 40f,
    text: String = stringResource(R.string.scan_a_qr_code),
    textSize: TextUnit = 12.sp,
    textPadding: Dp = 28.dp
) {

    val configuration = LocalConfiguration.current

    Canvas(modifier = modifier.fillMaxSize()) {

        val canvasHeight = this.size.height
        val canvasWidth = this.size.width
        val canvasCenter = this.size.center

        val textPaint = Paint().apply {
            this.color = Color.White.toArgb()
            this.textSize = textSize.toPx()
            this.textAlign = Paint.Align.CENTER
        }

        val scannerPath = Path().apply {
            addRoundRect(
                roundRect = RoundRect(
                    rect = Rect(
                        center = Offset(x = canvasCenter.x, y = canvasCenter.y),
                        radius = min(configuration.screenWidthDp, configuration.screenHeightDp).toFloat()
                    ),
                    cornerRadius = CornerRadius(cornerRadius)
                )
            )
            fillType = PathFillType.EvenOdd
            close()
        }

        val backgroundPath = Path().apply {
            moveTo(0f, 0f)
            lineTo(0f, canvasHeight)
            lineTo(canvasWidth, canvasHeight)
            lineTo(canvasWidth, 0f)
            fillType = PathFillType.EvenOdd
            addPath(scannerPath)
            close()
        }

        drawPath(path = backgroundPath, color = backgroundColor)
        drawPath(path = scannerPath, color = scannerColor)
        drawPath(path = scannerPath, color = boundaryColor, style = Stroke(width = boundaryWidth.toPx()))

        drawContext.canvas.nativeCanvas.drawText(
            text,
            scannerPath.getBounds().bottomCenter.x,
            scannerPath.getBounds().bottomCenter.y + textPadding.toPx(),
            textPaint
        )
    }
}