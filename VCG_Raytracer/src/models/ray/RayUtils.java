package models.ray;

import utils.algebra.Vec3;

import java.util.Random;

public class RayUtils {
    public static final float EPSILON = 1e-6f;
    private static final Random RANDOM = new Random();

    // Reflection formula
    public static Vec3 reflect(Vec3 v, Vec3 n) {
        return v.sub(n.multScalar(2 * v.scalar(n)));
    }

    //random unit vector inside a unit sphere
    public static Vec3 randomInUnitSphere() {
        Vec3 p;
        do {
            //random point in a cube with coordinates between -1 and 1
            p = new Vec3(RANDOM.nextFloat() * 2 - 1, RANDOM.nextFloat() * 2 - 1, RANDOM.nextFloat() * 2 - 1);
        } while (p.length() > 1.0f);  //point is within the unit sphere

        return p.normalize();
    }

    // Generates a random vector on the hemisphere defined by the normal
    public static Vec3 randomInHemisphere(Vec3 normal) {
        Vec3 onUnitSphere = randomInUnitSphere();  //random point in the unit sphere

        if (onUnitSphere.scalar(normal) < 0.0f)  //If it's in the opposite hemisphere, negate it
            onUnitSphere = onUnitSphere.negate();

        return onUnitSphere;
    }
}
