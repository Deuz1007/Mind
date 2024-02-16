package com.mindapps.mind.utilities;

public class UniqueID {
    public static final int BYTES_LENGTH = 16;
    private static final char[] ALPHABET = new char[] {
            'u', 's', 'e', 'a', 'n', 'd', 'o', 'm',
            '-', '2', '6', 'T', '1', '9', '8', '3',
            '4', '0', 'P', 'X', '7', '5', 'p', 'x',
            'J', 'A', 'C', 'K', 'V', 'E', 'R', 'Y',
            'M', 'I', 'N', 'D', 'B', 'U', 'S', 'H',
            'W', 'O', 'L', 'F', '_', 'G', 'Q', 'Z',
            'b', 'f', 'g', 'h', 'j', 'k', 'l', 'q',
            'v', 'w', 'y', 'z', 'r', 'i', 'c', 't'
    };

    public static String generate() {
        // Prepare char[] for id
        char[] id = new char[BYTES_LENGTH];

        for (int i = 0; i < BYTES_LENGTH; i++) {
            // Created random index
            int index = (int) (Math.random() * ALPHABET.length);

            // Assign the index-matched character
            id[i] = ALPHABET[index];
        }

        // Convert the char[] to String and return
        return new String(id);
    }
}
