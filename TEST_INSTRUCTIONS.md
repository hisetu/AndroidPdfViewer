# Testing Instructions for Zoom Sensitivity Feature

## Quick Test

### On a 4K Device:

1. **Without the fix (original behavior):**
   ```java
   pdfView.fromAsset("sample.pdf")
       .zoomSensitivity(1.0f)  // Original behavior
       .load();
   ```
   - Try to zoom in/out with pinch gesture
   - Notice how much finger movement is required

2. **With the fix (recommended for 4K):**
   ```java
   pdfView.fromAsset("sample.pdf")
       .zoomSensitivity(2.5f)  // Enhanced for 4K
       .load();
   ```
   - Try the same pinch gesture
   - Should feel much more responsive

## Test Cases

### Test Case 1: Basic Zoom In
1. Open a PDF with default zoom
2. Perform a small pinch-out gesture (zoom in)
3. Verify zoom increases smoothly
4. Try with different sensitivity values (1.0f, 1.5f, 2.0f, 2.5f)

### Test Case 2: Basic Zoom Out
1. Zoom in to 2x or 3x
2. Perform a small pinch-in gesture (zoom out)
3. Verify zoom decreases smoothly
4. Try with different sensitivity values

### Test Case 3: Zoom Limits
1. Zoom in to maximum zoom level
2. Try to zoom in further
3. Verify it respects max zoom limit (no over-zoom)
4. Repeat for minimum zoom

### Test Case 4: Dynamic Sensitivity Change
```java
// In your Activity
Button btnLowSensitivity = findViewById(R.id.btnLow);
Button btnHighSensitivity = findViewById(R.id.btnHigh);

btnLowSensitivity.setOnClickListener(v -> {
    pdfView.setZoomSensitivity(1.0f);
    Toast.makeText(this, "Low sensitivity (1.0x)", Toast.LENGTH_SHORT).show();
});

btnHighSensitivity.setOnClickListener(v -> {
    pdfView.setZoomSensitivity(2.5f);
    Toast.makeText(this, "High sensitivity (2.5x)", Toast.LENGTH_SHORT).show();
});
```

### Test Case 5: Screen Density Detection
```java
private void setupPDFWithAutoSensitivity() {
    DisplayMetrics metrics = getResources().getDisplayMetrics();
    float dpi = metrics.densityDpi;
    float density = dpi / 160f;
    
    float sensitivity;
    String deviceType;
    
    if (density >= 3.5f) {
        sensitivity = 2.5f;
        deviceType = "4K/XXXHDPI";
    } else if (density >= 2.5f) {
        sensitivity = 2.0f;
        deviceType = "XXHDPI";
    } else {
        sensitivity = 1.5f;
        deviceType = "Standard";
    }
    
    Log.d("PDFTest", "DPI: " + dpi + ", Density: " + density + 
          ", Type: " + deviceType + ", Sensitivity: " + sensitivity);
    
    pdfView.fromAsset("sample.pdf")
        .zoomSensitivity(sensitivity)
        .onLoad(nbPages -> {
            Toast.makeText(this, 
                "Loaded with " + sensitivity + "x sensitivity for " + deviceType,
                Toast.LENGTH_LONG).show();
        })
        .load();
}
```

## Expected Results

### 1080p Device (density ~2.0):
- Sensitivity 1.5f should feel natural and responsive
- Sensitivity 2.0f should feel more responsive

### 4K Device (density ~3.5-4.0):
- Sensitivity 1.0f should feel sluggish (the original problem)
- Sensitivity 2.0-2.5f should feel natural and responsive (like 1080p)
- Sensitivity 3.0f should feel very responsive

## Performance Verification

Monitor these aspects during testing:

1. **Smoothness**: Zoom animation should be smooth without stuttering
2. **Responsiveness**: Small finger movements should produce visible zoom changes
3. **No Overshoot**: Zoom should stop at min/max limits
4. **No Lag**: There should be no delay between gesture and zoom response

## Device Comparison Table

| Device Type | Screen Density | DPI | Recommended Sensitivity |
|------------|---------------|-----|------------------------|
| Phone (HD) | ~1.5 | ~240 | 1.5f |
| Phone (FHD) | ~2.0 | ~320 | 1.5f |
| Tablet (FHD) | ~2.0 | ~320 | 2.0f |
| Tablet (QHD) | ~2.5 | ~400 | 2.0f |
| Tablet (4K) | ~3.5+ | ~560+ | 2.5f |

## Debugging Tips

If zoom feels wrong:

1. **Too Sensitive** (zoom jumps too much):
   - Reduce sensitivity value (try 1.5f or 1.0f)
   
2. **Not Sensitive Enough** (need large finger movements):
   - Increase sensitivity value (try 2.5f or 3.0f)
   
3. **Check Current Settings**:
   ```java
   DisplayMetrics metrics = getResources().getDisplayMetrics();
   Log.d("PDFTest", "DPI: " + metrics.densityDpi);
   Log.d("PDFTest", "Density: " + (metrics.densityDpi / 160f));
   Log.d("PDFTest", "Width: " + metrics.widthPixels);
   Log.d("PDFTest", "Height: " + metrics.heightPixels);
   ```
