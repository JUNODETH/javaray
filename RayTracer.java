import java.awt.Color;
import java.io.IOException;
import java.util.Vector;
import java.io.File;

import javax.imageio.ImageIO;

/**
 * RayTracer
 */
public class RayTracer {
    //funky shit
    HDRColor skytopColor = new HDRColor(new Color(0xFFD0D0FF), 1.0f);
    HDRColor skybottomColor = new HDRColor(new Color(0xFF201770), 1.0f);
    Material mat1 = new Material(new Color(0xFFF0A729), 1.0f);
    Material mat2 = new Material(new Color(0xFFB9CDD6), 1.0f);
    Material mat3 = new Material(new Color(0xFFDF00DF), 0.5f);
    Color ambientLighting = Color.BLACK;
    float lightIntesity = 1.0f;

    //float strongest = 0f;

    Vector3 directionalLight = new Vector3(-1, -2, 1).normalized();

    public static void main(String[] args) {
        RayTracer mainTracer = new RayTracer();
        mainTracer.runTracer();
    }

    /**
     * gets called from main, only because main is static and i dont want to only use statics
     */
    public void runTracer(){
        //Material mat1 = new Material(0xFFFF0000, 1.0f);

        Vector<Drawable> objects = new Vector<>();
        Sphere obj1 = new Sphere(new Vector3(0, 0, 5), 1, mat1);
        Sphere obj2 = new Sphere(new Vector3(2, -1, 4), 1.0, mat1);
        Sphere obj3 = new Sphere(new Vector3(-1, 1, 3), 1.0, mat3);
        Plane floor = new Plane(new Vector3(0, -2.0, 0), new Vector3(0, 1.0, 0), 2, mat2);

        objects.add(obj1);
        objects.add(obj2);
        objects.add(obj3);
        objects.add(floor);

        //instantiate scene
        Camera cam = new Camera(640, 640);
        //cam.isOrthogonal = true;

        //drawDepth(objects, cam);
        //drawNormal(objects, cam);
        drawColor(objects, cam);

        //everything gets saved in a buffered image and displayed
        try {
            File outputFile = new File("saved.png");
            ImageIO.write(cam.img, "png", outputFile);
        }
        catch (IOException e){
            System.out.println("womp womp, couldnt write file");
        }
    }

    void drawDepth(Vector<Drawable> objects, Camera cam){
        for(int i = 0; i < cam.width; i++){
            for(int j = 0; j < cam.height; j++){
                float closestDist = Float.MAX_VALUE;
                //Drawable closestObj = null;
                Vector3 dir = new Vector3((i-cam.halfW)*cam.heightInv, (cam.halfH-j)*cam.heightInv, 1);
                Ray ray = new Ray(cam.pos, dir.normalized());
                
                for (Drawable obj : objects) {
                    float t = (float) obj.hit(ray);
                    if(t > 0 && t < closestDist){
                        closestDist = t;
                        //closestObj = obj;
                    }
                }
                if (closestDist == Float.MAX_VALUE) {
                    closestDist = -1.0f;
                }
                
                if(closestDist <= 0){
                    cam.img.setRGB(i, j, 0xFFFFFFFF); //draw white
                }
                else{
                    float depthConstant = 3.0f; //needs to be > 0
                    float scaledDepth = (1 - (depthConstant / (closestDist + depthConstant)));
                    int gray = Math.round(scaledDepth * 255);
                    //System.out.print(t + " " + scaledDepth + " " + gray + " ");
                    cam.img.setRGB(i, j, new Color(gray, gray, gray).getRGB());
                }
            }
        }
    }

    void drawNormal(Vector<Drawable> objects, Camera cam){
        for(int i = 0; i < cam.width; i++){
            for(int j = 0; j < cam.height; j++){
                float closestDist = Float.MAX_VALUE;
                Drawable closestObj = null;
                Vector3 dir = new Vector3((i-cam.halfW)*cam.heightInv, (cam.halfH-j)*cam.heightInv, 1);
                Ray ray = new Ray(cam.pos, dir.normalized());
                
                for (Drawable obj : objects) {
                    float t = (float) obj.hit(ray);
                    if(t > 0 && t < closestDist){
                        closestDist = t;
                        closestObj = obj;
                    }
                }
                if (closestDist == Float.MAX_VALUE) {
                    closestDist = -1.0f;
                }
                if(closestDist <= 0){ //draw bg
                    cam.img.setRGB(i, j, 0xFF8080F0);
                }
                else{
                    Vector3 normal = closestObj.getNormal(cam.pos.add(ray.pointAt(closestDist)));
                    int xUV = (int) Math.round(Math.abs(normal.x) * 255);
                    int yUV = (int) Math.round(Math.abs(normal.y) * 255);
                    int zUV = (int) Math.round(Math.abs(normal.z) * 255);
                    Color normalCol = new Color(xUV, yUV, zUV);

                    cam.img.setRGB(i, j, normalCol.getRGB());
                }
            }
        }
    }

    void drawColor(Vector<Drawable> objects, Camera cam){
        for(int i = 0; i < cam.width; i++){
            for(int j = 0; j < cam.height; j++){
                HDRColor finalColor = new HDRColor(0, 0, 0);
                Ray ray;
                Vector3 dir = new Vector3((i-cam.halfW)*cam.heightInv, (cam.halfH-j)*cam.heightInv, 1).normalized();
                if(cam.isOrthogonal){
                    Vector3 start = cam.pos.add(new Vector3((i-cam.halfW)*cam.heightInv, (cam.halfH-j)*cam.heightInv, 0));
                    ray = new Ray(start, new Vector3(0, 0, 1));
                }
                else{
                    
                    ray = new Ray(cam.pos, dir);
                }

                //---------------SHOOTY PART--------------------
                Drawable closestObjAddress[] = { null }; //janky workaround to do C like pointer stuff but with single element arrays

                float closestDist = rayEach(objects, ray, closestObjAddress); 
                Drawable closestObj = closestObjAddress[0];
                //----------------------------------------------------------- repeat this for bouncing
                if(closestDist <= 0){ //hit sky
                    float t = ((float) ray.dir.y + 1.0f)*0.5f;
                    //System.out.println(t);
                    finalColor = HDRColor.lerpColors(skybottomColor, skytopColor, t);
                }
                else{
                    Vector3 end = cam.pos.add(ray.pointAt(closestDist));
                    Vector3 normal = closestObj.getNormal(end);
                    boolean blocked = false;
                    Ray endRay = new Ray(end, directionalLight.inverted());
                    for (Drawable obj : objects) {
                        if(obj != closestObj){ //dont check self, checking self fucks shit up cause imprecision
                            blocked = obj.hitSimple(endRay);
                            if(blocked){
                                finalColor = new HDRColor(0, 0, 0);
                                break;
                            }
                        }
                        
                    }
                    if(!blocked){
                        //Vector3 normal = closestObj.getNormal(end); //ignore warning, closestObj gets set in rayEach
                        float lightStrenght = (float) normal.dot(directionalLight.inverted());
                        HDRColor col = closestObj.getMaterial().getColor();
                        if(lightStrenght < 0.0f){
                            lightStrenght = 0f;
                        }
                        col = col.multiply(lightStrenght*lightIntesity);

                        finalColor = col;
                    }

                    if(closestObj.getMaterial().getRoughness() < 0.6f){ //hardcoded second bounce
                        //ray reflection: r = d - 2*(d,n)*n
                        dir = dir.sub(normal.mult(2 * dir.dot(normal)));
                        ray = new Ray(end, dir.normalized());

                        closestDist = rayEach(objects, ray, closestObjAddress); 
                        closestObj = closestObjAddress[0];

                        if(closestDist <= 0){ //hit sky
                            float t = ((float) ray.dir.y + 1.0f)*0.5f;
                            //System.out.println(t);
                            finalColor = finalColor.add(HDRColor.lerpColors(skybottomColor, skytopColor, t));
                        }
                        else{
                            end = cam.pos.add(ray.pointAt(closestDist));
                            blocked = false;
                            endRay = new Ray(end, directionalLight.inverted());
                            for (Drawable obj : objects) {
                                if(obj != closestObj){ //dont check self, checking self fucks shit up cause imprecision
                                    blocked = obj.hitSimple(endRay);
                                    if(blocked){
                                        //finalColor = finalColor.add(new HDRColor(0, 0, 0));
                                        break;
                                    }
                                }
                                
                            }
                            if(!blocked){
                                normal = closestObj.getNormal(end); //ignore warning, closestObj gets set in rayEach
                                float lightStrenght = (float) normal.dot(directionalLight.inverted());
                                HDRColor col = closestObj.getMaterial().getColor();
                                if(lightStrenght < 0.0f){
                                    lightStrenght = 0f;
                                }
                                col = col.multiply(lightStrenght*lightIntesity);
        
                                finalColor = finalColor.add(col);
                            }
                        }
                    }
                }
                cam.img.setRGB(i, j, HDRColor.hdrToSdr(finalColor.multiply(1.0f)).getRGB());
            }
        }
    }
    /**
     * Test a ray for every object, used to shoot from camera, do bounces and do final light check
     * @param objects
     * @param ray
     * @return
     */
    public float rayEach(Vector<Drawable> objects, Ray ray, Drawable[] closestObj){
        float closestDist = Float.MAX_VALUE;
        
        for (Drawable obj : objects) {
            Drawable startObj = closestObj[0];
            if(obj != startObj){
                float t = (float) obj.hit(ray);
                if(t > 0 && t <= closestDist){
                    closestDist = t;
                    closestObj[0] = obj;
                }
            }
        }
        if (closestDist == Float.MAX_VALUE) {
            closestDist = -1.0f;
        }

        return closestDist;
    }
}