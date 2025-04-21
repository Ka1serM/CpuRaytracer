package materials;

import lights.Light;
import raytracer.ray.Intersection;
import raytracer.ray.Ray;
import utils.RgbColor;

import java.util.List;

public interface Material {
    RgbColor getDirectLighting(Intersection intersection, Ray ray, List<Light> lights);
    Ray reflect(Intersection intersection, Ray ray);

    default Ray refract(Intersection intersection, Ray ray) {
        // Default implementation returns null, as not all materials will support refraction
        return null;
    }

    default RgbColor getAlbedo() {
        return RgbColor.BLACK;
    }

    default float getReflectivity() {
        return 1f;
    }

    default float getTransparency() {
        return 1f;
    }


}