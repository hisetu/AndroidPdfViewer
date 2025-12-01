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

import android.content.Context
import android.util.DisplayMetrics
import android.view.WindowManager
import kotlin.math.max
import kotlin.math.min

/**
 * Utility object for screen resolution detection and zoom level calculation.
 */
object ScreenUtils {

    /**
     * Default zoom levels for standard (1080p) screens
     */
    const val DEFAULT_MIN_ZOOM = 1f
    const val DEFAULT_MID_ZOOM = 1.75f
    const val DEFAULT_MAX_ZOOM = 3f

    /**
     * Zoom levels for 4K screens
     */
    const val ZOOM_4K_MIN = 1f
    const val ZOOM_4K_MID = 3.5f
    const val ZOOM_4K_MAX = 7f

    /**
     * Checks if the device has a 4K screen.
     * A screen is considered 4K if max(width, height) >= 3840 && min(width, height) >= 2160
     *
     * @param context The application context
     * @return true if the device has a 4K screen, false otherwise
     */
    fun is4KScreen(context: Context): Boolean {
        val displayMetrics = getDisplayMetrics(context)
        val width = displayMetrics.widthPixels
        val height = displayMetrics.heightPixels

        val maxDimension = max(width, height)
        val minDimension = min(width, height)

        return maxDimension >= 3840 && minDimension >= 2160
    }

    /**
     * Gets the display metrics for the current device.
     *
     * @param context The application context
     * @return DisplayMetrics containing screen information
     */
    fun getDisplayMetrics(context: Context): DisplayMetrics {
        val displayMetrics = DisplayMetrics()
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            val windowMetrics = windowManager.currentWindowMetrics
            val bounds = windowMetrics.bounds
            displayMetrics.widthPixels = bounds.width()
            displayMetrics.heightPixels = bounds.height()
        } else {
            @Suppress("DEPRECATION")
            windowManager.defaultDisplay.getRealMetrics(displayMetrics)
        }
        return displayMetrics
    }

    /**
     * Data class containing zoom level configuration.
     */
    data class ZoomConfig(
        val minZoom: Float,
        val midZoom: Float,
        val maxZoom: Float
    )

    /**
     * Gets the appropriate zoom configuration based on screen resolution.
     * Returns higher zoom levels for 4K screens to improve pinch-zoom responsiveness.
     *
     * @param context The application context
     * @return ZoomConfig with appropriate zoom levels for the screen
     */
    fun getZoomConfigForScreen(context: Context): ZoomConfig {
        return if (is4KScreen(context)) {
            ZoomConfig(
                minZoom = ZOOM_4K_MIN,
                midZoom = ZOOM_4K_MID,
                maxZoom = ZOOM_4K_MAX
            )
        } else {
            ZoomConfig(
                minZoom = DEFAULT_MIN_ZOOM,
                midZoom = DEFAULT_MID_ZOOM,
                maxZoom = DEFAULT_MAX_ZOOM
            )
        }
    }
}
