package com.phaseos.utils;

public enum Color {
    WHITE, ORANGE, MAGENTA, LIGHT_BLUE,
    YELLOW, LIME, PINK, GRAY, LIGHT_GRAY,
    CYAN, PURPLE, BLUE, BROWN, GREEN, RED,
    BLACK;

    public static int getColor(Color color) {
        if (color == WHITE) return 0;
        else if (color == ORANGE) return 1;
        else if (color == MAGENTA) return 2;
        else if (color == LIGHT_BLUE) return 3;
        else if (color == YELLOW) return 4;
        else if (color == LIME) return 5;
        else if (color == PINK) return 6;
        else if (color == GRAY) return 7;
        else if (color == LIGHT_GRAY) return 8;
        else if (color == CYAN) return 9;
        else if (color == PURPLE) return 10;
        else if (color == BLUE) return 11;
        else if (color == BROWN) return 12;
        else if (color == GREEN) return 13;
        else if (color == RED) return 14;
        else if (color == BLACK) return 15;
        else return 0;
    }
}
