package com.example.mind.utilities;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.OpenableColumns;

import com.example.mind.exceptions.FileSizeLimitException;
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
    private static final double FILE_SIZE_LIMIT = 1024 * 1024; //1 MiB

    private static void checkFileSize(ContentResolver resolver, Uri uri) throws FileSizeLimitException {
        Cursor cursor = resolver.query(uri, null, null, null, null);

        int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
        cursor.moveToFirst();

        if (cursor.getDouble(sizeIndex) > FILE_SIZE_LIMIT)
            throw new FileSizeLimitException();
    }

    public static String PDF(Context context, Uri uri) throws IOException, FileSizeLimitException {
        // Check file size
        checkFileSize(context.getContentResolver(), uri);

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

    public static String Word(Context context, Uri uri) throws IOException, FileSizeLimitException {
        // Check file size
        checkFileSize(context.getContentResolver(), uri);

        // Create an InputStream for the Uri
        InputStream stream = context.getContentResolver().openInputStream(uri);
        assert stream != null;

        // Extract the raw text
        Result<String> result = converter.extractRawText(stream);

        // Close stream
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
