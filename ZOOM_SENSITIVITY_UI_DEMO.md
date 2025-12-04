# Zoom Sensitivity UI Demo

This sample app demonstrates the dynamic adjustment of zoom sensitivity for AndroidPdfViewer.

## Features

### Interactive UI Control
- **SeekBar** at the bottom of the screen allows real-time adjustment of zoom sensitivity
- **Range**: 0.5x to 5.0x (default: 1.0x)
- **Live Display**: Current sensitivity value shown in bold text
- **Toast Notification**: Confirmation message when sensitivity is changed

### UI Layout

```
┌─────────────────────────────────────┐
│                                     │
│         PDF Content Area            │
│                                     │
│                                     │
│                                     │
└─────────────────────────────────────┘
┌─────────────────────────────────────┐
│ Zoom Sensitivity: 1.0x              │
│ ━━━━━━━━━━○━━━━━━━━━━━━━━━━━━━     │
│       Range: 0.5x - 5.0x            │
└─────────────────────────────────────┘
```

### How to Use

1. **Launch the app** - Sample PDF loads automatically
2. **Adjust SeekBar** - Drag the slider left (lower sensitivity) or right (higher sensitivity)
3. **Release the slider** - PDF reloads with new zoom sensitivity
4. **Test zoom** - Use pinch gesture to experience the new sensitivity

### Code Highlights

#### SeekBar Mapping
- SeekBar progress: 0-40
- Sensitivity value: 0.5x - 5.0x
- Progress 0 → 0.5x
- Progress 10 → 1.0x (default)
- Progress 40 → 5.0x

#### Dynamic Reload
When the user releases the SeekBar, the PDF is automatically reloaded with the new sensitivity:

```java
@Override
public void onStopTrackingTouch(SeekBar seekBar) {
    // Reload PDF with new sensitivity
    if (uri != null) {
        displayFromUri(uri);
    } else {
        displayFromAsset(SAMPLE_FILE);
    }
    Toast.makeText(PDFViewActivity.this, 
        String.format(Locale.US, "Zoom sensitivity set to %.1fx", currentZoomSensitivity), 
        Toast.LENGTH_SHORT).show();
}
```

#### State Persistence
The zoom sensitivity value is preserved across configuration changes (e.g., screen rotation):

```java
@Override
protected void onSaveInstanceState(@NonNull Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putFloat("zoomSensitivity", currentZoomSensitivity);
}
```

### Use Cases

**0.5x - 0.8x (Low Sensitivity)**
- Precise control for detailed work
- Prevents accidental zoom on touchpads
- Good for scrolling-focused reading

**1.0x (Default)**
- Standard zoom behavior
- Balanced for most use cases

**1.5x - 3.0x (Medium-High Sensitivity)**
- Faster zoom response
- Good for high-resolution displays (4K)
- Reduces finger movement needed

**3.5x - 5.0x (Very High Sensitivity)**
- Maximum responsiveness
- Useful for accessibility
- Best for large displays

### Building and Testing

```bash
# Build the sample app
./gradlew :sample:assembleDebug

# Install to device
adb install -r sample/build/outputs/apk/debug/sample-debug.apk

# Or use Android Studio
# Open project and run the 'sample' module
```

### Files Modified

- `sample/src/main/res/layout/activity_main.xml` - Added control panel UI
- `sample/src/main/java/com/github/barteksc/sample/PDFViewActivity.java` - Added SeekBar logic

### API Usage Example

The sample demonstrates the `zoomSensitivity()` API:

```java
pdfView.fromAsset(SAMPLE_FILE)
    .defaultPage(pageNumber)
    .onPageChange(this)
    .enableAnnotationRendering(true)
    .onLoad(this)
    .scrollHandle(new DefaultScrollHandle(this))
    .spacing(10)
    .onPageError(this)
    .pageFitPolicy(FitPolicy.BOTH)
    .zoomSensitivity(currentZoomSensitivity) // Dynamic value from SeekBar
    .load();
```

## Screenshots

The app features a clean, minimal UI with the PDF taking up most of the screen and a small control panel at the bottom for adjusting zoom sensitivity.

### Testing Tips

1. Load a PDF with detailed content (text, images, diagrams)
2. Set sensitivity to 0.5x - notice how it requires larger pinch gestures
3. Set sensitivity to 3.0x - notice how smaller pinch gestures zoom quickly
4. Try different values to find your preferred sensitivity
5. The setting persists across file loads and screen rotations

---

**Note**: This is a demonstration sample app. In production apps, you might want to:
- Save the user's preferred sensitivity to SharedPreferences
- Add a reset button to return to default (1.0x)
- Provide preset buttons (e.g., "Low", "Medium", "High")
- Add a settings screen for more control options
