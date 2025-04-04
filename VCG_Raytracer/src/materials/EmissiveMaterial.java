package materials;

import lights.Light;
import models.ray.Ray;
import utils.RgbColor;
import utils.algebra.Vec3;

import java.util.List;

import static models.ray.RayUtils.EPSILON;
import static models.ray.RayUtils.randomInHemisphere;

public class EmissiveMaterial implements Material {

    private final RgbColor albedo;

    public EmissiveMaterial(RgbColor albedo) {
        this.albedo = albedo;
    }

    @Override
    public RgbColor sampleColor(Vec3 position, Vec3 normal, Vec3 viewDir, List<Light> lights) {
        return this.albedo;
    }

    @Override
    public Ray scatter(Vec3 position, Vec3 normal, Vec3 viewDir) {
        //Offset to void self-intersection
        Vec3 offsetPosition = position.add(normal.invert().multScalar(EPSILON));

        Vec3 randomDirection = randomInHemisphere(normal);

        return new Ray(offsetPosition, randomDirection);
    }
}
