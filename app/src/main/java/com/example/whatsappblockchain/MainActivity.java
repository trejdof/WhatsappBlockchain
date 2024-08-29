package com.example.whatsappblockchain;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.database.Cursor;
import android.provider.OpenableColumns;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private ActivityResultLauncher<String[]> selectZipFileLauncher;
    private TextView selectedFileTextView;
    private Uri selectedZipUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        selectedFileTextView = findViewById(R.id.selected_file_text);
        Button selectZipButton = findViewById(R.id.select_zip_button);
        Button calculateButton = findViewById(R.id.calculate_button);

        selectZipFileLauncher = registerForActivityResult(
                new ActivityResultContracts.OpenDocument(),
                this::onZipFileSelected
        );

        selectZipButton.setOnClickListener(v -> selectZipFile());

        calculateButton.setOnClickListener(v -> {
            try {
                File textFile = extractTextFile();
                if (textFile != null) {
                    parseTextFile(textFile);
                } else {
                    Log.e(TAG, "No text file found in ZIP");
                }
            } catch (Exception e) {
                Log.e(TAG, "Error extracting or parsing text file", e);
            }
        });
    }

    private void selectZipFile() {
        selectZipFileLauncher.launch(new String[]{"application/zip"});
    }

    private void onZipFileSelected(Uri uri) {
        if (uri != null) {
            selectedZipUri = uri;
            String fileName = getFileName(uri);
            selectedFileTextView.setText("Selected file: " + fileName);
        }
    }

    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (nameIndex != -1) {
                        result = cursor.getString(nameIndex);
                    }
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    private File extractTextFile() throws Exception {
        if (selectedZipUri == null) {
            Log.e(TAG, "No ZIP file selected");
            return null;
        }

        File tempDir = getCacheDir(); // Temporary directory for extracted files
        File tempFile = new File(tempDir, "extractedTextFile.txt");

        try (InputStream inputStream = getContentResolver().openInputStream(selectedZipUri);
             ZipInputStream zipInputStream = new ZipInputStream(inputStream)) {

            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                if (entry.getName().endsWith(".txt")) { // Looking for .txt files
                    try (FileOutputStream outputStream = new FileOutputStream(tempFile)) {
                        byte[] buffer = new byte[1024];
                        int length;
                        while ((length = zipInputStream.read(buffer)) > 0) {
                            outputStream.write(buffer, 0, length);
                        }
                        return tempFile;
                    }
                }
            }
        }
        return null;
    }


    private void parseTextFile(File textFile) {
        if (textFile == null || !textFile.exists()) {
            Log.e(TAG, "Text file is null or does not exist");
            return;
        }

        // Use a LinkedList to represent the blockchain
        LinkedList<Block> blockChain = new LinkedList<>();

        // Use a HashMap to store the sum of numbers for each person
        HashMap<String, Integer> personSums = new HashMap<>();

        Pattern pattern = Pattern.compile("\\b\\d+\\b"); // Regular expression to find numbers

        try (BufferedReader reader = new BufferedReader(new FileReader(textFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Skip lines that do not contain ":"
                if (!line.contains(":")) {
                    continue;
                }

                // Split the line by the first " - " to remove the date
                String[] parts = line.split(" - ", 2);
                if (parts.length < 2) {
                    continue; // Skip lines that do not have the expected format
                }

                // The remaining part is the name and message
                String messagePart = parts[1];

                // Split the part by the first ": " to separate the name and the message
                String[] nameAndMessage = messagePart.split(": ", 2);
                if (nameAndMessage.length < 2) {
                    continue; // Skip lines that do not have the expected format
                }

                String personName = nameAndMessage[0];
                String message = nameAndMessage[1];

                // Extract the number from the message using regex
                Matcher matcher = pattern.matcher(message);
                if (matcher.find()) {
                    int number = Integer.parseInt(matcher.group());
                    blockChain.add(new Block(personName, String.valueOf(number))); // Add a new block to the LinkedList

                    // Update the sum for this person
                    personSums.put(personName, personSums.getOrDefault(personName, 0) + number);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error reading text file", e);
        }

        // For testing purposes, print the blockchain
        for (Block block : blockChain) {
            Log.d("BlockChain", "Person: " + block.personName + ", Number: " + block.extractedNumber);
        }

        // Display the sum for each person in the TextView
        StringBuilder result = new StringBuilder();
        for (String person : personSums.keySet()) {
            result.append("Person: ").append(person).append(", Sum: ").append(personSums.get(person)).append("\n");
        }

        // Find the TextView and set the result text
        TextView sumTextView = findViewById(R.id.sum_text_view);
        sumTextView.setText(result.toString());
    }


    public class Block {
        String personName;
        String extractedNumber;

        public Block(String personName, String extractedNumber) {
            this.personName = personName;
            this.extractedNumber = extractedNumber;
        }
    }



}
