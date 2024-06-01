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
    HDRColor skytopColor = new HDRColor(new Color(0xFFD0D0FF), 1.5f);
    HDRColor skybottomColor = new HDRColor(new Color(0xFF201770), 1.0f);
    Material mat1 = new Material(new Color(0xFFF0A729), 1f);
    Material mat2 = new Material(new Color(0xFF5050AF), 1f);
    Material mat3 = new Material(new Color(0xFFFFFFFF), 0.0f);
    Material mat4 = new Material(new Color(0xFF00FF00), 0.0f);
    int maxBounces = 1;
    float ambientIntensity = 0.1f;
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
        Sphere obj2 = new Sphere(new Vector3(2, -1, 4), 1.0, mat3);
        Sphere obj3 = new Sphere(new Vector3(-1, 1, 3), 1.0, mat1);
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
                HDRColor finalColor = new HDRColor(1f, 1f, 1f);
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
                //init a few things
                float closestDist;
                Drawable closestObj;
                float rayDecay = 1.0f; //light gets absorb when bouncing, this multiplier make the ray weaker
                //----------------------------------------------------------- repeat this for bouncing
                for (int k = 0; k <= maxBounces; k++) {
                    closestDist = rayEach(objects, ray, closestObjAddress); 
                    closestObj = closestObjAddress[0];
                    

                    if(closestDist <= 0){ //hit sky
                        float t = ((float) ray.dir.y + 1.0f)*0.5f;
                        //System.out.println(t);
                        finalColor = finalColor.multiply(HDRColor.lerpColors(skybottomColor, skytopColor, t));
                        finalColor = finalColor.multiply(rayDecay);
                        break;
                    }
                    else{
                        Vector3 end = cam.pos.add(ray.pointAt(closestDist));
                        Vector3 normal = closestObj.getNormal(end);
                        boolean blocked = false;
                        Ray endRay = new Ray(end, directionalLight.inverted());
                        for (Drawable obj : objects) { //calculates if the pixel is in shadow
                            if(obj != closestObj){ //dont check self, checking self fucks shit up cause imprecision
                                blocked = obj.hitSimple(endRay);
                                if(blocked){
                                    HDRColor col = closestObj.getColor(end);
                                    finalColor = finalColor.multiply(col);
                                    finalColor = finalColor.multiply(skybottomColor.multiply(ambientIntensity)); //shadow can have a little ambience
                                    break;
                                }
                            }
                        }
                        float roughness = closestObj.getMaterial().getRoughness();
                        if(!blocked){
                            //normal = closestObj.getNormal(end); //ignore warning, closestObj gets set in rayEach
                            float lightStrenght = (float) normal.dot(directionalLight.inverted());
                            HDRColor col = closestObj.getColor(end);
                            if(lightStrenght < 0.0f){
                                lightStrenght = 0f;
                            }
                            col = col.multiply(lightStrenght*lightIntesity*rayDecay);
                            float t = ((float) normal.y + 1.0f)*0.5f; //ambient color
                            col = col.add(HDRColor.lerpColors(skybottomColor, skytopColor, t).multiply(ambientIntensity*(1-lightStrenght)));
    
                            finalColor = finalColor.multiply(col);
                        }
                        if(roughness < 0.95f){ //do the bounce
                            //ray reflection: r = d - 2*(d,n)*n
                            dir = dir.sub(normal.mult(2 * dir.dot(normal)));
                            ray = new Ray(end, dir.normalized());
                            //create new ray and start again
                            float a = 4f;
                            //rayDecay = a/(k+1.0f + a);
                        } 
                        else {
                            //finalColor = finalColor.multiply(maxBounces);
                            break;
                        } //no more boing boing :(
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