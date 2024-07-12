package imageprocessing;

public class Utils {
    public static int getRed(int rgb) {
        return (rgb & 0x00FF0000) >> 16;
    }

    public static int getGreen(int rgb) {
        return (rgb & 0x0000FF00) >> 8;
    }

    public static int getBlue(int rgb) {
        return (rgb & 0x000000FF);
    }

    public static int getRGBFromColors(int r, int g, int b) {
        int rgb = 0;

        rgb |= b;
        rgb |= g << 8;
        rgb |= r << 16;

        rgb |= 0xFF000000;

        return rgb;
    }

    public static boolean isShadeOfGray(int red, int green, int blue) {
        return Math.abs(red - green) < 30 && Math.abs(red - blue) < 30 && Math.abs(green - blue) < 30;
    }
}
