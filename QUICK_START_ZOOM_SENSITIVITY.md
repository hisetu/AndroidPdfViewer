# Quick Start: Zoom Sensitivity

## Problem
PDF zoom feels unresponsive on 4K devices? 

## Solution (One Line!)

```java
pdfView.fromAsset("document.pdf")
    .zoomSensitivity(2.5f)  // Add this line
    .load();
```

## Values

| Device | Value | Why |
|--------|-------|-----|
| 1080p | 1.5f | Default, works well |
| 2K | 2.0f | Good balance |
| 4K | 2.5f | Matches 1080p feel |

## Smart Auto-Detection

```java
DisplayMetrics m = getResources().getDisplayMetrics();
float s = m.densityDpi >= 560 ? 2.5f : (m.densityDpi >= 400 ? 2.0f : 1.5f);
pdfView.fromAsset("document.pdf").zoomSensitivity(s).load();
```

## Runtime Change

```java
pdfView.setZoomSensitivity(2.5f);  // Change anytime
```

That's it! 🎉

See [ZOOM_SENSITIVITY.md](ZOOM_SENSITIVITY.md) for more details.
