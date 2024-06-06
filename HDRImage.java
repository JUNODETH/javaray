public class HDRImage {
    public int width;
    public int height;
    public HDRColor[][] pixels;

    public HDRImage(int width, int height){
        pixels = new HDRColor[width][height];
        this.width = width;
        this.height = height;
    }
}
