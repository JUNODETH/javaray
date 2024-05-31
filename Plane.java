public class Plane extends Drawable {
    public Vector3 center;
    private Vector3 normal;
    public double size;

    public Material mat;
    /**
     * 
     * @param center
     * @param normal Gets normalized within the constructer
     */
    public Plane(Vector3 center, Vector3 normal, double size, Material mat){
        this.center = center;
        this.normal = normal.normalized();
        this.size = size;
        this.mat = mat;
    }

    public double hit(Ray r){
        double a = normal.dot(center.sub(r.origin));
        double b = normal.dot(r.dir);
        return a/b;
    }
    public boolean hitSimple(Ray r){
        double a = normal.dot(center.sub(r.origin));
        double b = normal.dot(r.dir);
        return (a/b > 0);
    }

    public Vector3 getNormal(Vector3 point){
        return normal;
    }

    public Material getMaterial(){
        return mat;
    }

    public HDRColor getColor(Vector3 point){
        HDRColor black = new HDRColor(0f, 0f, 0f);
        float t = (float) point.x;
        float u = (float) point.z;
        t+= Math.round(u);
        if (t < 0f){
            t = -t + 1f;
        }
        
        t %= 2f;
        t *= 0.5f;
        t = Math.round(t);
        return HDRColor.lerpColors(black, mat.getColor(), t);
    }
}
