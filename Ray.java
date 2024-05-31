public class Ray {
    public Vector3 origin;
    public Vector3 dir;
    float t;

    public Ray(Vector3 origin, Vector3 dir){
        this.origin = origin;
        this.dir = dir;
    }

    public Vector3 pointAt(double t){
        return origin.add(dir.mult(t));
    }
}
