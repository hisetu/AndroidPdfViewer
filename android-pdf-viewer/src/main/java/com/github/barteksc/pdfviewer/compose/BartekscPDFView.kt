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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.github.barteksc.pdfviewer.PDFView
import java.io.File

/**
 * A Jetpack Compose wrapper for PDFView that supports customizable zoom levels.
 * This component automatically configures zoom settings for improved pinch-zoom
 * experience, especially on 4K devices.
 *
 * @param file The PDF file to display
 * @param minZoom Minimum zoom level (default: 1f)
 * @param midZoom Middle zoom level for double-tap (default: 1.75f)
 * @param maxZoom Maximum zoom level (default: 3f)
 * @param modifier Modifier for the composable
 * @param enableSwipe Enable/disable swipe gestures (default: true)
 * @param swipeHorizontal Enable horizontal swipe mode (default: false for vertical)
 * @param enableDoubleTap Enable/disable double-tap zoom (default: true)
 * @param defaultPage Default page to display initially (default: 0)
 * @param spacing Spacing between pages in dp (default: 0)
 * @param onPageChange Callback when page changes
 * @param onLoadComplete Callback when PDF is fully loaded
 * @param onError Callback when an error occurs
 */
@Composable
fun BartekscPDFView(
    file: File,
    minZoom: Float = ScreenUtils.DEFAULT_MIN_ZOOM,
    midZoom: Float = ScreenUtils.DEFAULT_MID_ZOOM,
    maxZoom: Float = ScreenUtils.DEFAULT_MAX_ZOOM,
    modifier: Modifier = Modifier,
    enableSwipe: Boolean = true,
    swipeHorizontal: Boolean = false,
    enableDoubleTap: Boolean = true,
    defaultPage: Int = 0,
    spacing: Int = 0,
    onPageChange: ((page: Int, pageCount: Int) -> Unit)? = null,
    onLoadComplete: ((nbPages: Int) -> Unit)? = null,
    onError: ((t: Throwable) -> Unit)? = null
) {
    val context = LocalContext.current
    
    // Remember the file path to detect changes
    val filePath = remember(file) { file.absolutePath }
    
    // Track whether we've already loaded this file
    val loadedFileRef = remember { mutableMapOf<String, Boolean>() }
    
    AndroidView(
        factory = { ctx ->
            PDFView(ctx, null).apply {
                // Set zoom levels in factory
                setMinZoom(minZoom)
                setMidZoom(midZoom)
                setMaxZoom(maxZoom)
                
                // Load the PDF file with configuration
                fromFile(file)
                    .enableSwipe(enableSwipe)
                    .swipeHorizontal(swipeHorizontal)
                    .enableDoubletap(enableDoubleTap)
                    .defaultPage(defaultPage)
                    .spacing(spacing)
                    .apply {
                        onPageChange?.let { callback ->
                            onPageChange { page, pageCount -> callback(page, pageCount) }
                        }
                        onLoadComplete?.let { callback ->
                            onLoad { nbPages -> callback(nbPages) }
                        }
                        onError?.let { callback ->
                            onError { t -> callback(t) }
                        }
                    }
                    .load()
                
                loadedFileRef[filePath] = true
            }
        },
        modifier = modifier,
        update = { view ->
            // Only update zoom levels if they've changed
            if (view.minZoom != minZoom) view.setMinZoom(minZoom)
            if (view.midZoom != midZoom) view.setMidZoom(midZoom)
            if (view.maxZoom != maxZoom) view.setMaxZoom(maxZoom)
            
            // Reload PDF only if the file has changed
            if (loadedFileRef[filePath] != true) {
                view.fromFile(file)
                    .enableSwipe(enableSwipe)
                    .swipeHorizontal(swipeHorizontal)
                    .enableDoubletap(enableDoubleTap)
                    .defaultPage(defaultPage)
                    .spacing(spacing)
                    .apply {
                        onPageChange?.let { callback ->
                            onPageChange { page, pageCount -> callback(page, pageCount) }
                        }
                        onLoadComplete?.let { callback ->
                            onLoad { nbPages -> callback(nbPages) }
                        }
                        onError?.let { callback ->
                            onError { t -> callback(t) }
                        }
                    }
                    .load()
                
                loadedFileRef[filePath] = true
            }
        }
    )
    
    // Clean up when the composable is disposed
    DisposableEffect(filePath) {
        onDispose {
            loadedFileRef.remove(filePath)
        }
    }
}

/**
 * A Jetpack Compose wrapper for PDFView with automatic 4K screen detection.
 * This variant automatically adjusts zoom levels based on screen resolution
 * for optimal pinch-zoom experience.
 *
 * @param file The PDF file to display
 * @param modifier Modifier for the composable
 * @param enableSwipe Enable/disable swipe gestures (default: true)
 * @param swipeHorizontal Enable horizontal swipe mode (default: false for vertical)
 * @param enableDoubleTap Enable/disable double-tap zoom (default: true)
 * @param defaultPage Default page to display initially (default: 0)
 * @param spacing Spacing between pages in dp (default: 0)
 * @param onPageChange Callback when page changes
 * @param onLoadComplete Callback when PDF is fully loaded
 * @param onError Callback when an error occurs
 */
@Composable
fun BartekscPDFViewAuto(
    file: File,
    modifier: Modifier = Modifier,
    enableSwipe: Boolean = true,
    swipeHorizontal: Boolean = false,
    enableDoubleTap: Boolean = true,
    defaultPage: Int = 0,
    spacing: Int = 0,
    onPageChange: ((page: Int, pageCount: Int) -> Unit)? = null,
    onLoadComplete: ((nbPages: Int) -> Unit)? = null,
    onError: ((t: Throwable) -> Unit)? = null
) {
    val context = LocalContext.current
    val zoomConfig = remember { ScreenUtils.getZoomConfigForScreen(context) }
    
    BartekscPDFView(
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
