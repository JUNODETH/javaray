public class Sphere extends Drawable{
    public Vector3 center;
    public double radius;

    public Material mat;

    //public static int objCount = 0;
    public int index;

    public Sphere(Vector3 center, double radius, Material mat){
        this.center = center;
        this.radius = radius;
        this.mat = mat;
    }

    public boolean hitSimple(Ray r){ //returns whether an intersection happened, used for things like casting toward light
        Vector3 oc = r.origin.sub(center);
        double a = r.dir.dot(r.dir);
        double b = 2.0 * oc.dot(r.dir);
        double c = oc.dot(oc) - radius*radius;
        double discriminant = b*b - 4*a*c;

        //knowing if there was a hit isnt enough, because the hit could be behind

        if(discriminant < 0){
            return false; //no hit
        }
        else{ //wurzel ist plus/minus, es gibt zwar meistens zwei schnittpunkte, aber nur der nähste wird gesucht -> nur der minus fall betrachtet
            return (((-b - Math.sqrt(discriminant)) / (2.0*a)) >= 0f);
        }
    }
    public double hit(Ray r){ //returns distance to camera
        Vector3 oc = r.origin.sub(center);
        double a = r.dir.dot(r.dir);
        double b = 2.0 * oc.dot(r.dir);
        double c = oc.dot(oc) - radius*radius;
        double discriminant = b*b - 4*a*c;

        if(discriminant < 0){
            return -1.0; //no hit
        }
        else{ //wurzel ist plus/minus, es gibt zwar meistens zwei schnittpunkte, aber nur der nähste wird gesucht -> nur der minus fall betrachtet
            return (-b - Math.sqrt(discriminant)) / (2.0*a);
        }
    }

    //returns the normal at the point of a sphere
    public Vector3 getNormal(Vector3 point){
        return point.sub(center).normalized();
    }

    public Material getMaterial(){
        return mat;
    }

    public HDRColor getColor(Vector3 point){
        return mat.getColor();
    }
}