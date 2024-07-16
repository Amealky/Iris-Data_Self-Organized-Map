package bm.classification.som.irisdatasom;

public class Utils {

    public static double[] hexToRGBA(String hexColor) {
        if (hexColor.startsWith("#")) {
            hexColor = hexColor.substring(1);
        }
        
        int r = Integer.parseInt(hexColor.substring(0, 2), 16);
        int g = Integer.parseInt(hexColor.substring(2, 4), 16);
        int b = Integer.parseInt(hexColor.substring(4, 6), 16);

        return new double[]{r / 255.0, g / 255.0, b / 255.0, 1.0};
    }

}
