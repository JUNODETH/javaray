import java.awt.Color;

public class Material {
    private HDRColor color;
    /**
     * roughness represented as a foat between 0 and 1
     * 0: 100% reflective
     * 1: no reflection
     */
    private float roughness;

    public Material(HDRColor color, float roughness){
        this.color = color;
        this.roughness = roughness;
    }

    public Material(Color color, float roughness){
        this.color = new HDRColor(color, 1.0f);
        this.roughness = roughness;
    }

    public float getRoughness() {
        return roughness;
    }
    /**
     * Set roughness
     * @param roughness A float in range [0.0;1.0], gets automatically clamped
     */
    public void setRoughness(float roughness) {
        if(roughness > 1.0f){
            this.roughness = 1.0f;
        }
        else if(roughness < 0.0f){
            this.roughness = 0.0f;
        }
        else{
            this.roughness = roughness;
        }
    }

    public HDRColor getColor(){
        return color.clone();
    }
}
