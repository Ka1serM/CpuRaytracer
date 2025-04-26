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
        Vec3 N = intersection.normal().normalize(); //Normale
        Vec3 I = ray.direction().normalize();       //Einfallsrichtung

        float cosTheta = N.scalar(I);

        float etaI = 1.0f; //IOR von Luft
        float etaT = ior; //IOR des Materials

        //Strahl kommt von innen, da Einfallsrichtung entgegengesetzt der Normale
        if (cosTheta > 0) {
            Vec3 tmp = N.negate(); //Normale umdrehen
            N = tmp;
            float swap = etaI;
            etaI = etaT;
            etaT = swap;
            cosTheta = Math.abs(cosTheta); //positiver Wert
        }

        float eta = etaI / etaT;
        float k = 1.0f - eta * eta * (1.0f - cosTheta * cosTheta);

        if (k < 0.0f) {
            //Totalreflexion: reflektieren
            Vec3 reflected = I.sub(N.multScalar(2.0f * I.scalar(N)));
            return new Ray(intersection.position(), reflected);
        } else {
            //Normale Brechung
            Vec3 refracted = I.multScalar(eta)
                    .add(N.multScalar(eta * cosTheta - (float)Math.sqrt(k)));
            return new Ray(intersection.position(), refracted.normalize());
        }
    }
}
