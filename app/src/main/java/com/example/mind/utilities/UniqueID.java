package com.example.mind.utilities;

import java.util.Date;

public class UniqueID {
    private static final int BYTES_LENGTH = 10;
    private static final char[] ALPHABET = new char[] {'u','s','e','a','n','d','o','m','-','2','6','T','1','9','8','3','4','0','P','X','7','5','p','x','J','A','C','K','V','E','R','Y','M','I','N','D','B','U','S','H','W','O','L','F','_','G','Q','Z','b','f','g','h','j','k','l','q','v','w','y','z','r','i','c'};

    private static String getDateString() {
        // Get the current time
        long date = new Date().getTime();
        // Convert the long time to hex string
        String dateString = Long.toHexString(date);

        // Preparation for hex string conversion
        // If the hex string's length is odd number, add "0" to front
        if (dateString.length() % 2 == 1)
            dateString = "0" + dateString;
        // Convert the modified (if modified) dateString to char[]
        char[] dateChars = dateString.toCharArray();

        // Prepare converted date array
        char[] dateToAppend = new char[6];

        // Traverse through dateChars 2 characters at a time
        for (int i = 0; i < dateChars.length; i += 2) {
            // Append the 2 chars into string
            String hex = "" + dateChars[i] + dateChars[i + 1];

            // Convert hex into int
            int hexInt = Integer.parseInt(hex, 16);
            // Get the mod 63 of the hexInt
            int alphabetIndex = hexInt % 63;

            // Add the index-matched character
            dateToAppend[i / 2] = ALPHABET[alphabetIndex];
        }

        return new String(dateToAppend);
    }

    public static String generate() {
        // Prepare char[] for id
        char[] id = new char[BYTES_LENGTH];

        for (int i = 0; i < BYTES_LENGTH; i++) {
            // Created random index
            int index = (int) (Math.random() * ALPHABET.length);
            // Assign the index-matched character
            id[i] = ALPHABET[index];
        }

        // Get string date
        String date = getDateString();

        // Append the date to generated id, then return;
        return new String(id) + date;
    }
}
