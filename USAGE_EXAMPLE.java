// Example 1: Simple usage with fixed sensitivity value
pdfView.fromAsset("document.pdf")
    .defaultPage(0)
    .zoomSensitivity(2.0f)  // Better for 4K displays
    .enableSwipe(true)
    .swipeHorizontal(false)
    .load();

// Example 2: Dynamic sensitivity based on screen density
private void setupPDFView() {
    DisplayMetrics metrics = getResources().getDisplayMetrics();
    float density = metrics.densityDpi / 160f;
    float zoomSensitivity;
    
    if (density >= 3.5f) {
        // Ultra high-density displays (4K and above)
        zoomSensitivity = 2.5f;
    } else if (density >= 2.5f) {
        // High-density displays
        zoomSensitivity = 2.0f;
    } else {
        // Normal displays
        zoomSensitivity = 1.5f;
    }
    
    pdfView.fromUri(uri)
        .defaultPage(pageNumber)
        .zoomSensitivity(zoomSensitivity)
        .onLoad(onLoadCompleteListener)
        .onPageChange(onPageChangeListener)
        .load();
}

// Example 3: Allow users to adjust sensitivity
private void setUserPreferredSensitivity(float userValue) {
    // Can be called at any time after PDFView is initialized
    pdfView.setZoomSensitivity(userValue);
}

// Example 4: Detect if running on tablet/phone and adjust
private float getOptimalZoomSensitivity() {
    DisplayMetrics metrics = getResources().getDisplayMetrics();
    
    // Check if it's a tablet
    boolean isTablet = (getResources().getConfiguration().screenLayout 
        & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    
    // Get screen density
    float density = metrics.densityDpi / 160f;
    
    // Tablets with high DPI need more sensitivity
    if (isTablet && density >= 2.5f) {
        return 2.5f;
    } else if (density >= 3.0f) {
        return 2.0f;
    } else {
        return 1.5f;
    }
}
