/**
 * Copyright 2016 Bartosz Schiller
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.barteksc.pdfviewer.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import java.io.File

/**
 * A complete PDF viewer screen composable that automatically detects 4K screens
 * and applies appropriate zoom settings for optimal pinch-zoom experience.
 *
 * This screen provides:
 * - Automatic 4K screen detection
 * - Optimized zoom levels for different screen resolutions
 * - Full screen PDF viewing with configurable options
 *
 * @param file The PDF file to display
 * @param modifier Modifier for the screen container
 * @param enableSwipe Enable/disable swipe gestures (default: true)
 * @param swipeHorizontal Enable horizontal swipe mode (default: false for vertical)
 * @param enableDoubleTap Enable/disable double-tap zoom (default: true)
 * @param defaultPage Default page to display initially (default: 0)
 * @param spacing Spacing between pages in dp (default: 10)
 * @param onPageChange Callback when page changes, provides (page, pageCount)
 * @param onLoadComplete Callback when PDF is fully loaded, provides page count
 * @param onError Callback when an error occurs
 */
@Composable
fun PDFViewerScreen(
    file: File,
    modifier: Modifier = Modifier,
    enableSwipe: Boolean = true,
    swipeHorizontal: Boolean = false,
    enableDoubleTap: Boolean = true,
    defaultPage: Int = 0,
    spacing: Int = 10,
    onPageChange: ((page: Int, pageCount: Int) -> Unit)? = null,
    onLoadComplete: ((nbPages: Int) -> Unit)? = null,
    onError: ((t: Throwable) -> Unit)? = null
) {
    val context = LocalContext.current
    val zoomConfig = remember { ScreenUtils.getZoomConfigForScreen(context) }
    
    PDFViewerScreenContent(
        file = file,
        minZoom = zoomConfig.minZoom,
        midZoom = zoomConfig.midZoom,
        maxZoom = zoomConfig.maxZoom,
        modifier = modifier,
        enableSwipe = enableSwipe,
        swipeHorizontal = swipeHorizontal,
        enableDoubleTap = enableDoubleTap,
        defaultPage = defaultPage,
        spacing = spacing,
        onPageChange = onPageChange,
        onLoadComplete = onLoadComplete,
        onError = onError
    )
}

/**
 * PDF viewer screen content with explicit zoom configuration.
 * Use this when you want to manually specify zoom levels instead of
 * relying on automatic screen detection.
 *
 * @param file The PDF file to display
 * @param minZoom Minimum zoom level
 * @param midZoom Middle zoom level for double-tap
 * @param maxZoom Maximum zoom level
 * @param modifier Modifier for the screen container
 * @param enableSwipe Enable/disable swipe gestures (default: true)
 * @param swipeHorizontal Enable horizontal swipe mode (default: false for vertical)
 * @param enableDoubleTap Enable/disable double-tap zoom (default: true)
 * @param defaultPage Default page to display initially (default: 0)
 * @param spacing Spacing between pages in dp (default: 10)
 * @param onPageChange Callback when page changes, provides (page, pageCount)
 * @param onLoadComplete Callback when PDF is fully loaded, provides page count
 * @param onError Callback when an error occurs
 */
@Composable
fun PDFViewerScreenContent(
    file: File,
    minZoom: Float,
    midZoom: Float,
    maxZoom: Float,
    modifier: Modifier = Modifier,
    enableSwipe: Boolean = true,
    swipeHorizontal: Boolean = false,
    enableDoubleTap: Boolean = true,
    defaultPage: Int = 0,
    spacing: Int = 10,
    onPageChange: ((page: Int, pageCount: Int) -> Unit)? = null,
    onLoadComplete: ((nbPages: Int) -> Unit)? = null,
    onError: ((t: Throwable) -> Unit)? = null
) {
    Box(modifier = modifier.fillMaxSize()) {
        BartekscPDFView(
            file = file,
            minZoom = minZoom,
            midZoom = midZoom,
            maxZoom = maxZoom,
            modifier = Modifier.fillMaxWidth(),
            enableSwipe = enableSwipe,
            swipeHorizontal = swipeHorizontal,
            enableDoubleTap = enableDoubleTap,
            defaultPage = defaultPage,
            spacing = spacing,
            onPageChange = onPageChange,
            onLoadComplete = onLoadComplete,
            onError = onError
        )
    }
}
