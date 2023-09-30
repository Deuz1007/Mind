package com.example.mind.utilities;

import android.content.Context;
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
        InputStream stream = context.getContentResolver().openInputStream(uri);
        assert stream != null;

        StringBuilder builder = new StringBuilder();

        PdfReader reader = new PdfReader(stream);
        int pages = reader.getNumberOfPages();

        for (int i = 1; i <= pages; i++) {
            builder.append(PdfTextExtractor.getTextFromPage(reader, i));
        }

        reader.close();

        return builder.toString();
    }

    public static String Word(Context context, Uri uri) throws IOException {
        InputStream stream = context.getContentResolver().openInputStream(uri);
        assert stream != null;

        Result<String> result = converter.extractRawText(stream);

        return result.getValue();
    }

    public static void Image(Context context, Uri uri, PostProcess callback) throws IOException {
        recognizer.process(InputImage.fromFilePath(context, uri))
                .addOnSuccessListener(text -> callback.Success(text.getText()))
                .addOnFailureListener(callback::Failed);
    }
}
