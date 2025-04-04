package materials;

import lights.Light;
import models.ray.Ray;
import utils.RgbColor;
import utils.algebra.Vec3;

import java.util.List;

import static models.ray.RayUtils.reflect;

public class RefractiveMaterial implements Material {
    private final float ior; //Index of Refraction

    public RefractiveMaterial(float ior) {
        this.ior = ior;
    }

    @Override
    public RgbColor sampleColor(Vec3 position, Vec3 normal, Vec3 viewDir, List<Light> lights) {
        return RgbColor.BLACK;
    }

    @Override
    public Ray scatter(Vec3 position, Vec3 normal, Vec3 viewDir) {
        normal = normal.normalize();
        viewDir = viewDir.normalize();

        float n1 = 1.0f; // Air IOR
        float n2 = ior;  // Material IOR

        float cosThetaI = normal.scalar(viewDir); //cosine of the angle of incidence

        boolean insideMaterial = cosThetaI < 0;
        if (insideMaterial) {
            normal = normal.negate();  //Flip normal if inside the material
            float temp = n1;
            n1 = n2;
            n2 = temp;
        }

        // Ensure cosThetaI is always positive
        cosThetaI = Math.abs(cosThetaI);

        float eta = n1 / n2; //Ratio of refractive indices

        // Calculate sin^2 of the transmitted angle (Snell's Law)
        float sinThetaT2 = eta * eta * (1.0f - cosThetaI * cosThetaI);

        if (sinThetaT2 > 1.0f) {
            //Total Internal Reflection
            Vec3 reflected = reflect(viewDir, normal);
            return new Ray(position, reflected);
        }

        //Snell's Law
        float cosThetaT = (float) Math.sqrt(Math.max(0.0, 1.0 - sinThetaT2));  //cos of the refraction angle
        Vec3 refracted = viewDir.multScalar(eta).sub(normal.multScalar(eta * cosThetaI + cosThetaT));  //refracted direction

        return new Ray(position, refracted.normalize());
    }

}
