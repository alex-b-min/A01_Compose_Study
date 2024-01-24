package com.ftd.ivi.cerence.ui.compose

import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.drawToBitmap
import com.ftd.ivi.cerence.util.CustomLogger

@Composable
fun CaptureBitmap(
    captureRequestKey: String,
    content: @Composable () -> Unit,
    onBitmapCaptured: (String, Bitmap) -> Unit
) {

    val context = LocalContext.current

    /**
     * ComposeView that would take composable as its content
     * Kept in remember so recomposition doesn't re-initialize it
     **/
    val composeView = remember { ComposeView(context) }

    // If key is changed it means it's requested to capture a Bitmap
    LaunchedEffect(captureRequestKey) {
        CustomLogger.e("LaunchedEffect captureRequestKey:${captureRequestKey}")
        composeView.post {
            onBitmapCaptured.invoke(captureRequestKey, composeView.drawToBitmap())
        }
    }

    /** Use Native View inside Composable **/
    AndroidView(
        factory = {
            composeView.apply {
                setContent {
                    content.invoke()
                }
            }
        }
    )
}