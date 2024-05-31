import java.awt.image.BufferedImage;

public class Camera {
    public int width;
    public int height;
    public double halfW;//preprocessed values for faster calculations
    public double halfH;
    public double heightInv;//mult is faster than div
    public boolean isOrthogonal = false;

    public BufferedImage img;

    public Vector3 pos = new Vector3(0, 0, 0);

    public Camera(int resWidth, int resHeight){
        width = resWidth;
        height = resHeight;
        halfW = width/2.0;
        halfH = height/2.0;
        heightInv = 1.0/halfH;
        img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    }

    public Camera(int resWidth, int resHeight, float fov, Vector3 pos){
        width = resWidth;
        height = resHeight;
        halfW = width/2.0;
        halfH = height/2.0;
        heightInv = 1.0/halfH;
        this.pos = pos;
        img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    }
}
