public abstract class Drawable {
    public Vector3 center;
    //public static int objCount = 0;
    public Material mat;
    public int index;

    public abstract boolean hitSimple(Ray r);
    public abstract double hit(Ray r);

    public abstract Vector3 getNormal(Vector3 point);

    public abstract Material getMaterial();

    public abstract HDRColor getColor(Vector3 point);

    /*public abstract int getIndex();
    public abstract int setIndex();*/
}
