package raytracer.ray;

import scene.models.Hittable;
import scene.models.SceneObject;
import scene.models.primitives.Shape;
import utils.algebra.Vec3;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class RayUtils {
    public static final float RAY_EPSYLON = 0.00001f;
    public static final float SHADOW_EPSYLON = 0.0001f;

    public static final ThreadLocalRandom RANDOM = ThreadLocalRandom.current();

    // Reflection formula
    public static Vec3 reflect(Vec3 v, Vec3 n) {
        return v.sub(n.multScalar(2 * v.scalar(n)));
    }

    // Optimized cosine-weighted random vector in the hemisphere defined by the normal
    public static Vec3 randomInHemisphere(Vec3 normal) {
        // Generate random azimuthal angle between 0 and 2 * PI
        float phi = RANDOM.nextFloat() * (float) (2 * Math.PI);

        // Generate random cosine-weighted polar angle (θ) where cos(θ) is the random value
        float cosTheta = (float) Math.sqrt(RANDOM.nextFloat());  // Cosine-weighted random
        float sinTheta = (float) Math.sqrt(1.0f - cosTheta * cosTheta);  // Sine of theta

        // Calculate direction components
        float x = sinTheta * (float) Math.cos(phi);
        float y = sinTheta * (float) Math.sin(phi);

        Vec3 randomDir = new Vec3(x, y, cosTheta);

        if (randomDir.scalar(normal) < 0.0f)  // If it's in the opposite hemisphere, flip it
            randomDir = randomDir.negate();

        return randomDir;
    }

    public static Intersection findClosestIntersection(List<SceneObject> hittableList, Ray ray) {
        Intersection closestIntersection = null;
        float closestDistance = Float.MAX_VALUE;

        for (Hittable obj : hittableList) {
            Intersection hit = obj.hit(ray);
            if (hit != null && hit.distance() < closestDistance) {
                closestDistance = hit.distance();
                closestIntersection = hit;
            }
        }

        return closestIntersection;
    }

    public static Vec3 randomInUnitDisk() {
        Vec3 p;
        do {
            float x = 2.0f * RANDOM.nextFloat() - 1.0f;
            float y = 2.0f * RANDOM.nextFloat() - 1.0f;
            p = new Vec3(x, y, 0);
        } while (p.scalar(p) >= 1.0f);
        return p;
    }

}
