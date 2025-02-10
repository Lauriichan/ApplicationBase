package me.lauriichan.applicationbase.app.util.color;

import java.awt.Color;

public final class ColorParser {

    private ColorParser() {
        throw new UnsupportedOperationException();
    }

    public static String asString(final Color color) {
        return '#' + Integer.toString(color.getRed(), 16) + Integer.toString(color.getGreen(), 16) + Integer.toString(color.getBlue(), 16);
    }
    
    public static Color parse(final String hex, final Color fallback) {
        Color output = parse(hex);
        if (output == null) {
            return fallback;
        }
        return output;
    }

    public static Color parse(final String hex) {
        if (hex == null || hex.isBlank()) {
            return null;
        }
        if (hex.startsWith("#")) {
            return parse(hex.substring(1));
        }
        final int length = hex.length();
        int alpha = 0, red = 0, green = 0, blue = 0;
        switch (length) {
        case 1:
            red = green = blue = parseIntTwice(hex);
            alpha = 255;
            break;
        case 2:
            red = green = blue = parseIntTwice(hex.substring(0, 1));
            alpha = parseIntTwice(hex.substring(1, 2));
            break;
        case 3:
            red = parseIntTwice(hex.substring(0, 1));
            green = parseIntTwice(hex.substring(1, 2));
            blue = parseIntTwice(hex.substring(2, 3));
            alpha = 255;
            break;
        case 4:
            red = parseIntTwice(hex.substring(0, 1));
            green = parseIntTwice(hex.substring(1, 2));
            blue = parseIntTwice(hex.substring(2, 3));
            alpha = parseIntTwice(hex.substring(3, 4));
            break;
        case 5:
            red = parseIntTwice(hex.substring(0, 1));
            green = parseIntTwice(hex.substring(1, 2));
            blue = parseIntTwice(hex.substring(2, 3));
            alpha = parseInt(hex.substring(3, 5));
            break;
        case 6:
            red = parseInt(hex.substring(0, 2));
            green = parseInt(hex.substring(2, 4));
            blue = parseInt(hex.substring(4, 6));
            alpha = 255;
            break;
        case 7:
            red = parseIntTwice(hex.substring(0, 1));
            green = parseInt(hex.substring(1, 3));
            blue = parseInt(hex.substring(3, 5));
            alpha = parseInt(hex.substring(5, 7));
            break;
        case 8:
            red = parseInt(hex.substring(0, 2));
            green = parseInt(hex.substring(2, 4));
            blue = parseInt(hex.substring(4, 6));
            alpha = parseInt(hex.substring(6, 8));
            break;
        default:
            return null;
        }
        return new Color(red, green, blue, alpha);
    }
    
    private static int parseIntTwice(String value) {
        return parseInt(value + value);
    }
    
    private static int parseInt(String value) {
        try {
            return Integer.parseInt(value, 16);
        } catch(NumberFormatException nfe) {
            return 0;
        }
    }

}