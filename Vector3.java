public class Vector3 {
    public double x;
    public double y;
    public double z;

    public Vector3(double x, double y, double z){
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double dot(Vector3 other){
        return (x * other.x) + (y * other.y) + (z * other.z);
    }

    public double lengthSquared(){
        return (x*x + y*y + z*z);
    }

    public double lenght(){
        return Math.sqrt(this.lengthSquared());
    }

    public Vector3 normalized(){
        double lenInv = 1.0/this.lenght();
        return new Vector3(x*lenInv, y*lenInv, z*lenInv);
    }
    /**
     * @return additive inverse of the Vector3
     */
    public Vector3 inverted(){
        return new Vector3(-x, -y, -z);
    }

    public Vector3 add(Vector3 other){
        return new Vector3(x + other.x, y + other.y, z + other.z);
    }

    public Vector3 sub(Vector3 other){
        return add(other.inverted());
    }

    public Vector3 mult(double t){
        return new Vector3(x * t, y * t, z * t);
    }

    public String toString(){
        return (x + " " + y + " " + z);
    }
}
