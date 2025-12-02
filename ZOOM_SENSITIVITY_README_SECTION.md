
## Zoom Sensitivity (for high-resolution displays)

For devices with high-resolution displays (4K tablets, etc.), pinch zoom gestures may feel less responsive. 
You can adjust the zoom sensitivity to make zooming more responsive:

``` java
pdfView.fromAsset("document.pdf")
    .zoomSensitivity(2.0f)  // Increase sensitivity for better responsiveness
    .load();
```

**Recommended values:**
- `1.0f` - Standard behavior (no amplification)
- `1.5f` - Default value, works well for most devices
- `2.0f-2.5f` - Recommended for 4K displays
- `3.0f` - Maximum recommended for very high-resolution displays

You can also adjust sensitivity dynamically based on screen density:

``` java
DisplayMetrics metrics = getResources().getDisplayMetrics();
float density = metrics.densityDpi / 160f;
float sensitivity = density >= 3.5f ? 2.5f : (density >= 2.0f ? 2.0f : 1.5f);

pdfView.fromAsset("document.pdf")
    .zoomSensitivity(sensitivity)
    .load();
```

Or change it at runtime:
``` java
pdfView.setZoomSensitivity(2.5f);
```

See [ZOOM_SENSITIVITY.md](ZOOM_SENSITIVITY.md) for more details and examples.

