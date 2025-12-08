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
package com.github.barteksc.sample;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.github.barteksc.pdfviewer.util.FitPolicy;
import com.shockwave.pdfium.PdfDocument;

import java.util.List;
import java.util.Locale;

/**
 * Sample activity demonstrating PDFView usage.
 * 
 * This example shows:
 * - Loading PDF from assets
 * - Loading PDF from URI (file picker)
 * - Using zoomSensitivity to adjust pinch-zoom responsiveness
 *   (adjustable via SeekBar from 0.5x to 5.0x)
 * - Using minSpanToStartZoom to adjust the minimum finger distance to trigger zoom
 *   (adjustable via SeekBar from 0dp to 50dp)
 * - Dynamic adjustment of zoom sensitivity in real-time
 */
public class PDFViewActivity extends AppCompatActivity implements OnPageChangeListener, OnLoadCompleteListener,
        OnPageErrorListener {

    private static final String TAG = PDFViewActivity.class.getSimpleName();

    private final static int REQUEST_CODE = 42;
    public static final int PERMISSION_CODE = 42042;

    public static final String SAMPLE_FILE = "sample.pdf";
    public static final String READ_EXTERNAL_STORAGE = "android.permission.READ_EXTERNAL_STORAGE";

    PDFView pdfView;
    Uri uri;
    Integer pageNumber = 0;
    String pdfFileName;
    
    // Zoom sensitivity control
    private SeekBar sensitivitySeekBar;
    private TextView sensitivityValueText;
    private float currentZoomSensitivity = 1.0f;
    
    // Min span to start zoom control
    private SeekBar minSpanSeekBar;
    private TextView minSpanValueText;
    private float currentMinSpanDp = 0f;  // Default to 0 for immediate zoom trigger

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pdfView = findViewById(R.id.pdfView);
        sensitivitySeekBar = findViewById(R.id.sensitivitySeekBar);
        sensitivityValueText = findViewById(R.id.sensitivityValue);
        minSpanSeekBar = findViewById(R.id.minSpanSeekBar);
        minSpanValueText = findViewById(R.id.minSpanValue);

        if (savedInstanceState != null) {
            uri = savedInstanceState.getParcelable("uri");
            pageNumber = savedInstanceState.getInt("pageNumber", 0);
            currentZoomSensitivity = savedInstanceState.getFloat("zoomSensitivity", 1.0f);
            currentMinSpanDp = savedInstanceState.getFloat("minSpanDp", 0f);
        }

        setupSensitivityControl();
        setupMinSpanControl();
        afterViews();
    }

    private void setupSensitivityControl() {
        // SeekBar range: 0-40, mapping to 0.5x - 5.0x
        // Progress 0 = 0.5x, Progress 10 = 1.0x, Progress 40 = 5.0x
        int initialProgress = (int) ((currentZoomSensitivity - 0.5f) * 10);
        sensitivitySeekBar.setProgress(initialProgress);
        updateSensitivityText(currentZoomSensitivity);

        sensitivitySeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Convert progress (0-40) to sensitivity (0.5-5.0)
                currentZoomSensitivity = 0.5f + (progress * 0.1f);
                updateSensitivityText(currentZoomSensitivity);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

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
        });
    }

    private void updateSensitivityText(float sensitivity) {
        sensitivityValueText.setText(String.format(Locale.US, "%.1fx", sensitivity));
    }

    private void setupMinSpanControl() {
        // SeekBar range: 0-50, mapping to 0dp - 50dp
        int initialProgress = (int) currentMinSpanDp;
        minSpanSeekBar.setProgress(initialProgress);
        updateMinSpanText(currentMinSpanDp);

        minSpanSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                currentMinSpanDp = progress;
                updateMinSpanText(currentMinSpanDp);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Apply new min span setting
                pdfView.setMinSpanToStartZoomDp(currentMinSpanDp);
                Toast.makeText(PDFViewActivity.this, 
                    String.format(Locale.US, "Min span to start zoom: %.0fdp", currentMinSpanDp), 
                    Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateMinSpanText(float minSpanDp) {
        minSpanValueText.setText(String.format(Locale.US, "%.0fdp", minSpanDp));
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (uri != null) {
            outState.putParcelable("uri", uri);
        }
        outState.putInt("pageNumber", pageNumber);
        outState.putFloat("zoomSensitivity", currentZoomSensitivity);
        outState.putFloat("minSpanDp", currentMinSpanDp);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.pickFile) {
            pickFile();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    void pickFile() {
        int permissionCheck = ContextCompat.checkSelfPermission(this,
                READ_EXTERNAL_STORAGE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{READ_EXTERNAL_STORAGE},
                    PERMISSION_CODE
            );

            return;
        }

        launchPicker();
    }

    void launchPicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        try {
            startActivityForResult(intent, REQUEST_CODE);
        } catch (ActivityNotFoundException e) {
            //alert user that file manager not working
            Toast.makeText(this, R.string.toast_pick_file_error, Toast.LENGTH_SHORT).show();
        }
    }

    void afterViews() {
        pdfView.setBackgroundColor(Color.LTGRAY);
        if (uri != null) {
            displayFromUri(uri);
        } else {
            displayFromAsset(SAMPLE_FILE);
        }
        setTitle(pdfFileName);
    }

    private void displayFromAsset(String assetFileName) {
        pdfFileName = assetFileName;

        pdfView.fromAsset(SAMPLE_FILE)
                .defaultPage(pageNumber)
                .onPageChange(this)
                .enableAnnotationRendering(true)
                .onLoad(this)
                .scrollHandle(new DefaultScrollHandle(this))
                .spacing(10) // in dp
                .onPageError(this)
                .pageFitPolicy(FitPolicy.BOTH)
                .zoomSensitivity(currentZoomSensitivity) // Use current sensitivity from SeekBar
                .minSpanToStartZoom(currentMinSpanDp)   // Use current min span from SeekBar
                .load();
    }

    private void displayFromUri(Uri uri) {
        pdfFileName = getFileName(uri);

        pdfView.fromUri(uri)
                .defaultPage(pageNumber)
                .onPageChange(this)
                .enableAnnotationRendering(true)
                .onLoad(this)
                .scrollHandle(new DefaultScrollHandle(this))
                .spacing(10) // in dp
                .onPageError(this)
                .zoomSensitivity(currentZoomSensitivity) // Use current sensitivity from SeekBar
                .minSpanToStartZoom(currentMinSpanDp)   // Use current min span from SeekBar
                .load();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            if (intent != null) {
                uri = intent.getData();
                displayFromUri(uri);
            }
        }
    }

    @Override
    public void onPageChanged(int page, int pageCount) {
        pageNumber = page;
        setTitle(String.format("%s %s / %s", pdfFileName, page + 1, pageCount));
    }

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (columnIndex != -1) {
                        result = cursor.getString(columnIndex);
                    }
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        if (result == null) {
            result = uri.getLastPathSegment();
        }
        return result;
    }

    @Override
    public void loadComplete(int nbPages) {
        PdfDocument.Meta meta = pdfView.getDocumentMeta();
        Log.e(TAG, "title = " + meta.getTitle());
        Log.e(TAG, "author = " + meta.getAuthor());
        Log.e(TAG, "subject = " + meta.getSubject());
        Log.e(TAG, "keywords = " + meta.getKeywords());
        Log.e(TAG, "creator = " + meta.getCreator());
        Log.e(TAG, "producer = " + meta.getProducer());
        Log.e(TAG, "creationDate = " + meta.getCreationDate());
        Log.e(TAG, "modDate = " + meta.getModDate());

        printBookmarksTree(pdfView.getTableOfContents(), "-");

    }

    public void printBookmarksTree(List<PdfDocument.Bookmark> tree, String sep) {
        for (PdfDocument.Bookmark b : tree) {

            Log.e(TAG, String.format("%s %s, p %d", sep, b.getTitle(), b.getPageIdx()));

            if (b.hasChildren()) {
                printBookmarksTree(b.getChildren(), sep + "-");
            }
        }
    }

    /**
     * Listener for response to user permission request
     *
     * @param requestCode  Check that permission request code matches
     * @param permissions  Permissions that requested
     * @param grantResults Whether permissions granted
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                launchPicker();
            }
        }
    }

    @Override
    public void onPageError(int page, Throwable t) {
        Log.e(TAG, "Cannot load page " + page);
    }
}
