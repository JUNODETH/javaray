import java.awt.Color;

public class ColorTools {
    public static Color lerpColors(Color col1, Color col2, float t){
        int d[] = {(col2.getRed() - col1.getRed()), (col2.getGreen() - col1.getGreen()), (col2.getBlue() - col1.getBlue())};
        Color outColor = new Color(col1.getRed() + Math.round(d[0] * t), col1.getGreen() + Math.round(d[1] * t), col1.getBlue() + Math.round(d[2] * t));
        return outColor;
    }

    public static int clampChannel(int in){
        if(in > 255){
            in = 255;
        }
        return in;
    }
}
