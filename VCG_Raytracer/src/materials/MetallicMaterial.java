package materials;

import lights.Light;
import models.ray.Ray;
import utils.RgbColor;
import utils.algebra.Vec3;

import java.util.List;

import static models.ray.RayUtils.*;

public class MetallicMaterial implements Material {
    private final float roughness;

    public MetallicMaterial(float roughness) {
        this.roughness = roughness;
    }

    @Override
    public RgbColor getColor(Vec3 position, Vec3 normal, Vec3 viewDir, List<Light> lights) {
        return RgbColor.BLACK;
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
