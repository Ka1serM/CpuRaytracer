package lights;

import utils.RgbColor;
import utils.algebra.Vec3;

public class Light {
    private final Vec3 position;
    private final RgbColor color;
    private final float intensity;
    private final float radius;


    public Light(Vec3 position, RgbColor color, float intensity, float radius) {
        this.position = position;
        this.color = color;
        this.intensity = intensity;
        this.radius = radius;
    }

    public Vec3 getPosition(){
        return this.position;
    }

    public RgbColor getColor(){
        return this.color;
    }

    public float getIntensity() {
        return intensity;
    }

    public float getRadius() {
        return radius;
    }
}

