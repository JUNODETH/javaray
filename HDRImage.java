public class HDRImage {
    public int width;
    public int height;
    public HDRColor[][] pixels;

    public HDRImage(int width, int height){
        pixels = new HDRColor[width][height];
        this.width = width;
        this.height = height;
    }

    public HDRImage convolve(float[][] kernel){
        HDRImage res = new HDRImage(width, height);
        int kernelSize = kernel[0].length;
        int offset = kernelSize/2;

        for(int i = 0; i < width; i++){
            for(int j = 0; j < height; j++){
                res.pixels[i][j] = convolvePixel(i, j, kernel, offset, kernelSize);
            }
        }

        return res;
    }

    private HDRColor convolvePixel(int x, int y, float[][] kernel, int offset, int kernelSize){
        HDRColor outCol = new HDRColor(0, 0, 0);

        for(int i = 0 ; i < kernelSize; i++){
            for(int j = 0; j < kernelSize; j++){
                int xUV = x+i-offset;
                int yUV = y+j-offset;
                if(xUV < 0 || xUV >= width || yUV < 0 || yUV >= height){
                    outCol = outCol.add(pixels[x][y].multiply(kernel[i][j]));
                }
                else{
                    outCol = outCol.add(pixels[xUV][yUV].multiply(kernel[i][j]));
                }
            }
        }

        return outCol;
    }
}
