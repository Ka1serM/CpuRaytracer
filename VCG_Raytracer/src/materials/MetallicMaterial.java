package materials;

import lights.Light;
import models.ray.Ray;
import utils.RgbColor;
import utils.algebra.Vec3;

import java.util.List;

import static models.ray.RayUtils.*;

public class MetallicMaterial implements Material {
    private final RgbColor albedo;
    private final float roughness;

    public MetallicMaterial(RgbColor color, float roughness) {
        this.albedo = color;
        this.roughness = roughness;
    }

    @Override
    public RgbColor sampleColor(Vec3 position, Vec3 normal, Vec3 viewDir, List<Light> lights) {
        RgbColor accumulatedColor = RgbColor.BLACK;

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
        normal = normal.normalize();
        viewDir = viewDir.normalize();
        Vec3 reflected = reflect(viewDir, normal);

        // Randomly offset direction based on roughness amount
        Vec3 randomDirection = reflected.add(randomInHemisphere(normal).multScalar(roughness));

        // Offset to avoid self-intersection
        Vec3 offsetPosition = position.add(normal.multScalar(EPSILON));

        return new Ray(offsetPosition, randomDirection);
    }
}
