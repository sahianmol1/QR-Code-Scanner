package com.bestway.asqrscanner.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.bestway.asqrscanner.ui.theme.background_gray
import com.bestway.asqrscanner.ui.theme.color_capri
import com.bestway.asqrscanner.ui.theme.gray_f5
import com.bestway.asqrscanner.ui.theme.white

/**
 * Alert dialogs interrupt users with urgent information, details, or actions.
 *
 *
 * @param title The title of the Dialog which should specify the purpose of the Dialog.
 * @param text The text which presents the details regarding the Dialog's purpose.
 * @param confirmText the text to be displayed on the right side of the dialog.
 * @param dismissText the text to be displayed on the left side of the dialog.
 * @param onDismissRequest Executes when the user tries to dismiss the Dialog by clicking outside.
 * or pressing the back button. This is not called when the dismiss button is clicked.
 * @param onConfirmTextClick Executes when user clicks on [confirmText].
 * @param onDismissTextClick Executes when user clicks on [dismissText].
 * @param modifier Modifier to be applied to the layout of the dialog.
 * @param shape Defines the Dialog's shape.
 * @param backgroundColor The background color of the dialog.
 * @param contentColor The preferred content color provided by this dialog to its children.
 * @param properties Typically platform specific properties to further configure the dialog.
 */
@Composable
fun QRScannerAlertDialog(
    title: String,
    text: String,
    confirmText: String,
    dismissText: String,
    onDismissRequest: () -> Unit,
    onConfirmTextClick: () -> Unit,
    onDismissTextClick: () -> Unit,
    modifier: Modifier = Modifier,
    confirmTextColor: Color = QRScannerAlertDialogDefaults.DialogButtonTextColor,
    dismissTextColor: Color = QRScannerAlertDialogDefaults.DialogButtonTextColor,
    shape: Shape = QRScannerAlertDialogDefaults.DialogShape,
    backgroundColor: Color = QRScannerAlertDialogDefaults.DialogBackgroundColor,
    contentColor: Color = QRScannerAlertDialogDefaults.DialogContentColor,
    properties: DialogProperties = DialogProperties()
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = properties,
        content = {
            Surface(
                modifier = modifier,
                shape = shape,
                color = backgroundColor,
                contentColor = contentColor
            ) {
                Column(
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(24.dp)
                ) {
                    Text(
                        text = title,
                        fontSize = 24.sp,
                        color = QRScannerAlertDialogDefaults.DialogContentColor,
                        lineHeight = 32.sp,
                        fontWeight = FontWeight.Normal
                    )

                    Text(
                        modifier = Modifier.padding(top = 16.dp),
                        text = text,
                        fontSize = 14.sp,
                        letterSpacing = 0.25.sp,
                        lineHeight = 20.sp,
                        color = gray_f5,
                        fontWeight = FontWeight.Normal
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 24.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = onDismissTextClick
                        ) {
                            Text(
                                text = dismissText,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = dismissTextColor
                            )
                        }

                        TextButton(
                            modifier = Modifier.padding(start = 8.dp),
                            onClick = onConfirmTextClick
                        ) {
                            Text(
                                text = confirmText,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                letterSpacing = 0.1.sp,
                                color = confirmTextColor
                            )
                        }
                    }
                }
            }
        }
    )
}

/** Contains the default values used by [QRScannerAlertDialog]. */
object QRScannerAlertDialogDefaults {

    /** The default dialog confirm and dismiss button text color. */
    val DialogButtonTextColor
        @Composable
        get() = color_capri

    /** The default dialog shape. */
    val DialogShape = RoundedCornerShape(28.dp)

    /** The default dialog background color. */
    val DialogBackgroundColor
        @Composable
        get() = background_gray

    /** The default dialog content color. */
    val DialogContentColor
        @Composable
        get() = white
}
