package materials;

import lights.Light;
import raytracer.ray.Intersection;
import raytracer.ray.Ray;
import utils.RgbColor;

import java.util.List;

public class UnlitMaterial implements Material {
    private final RgbColor albedo;

    public UnlitMaterial(RgbColor color) {
        this.albedo = color;
    }

    @Override
    public RgbColor getAlbedo() {
        return albedo;
    }

    @Override
    public RgbColor getDirectLighting(Intersection intersection, Ray ray, List<Light> lights) {
        return albedo;
    }

    @Override
    public Ray reflect(Intersection intersection, Ray ray) {
        return null;
    }
}