package materials;

import lights.Light;
import models.ray.Ray;
import utils.RgbColor;
import utils.algebra.Vec3;

import java.util.List;
import java.util.Random;

import static models.ray.RayUtils.EPSILON;
import static models.ray.RayUtils.randomInHemisphere;

public class LambertMaterial implements Material {
    protected final RgbColor albedo;
    protected final Random random;

    public LambertMaterial(RgbColor diffuseColor) {
        this.albedo = diffuseColor;
        this.random = new Random();
    }

    @Override
    public RgbColor sampleColor(Vec3 position, Vec3 normal, Vec3 viewDir, List<Light> lights) {
        RgbColor accumulatedColor = new RgbColor(0, 0, 0);

        for (Light light : lights) {

            Vec3 lightDirection = light.getPosition().sub(position).normalize();
            float distance = light.getPosition().sub(position).length();

            //inverse square law
            float intensity = Math.max(0, normal.scalar(lightDirection)) * light.getIntensity() / (distance * distance);

            accumulatedColor = accumulatedColor.add(albedo.multScalar(intensity).multRGB(light.getColor()));
        }

        return accumulatedColor;
    }

    @Override
    public Ray scatter(Vec3 position, Vec3 normal, Vec3 viewDir) {
        //Offset to void self-intersection
        Vec3 offsetPosition = position.add(normal.multScalar(EPSILON));

        Vec3 randomDirection = randomInHemisphere(normal);

        return new Ray(offsetPosition, randomDirection);
    }
}
