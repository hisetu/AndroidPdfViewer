# Zoom Sensitivity Configuration

## Problem
On high-resolution displays (e.g., 4K), pinch zoom gestures may feel less responsive compared to lower resolution displays (e.g., 1080p). Users need to pinch their fingers much wider to achieve the same zoom effect.

## Solution
This library now provides a `zoomSensitivity` configuration option to adjust the responsiveness of pinch zoom gestures.

## Usage

### Method 1: Configure during PDFView initialization (Recommended)

```java
pdfView.fromAsset("sample.pdf")
    .defaultPage(0)
    .zoomSensitivity(2.0f)  // Increase sensitivity for 4K displays
    .load();
```

### Method 2: Set dynamically after initialization

```java
// Adjust sensitivity at runtime
pdfView.setZoomSensitivity(2.0f);
```

## Recommended Values

- **1.0f** - Standard behavior (no amplification)
- **1.5f** - Default value, works well for most devices
- **2.0f-2.5f** - Recommended for 4K displays
- **3.0f** - Maximum recommended value for very high-resolution displays

## Example: Auto-detect display density

```java
// Automatically adjust sensitivity based on screen density
DisplayMetrics metrics = getResources().getDisplayMetrics();
float density = metrics.densityDpi / 160f;
float sensitivity = 1.0f;

if (density >= 3.5f) {
    // 4K displays (XXXHDPI and above)
    sensitivity = 2.5f;
} else if (density >= 2.0f) {
    // High-density displays (XXHDPI)
    sensitivity = 2.0f;
} else {
    // Normal displays
    sensitivity = 1.5f;
}

pdfView.fromAsset("sample.pdf")
    .zoomSensitivity(sensitivity)
    .load();
```

## Technical Details

The zoom sensitivity works by amplifying the scale factor change detected by the pinch gesture:

```
amplified_scale = 1.0 + (original_scale - 1.0) * sensitivity
```

For example, with sensitivity 2.0:
- Original scale factor: 1.1 (10% zoom in)
- Amplified scale factor: 1.2 (20% zoom in)

This makes the zoom feel twice as responsive without affecting the zoom limits or causing jumpy behavior.
