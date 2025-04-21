package materials;

import lights.Light;
import raytracer.ray.Intersection;
import raytracer.ray.Ray;
import raytracer.ray.RayUtils;
import utils.RgbColor;
import utils.algebra.Vec3;

import java.util.List;

import static raytracer.ray.RayUtils.RAY_EPSYLON;

public class RefractiveMaterial implements Material {
    private final float ior; // Index of Refraction

    // Constructor
    public RefractiveMaterial(float ior) {
        this.ior = ior;
    }

    @Override
    public RgbColor getDirectLighting(Intersection intersection, Ray ray, List<Light> lights) {
        return RgbColor.BLACK;
    }

    @Override
    public Ray reflect(Intersection intersection, Ray ray) {
        return null; // TODO
    }

    public Ray refract(Intersection intersection, Ray ray) {
        float etaRatio;
        float cosThetaI;

        Vec3 normal = intersection.normal().normalize();
        Vec3 incident = ray.direction().normalize();
        float dotNI = normal.scalar(incident);

        // Determine refraction parameters based on the angle of incidence
        if (dotNI < 0.0f) {
            etaRatio = 1.0f / ior;
            cosThetaI = -dotNI;
        } else {
            etaRatio = ior;
            normal = normal.invert();  // Flip the normal if the ray is entering the material
            cosThetaI = dotNI;
        }

        // Calculate the sine of the refraction angle
        float cos2ThetaI = cosThetaI * cosThetaI;
        float sin2ThetaI = 1.0f - cos2ThetaI;
        float sin2ThetaT = etaRatio * etaRatio * sin2ThetaI;

        // Handle Total Internal Reflection (TIR) case
        if (sin2ThetaT > 1.0f) {
            return reflectInternal(intersection, ray);
        }

        // Calculate the refracted direction
        float cosThetaT = (float) Math.sqrt(Math.max(0.0f, 1.0f - sin2ThetaT));
        Vec3 refractDir = incident.multScalar(etaRatio).add(normal.multScalar(etaRatio * cosThetaI - cosThetaT)).normalize();

        // Return the refracted ray
        return new Ray(intersection.position(), refractDir);
    }

    public Ray reflectInternal(Intersection intersection, Ray ray) {
        Vec3 position = intersection.position();
        Vec3 normal = intersection.normal();
        Vec3 viewDir = ray.direction();

        // Calculate the ideal reflection direction (perfect mirror reflection)
        Vec3 reflected = RayUtils.reflect(viewDir, normal);
        // Offset the position to avoid self-intersection
        Vec3 offsetPosition = position.add(normal.multScalar(RAY_EPSYLON));

        // Return the reflected ray with the calculated direction
        return new Ray(offsetPosition, reflected);
    }
}
