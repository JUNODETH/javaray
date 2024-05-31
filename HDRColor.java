import java.awt.Color;

public class HDRColor {
    public float r, g, b; //0 -black, 1 -white, >1 -HDR stuff

    public HDRColor(float r,float g,float b){
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public HDRColor(Color col, float intensity){
        float inv = intensity/255.0f;
        this.r = col.getRed() * inv;
        this.g = col.getGreen() * inv;
        this.b = col.getBlue() * inv;
    }

    public HDRColor add(HDRColor o){
        return new HDRColor(r + o.r, g + o.g, b + o.b);
    }

    public HDRColor multiply(HDRColor o){
        return new HDRColor(r * o.r, g * o.g, b * o.b);
    }

    public HDRColor multiply(float o){
        return new HDRColor(r * o, g * o, b * o);
    }

    public static HDRColor sdrToHdr(Color c){
        float inv = 1.0f/255.0f;
        return new HDRColor(c.getRed() * inv, c.getGreen() * inv, c.getBlue() * inv);
    }
    /**
     * hdr to sdr by way of clamping
     * @param c
     * @return
     */
    public static Color hdrToSdr(HDRColor c){
        int red = ColorTools.clampChannel(Math.round(c.r*255));
        int green = ColorTools.clampChannel(Math.round(c.g*255));
        int blue = ColorTools.clampChannel(Math.round(c.b*255));
        return new Color(red, green, blue);
    }
    /**
     * hdr to sdr by way of clamping but the values get scaled first (exposure)
     * @param c
     * @param scalar
     * @return
     */
    public static Color hdrToSdr(HDRColor c, float scalar){
        int red = ColorTools.clampChannel(Math.round(c.r*scalar*255));
        int green = ColorTools.clampChannel(Math.round(c.g*scalar*255));
        int blue = ColorTools.clampChannel(Math.round(c.b*scalar*255));
        return new Color(red, green, blue);
    }

    public HDRColor clone(){
        return new HDRColor(r, g, b);
    }

    public static HDRColor lerpColors(HDRColor col1, HDRColor col2, float t){
        float d[] = {(col2.r - col1.r), (col2.g - col1.g), (col2.b - col1.b)};
        HDRColor outColor = new HDRColor(col1.r + d[0]*t, col1.g + d[1]*t, col1.b + d[2]*t);
        return outColor;
    }
}
