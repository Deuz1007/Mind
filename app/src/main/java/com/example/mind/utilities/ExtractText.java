package com.example.mind.utilities;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;

import com.example.mind.interfaces.PostProcess;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

import org.zwobble.mammoth.DocumentConverter;
import org.zwobble.mammoth.Result;

import java.io.IOException;
import java.io.InputStream;

public class ExtractText {
    private static final TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
    private static final DocumentConverter converter = new DocumentConverter();

    public static String PDF(Context context, Uri uri) throws IOException {
        // Create an InputStream for the Uri
        InputStream stream = context.getContentResolver().openInputStream(uri);
        assert stream != null;

        // String where the read text are appended
        StringBuilder builder = new StringBuilder();

        // Create a reader instance from the InputStream
        PdfReader reader = new PdfReader(stream);

        // Loop through each page and extract the text
        for (int i = 1; i <= reader.getNumberOfPages(); i++)
            // Append the text to the builder
            builder.append(PdfTextExtractor.getTextFromPage(reader, i));

        // Close streams
        reader.close();
        stream.close();

        // Return the string
        return builder.toString();
    }

    public static String Word(Context context, Uri uri) throws IOException {
        // Create an InputStream for the Uri
        InputStream stream = context.getContentResolver().openInputStream(uri);
        assert stream != null;

        // Extract the raw text
        Result<String> result = converter.extractRawText(stream);

        // Close streams
        stream.close();

        // Return the extracted text
        return result.getValue();
    }

    public static void Image(Context context, Uri uri, PostProcess callback) throws IOException {
        recognizer.process(InputImage.fromFilePath(context, uri))
                .addOnSuccessListener(text -> callback.Success(text.getText()))
                .addOnFailureListener(callback::Failed);
    }

    public static void Image(Bitmap bitmap, PostProcess callback) {
        recognizer.process(InputImage.fromBitmap(bitmap, 0))
                .addOnSuccessListener(text -> callback.Success(text.getText()))
                .addOnFailureListener(callback::Failed);
    }
}
