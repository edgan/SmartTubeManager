package org.cygnus.smarttubemanager;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_PERMISSIONS = 100;
    private static final String ARCH = "arm64-v8a";
    private static final String CHANNEL = "beta";
    private static final String API_URL = "https://api.github.com/repos/yuliskov/SmartTube/releases";
    private List<Map.Entry<String, String>> builds = new ArrayList<>();
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Set the content view


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false); // To avoid default title
        }

        checkPermissions();

        // Find the button by ID
        Button uninstallButton = findViewById(R.id.uninstall_button);

        // Set an OnClickListener to trigger the uninstall
        uninstallButton.setOnClickListener(v -> uninstallApp());

        fetchReleases();
    }

    private static final int UNINSTALL_REQUEST_CODE = 1;

    private final ActivityResultLauncher<Intent> uninstallLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Log.d(TAG, "Uninstallation successful");
                    Toast.makeText(this, "App uninstalled successfully", Toast.LENGTH_SHORT).show();
                    // Optional: finish activity or perform cleanup
                    finish();
                } else {
                    Log.d(TAG, "Uninstallation failed or was cancelled");
                    Toast.makeText(this, "Uninstall cancelled or failed", Toast.LENGTH_SHORT).show();
                }
            }
    );
    
    private void uninstallApp() {
        String packageName = "com.liskovsoft.smarttubetv.beta";
        Intent uninstallIntent = new Intent(Intent.ACTION_DELETE);
        uninstallIntent.setData(Uri.parse("package:" + packageName));

        try {
            // Modern way to check if intent can be handled
            if (!getPackageManager().queryIntentActivities(uninstallIntent,
                    PackageManager.MATCH_DEFAULT_ONLY).isEmpty()) {
                Log.d(TAG, "Starting uninstallation for package: " + packageName);
                uninstallLauncher.launch(uninstallIntent);
            } else {
                Log.e(TAG, "Uninstall intent could not be resolved.");
                Toast.makeText(this, "Uninstall not supported on this device.", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error starting uninstall", e);
            Toast.makeText(this, "Failed to start uninstall process", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == UNINSTALL_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Uninstallation completed successfully.", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Uninstallation canceled.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Uninstallation failed.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void createReleaseSelectionScreen() {
        LinearLayout buttonContainer = findViewById(R.id.button_container);

        if (buttonContainer == null) {
            Log.e(TAG, "createReleaseSelectionScreen: buttonContainer not found in layout.");
            return;
        }

        // Get the screen width
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;

        // Calculate 20% of screen width
        int buttonWidth = screenWidth / 5;

        for (Map.Entry<String, String> entry : builds) {
            String version = entry.getKey();
            String downloadUrl = entry.getValue();
            Log.d(TAG, "Adding button for version: " + version + " with URL: " + downloadUrl);

            Button button = new Button(this);
            button.setText(version);
            button.setBackgroundResource(R.drawable.button_selector);

            // Create layout parameters for the button
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    buttonWidth,  // Width is 20% of screen
                    LinearLayout.LayoutParams.WRAP_CONTENT  // Height wraps content
            );

            // Add margins if desired (optional)
            params.setMargins(0, 8, 0, 8);  // top and bottom margins of 8dp

            // Center the button horizontally
            params.gravity = Gravity.CENTER_HORIZONTAL;

            button.setLayoutParams(params);

            button.setOnClickListener(v -> {
                Log.d(TAG, "Button clicked for version: " + version);
                showConfirmationDialog(version, downloadUrl);
            });

            buttonContainer.addView(button);
        }
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_PERMISSIONS);
        }
    }


    private void fetchReleases() {
        new Thread(() -> {
            try {
                URL url = new URL(API_URL);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                if (urlConnection.getResponseCode() == 200) {
                    StringBuilder response = new StringBuilder();
                    Scanner scanner = new Scanner(urlConnection.getInputStream());
                    while (scanner.hasNext()) {
                        response.append(scanner.nextLine());
                    }
                    scanner.close();

                    parseReleases(response.toString());
                } else {
                    Log.e(TAG, "Error fetching releases. Response code: " + urlConnection.getResponseCode());
                }

            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage(), e);
            }
        }).start();
    }

    private void parseReleases(String releasesJson) {
        try {
            JSONArray releases = new JSONArray(releasesJson);
            for (int i = 0; i < releases.length(); i++) {
                JSONObject release = releases.getJSONObject(i);
                String releaseName = release.getString("name").toLowerCase();

                if (releaseName.contains(CHANNEL)) {
                    JSONArray assets = release.getJSONArray("assets");
                    for (int j = 0; j < assets.length(); j++) {
                        JSONObject asset = assets.getJSONObject(j);
                        String downloadUrl = asset.getString("browser_download_url");

                        if (downloadUrl.contains(ARCH)) {
                            builds.add(Map.entry(releaseName, downloadUrl));
                        }
                    }
                }
            }

            int numberOfBuilds = 5;

            // Sort builds in descending order by version and truncate to 9 entries
            builds.sort((entry1, entry2) -> extractVersionNumber(entry2.getKey()) - extractVersionNumber(entry1.getKey()));
            if (builds.size() > numberOfBuilds) {
                builds = builds.subList(0, numberOfBuilds);
            }

        } catch (JSONException e) {
            Log.e(TAG, "JSON Exception: " + e.getMessage());
        }

        runOnUiThread(this::createReleaseSelectionScreen);
    }

    private int extractVersionNumber(String version) {
        String numberPart = version.replaceAll("[^0-9]", "");
        return numberPart.isEmpty() ? 0 : Integer.parseInt(numberPart);
    }

    private void showConfirmationDialog(String version, String downloadUrl) {
        new AlertDialog.Builder(this)
                .setTitle("Confirmation")
                .setMessage(Html.fromHtml("You have to uninstall SmartTube before you can install an apk, if it is a downgrade. Otherwise the downgrade will fail. This is a limitation of Android.\n\n<p style=\"color:red;\"><b>You should use the backup feature built into SmartTube to backup your settings.</b></p>\n\nAre you ready to proceed?", Html.FROM_HTML_MODE_LEGACY))
                .setPositiveButton("Yes", (dialog, which) -> downloadAndInstallApk(downloadUrl, version))
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss()) // Dismiss the dialog
                .show();
    }


    private void downloadAndInstallApk(String downloadUrl, String version) {

        new Thread(() -> {
            try {
                Log.d(TAG, "Downloading APK for version: " + version + " from URL: " + downloadUrl);

                // Create URL and open connection
                URL url = new URL(downloadUrl);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();

                // File where the APK will be saved
                File apkFile = new File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), version + ".apk");

                // Stream the APK download
                BufferedInputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
                FileOutputStream fileOutputStream = new FileOutputStream(apkFile);

                // Buffer for read/write
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    fileOutputStream.write(buffer, 0, bytesRead);
                }

                // Close the streams
                fileOutputStream.close();
                inputStream.close();

                // Install the downloaded APK
                installApk(apkFile);

            } catch (IOException e) {
                Log.e(TAG, "Download Error for version " + version + ": " + e.getMessage());
            }
        }).start();
    }

    private void installApk(File apkFile) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri apkUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", apkFile);
        intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchReleases();
            } else {
                Toast.makeText(this, "Permissions required to download and install APKs", Toast.LENGTH_SHORT).show();
            }
        }
    }
}