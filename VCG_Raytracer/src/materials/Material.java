package materials;

import lights.Light;
import models.ray.Ray;
import utils.RgbColor;
import utils.algebra.Vec3;

import java.util.List;

public interface Material {
    RgbColor sampleColor(Vec3 position, Vec3 normal, Vec3 viewDir, List<Light> lights);
    Ray scatter(Vec3 position, Vec3 normal, Vec3 viewDir);
}