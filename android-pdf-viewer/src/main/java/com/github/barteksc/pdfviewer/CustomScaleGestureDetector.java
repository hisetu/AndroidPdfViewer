/**
 * Copyright 2016 Bartosz Schiller
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.barteksc.pdfviewer;

import android.content.Context;
import android.view.MotionEvent;

/**
 * Custom scale gesture detector that allows configuring the minimum span (finger distance)
 * required to start a scale gesture. This is useful for high-resolution displays where
 * the default Android ScaleGestureDetector may require too large a finger distance.
 * 
 * The key difference from Android's ScaleGestureDetector is that this implementation
 * allows you to set a very small minimum span, so pinch-to-zoom triggers almost immediately
 * when two fingers touch the screen.
 */
class CustomScaleGestureDetector {

    public interface OnScaleGestureListener {
        boolean onScale(CustomScaleGestureDetector detector);
        boolean onScaleBegin(CustomScaleGestureDetector detector);
        void onScaleEnd(CustomScaleGestureDetector detector);
    }

    private final OnScaleGestureListener listener;
    private final Context context;

    private float focusX;
    private float focusY;
    private float currentSpan;
    private float previousSpan;
    private float initialSpan;

    private boolean inProgress = false;
    private boolean gestureStarted = false;
    
    /** 
     * Minimum span in pixels required to start a scale gesture.
     * Default is 10dp converted to pixels - much smaller than Android's default.
     */
    private float minSpanToStart;
    
    /**
     * Default minimum span in dp
     */
    private static final float DEFAULT_MIN_SPAN_DP = 10f;

    public CustomScaleGestureDetector(Context context, OnScaleGestureListener listener) {
        this.context = context;
        this.listener = listener;
        // Convert default dp to pixels
        float density = context.getResources().getDisplayMetrics().density;
        this.minSpanToStart = DEFAULT_MIN_SPAN_DP * density;
    }

    /**
     * Set the minimum span (distance between fingers) in pixels required to start a scale gesture.
     * Lower values make the zoom trigger more easily with less finger movement.
     * 
     * @param minSpan Minimum span in pixels. Use 0 for immediate response.
     *                Typical values: 0-50 pixels for very responsive, 50-200 for normal.
     */
    public void setMinSpanToStart(float minSpan) {
        this.minSpanToStart = Math.max(0, minSpan);
    }

    /**
     * Set the minimum span in dp (density-independent pixels).
     * This is more consistent across different screen densities.
     * 
     * @param minSpanDp Minimum span in dp. Use 0 for immediate response.
     *                  Typical values: 0-20dp for very responsive, 20-50dp for normal.
     */
    public void setMinSpanToStartDp(float minSpanDp) {
        float density = context.getResources().getDisplayMetrics().density;
        this.minSpanToStart = Math.max(0, minSpanDp * density);
    }

    /**
     * Get the current minimum span required to start scaling.
     * @return Minimum span in pixels
     */
    public float getMinSpanToStart() {
        return minSpanToStart;
    }

    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();
        int pointerCount = event.getPointerCount();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                // Reset state
                gestureStarted = false;
                inProgress = false;
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                if (pointerCount >= 2) {
                    // Two fingers down, prepare for scaling
                    updateSpanAndFocus(event);
                    initialSpan = currentSpan;
                    previousSpan = currentSpan;
                    gestureStarted = true;
                    
                    // If minimum span is very small or zero, start immediately
                    if (currentSpan >= minSpanToStart && !inProgress) {
                        inProgress = listener.onScaleBegin(this);
                    }
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (pointerCount >= 2 && gestureStarted) {
                    updateSpanAndFocus(event);
                    
                    // Check if we should start scaling
                    if (!inProgress && currentSpan >= minSpanToStart) {
                        inProgress = listener.onScaleBegin(this);
                        previousSpan = currentSpan;
                    }
                    
                    // If scaling is in progress, notify listener
                    if (inProgress) {
                        boolean handled = listener.onScale(this);
                        if (handled) {
                            previousSpan = currentSpan;
                        }
                    }
                }
                break;

            case MotionEvent.ACTION_POINTER_UP:
                if (inProgress) {
                    listener.onScaleEnd(this);
                    inProgress = false;
                }
                gestureStarted = false;
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (inProgress) {
                    listener.onScaleEnd(this);
                    inProgress = false;
                }
                gestureStarted = false;
                break;
        }

        return true;
    }

    private void updateSpanAndFocus(MotionEvent event) {
        if (event.getPointerCount() < 2) {
            return;
        }

        float x0 = event.getX(0);
        float y0 = event.getY(0);
        float x1 = event.getX(1);
        float y1 = event.getY(1);

        // Calculate focus point (center between two fingers)
        focusX = (x0 + x1) / 2;
        focusY = (y0 + y1) / 2;

        // Calculate span (distance between two fingers)
        float dx = x1 - x0;
        float dy = y1 - y0;
        currentSpan = (float) Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * Returns the X coordinate of the focal point of the current gesture.
     * The focal point is the center point between the two fingers.
     */
    public float getFocusX() {
        return focusX;
    }

    /**
     * Returns the Y coordinate of the focal point of the current gesture.
     * The focal point is the center point between the two fingers.
     */
    public float getFocusY() {
        return focusY;
    }

    /**
     * Returns the current distance between the two pointers forming the gesture.
     */
    public float getCurrentSpan() {
        return currentSpan;
    }

    /**
     * Returns the previous distance between the two pointers forming the gesture.
     */
    public float getPreviousSpan() {
        return previousSpan;
    }

    /**
     * Returns the scaling factor from the previous scale event to the current event.
     * This value is defined as (current span) / (previous span).
     */
    public float getScaleFactor() {
        if (previousSpan > 0) {
            return currentSpan / previousSpan;
        }
        return 1.0f;
    }

    /**
     * Returns whether a scale gesture is currently in progress.
     */
    public boolean isInProgress() {
        return inProgress;
    }
}
