package models.primitives;

import materials.Material;
import models.ray.Intersection;
import models.ray.Ray;
import scene.SceneObject;
import utils.algebra.Vec3;

public class Box implements SceneObject {
    private final Material material;
    private final Vec3 min;   //lower edge
    private final Vec3 max;   //upper edge

    public Box(Vec3 min, Vec3 max, Material material) {
        this.min = min;
        this.max = max;
        this.material = material;
    }

    //AABB intersection test
    public float intersect(Ray ray) {
        float tMin = Float.NEGATIVE_INFINITY;
        float tMax = Float.POSITIVE_INFINITY;

        Vec3 rayOrigin = ray.getOrigin();
        Vec3 rayDirection = ray.getDirection();

        //check each axis (X, Y, Z)
        float invDirX = 1.0f / rayDirection.x;
        float t0X = (min.x - rayOrigin.x) * invDirX;
        float t1X = (max.x - rayOrigin.x) * invDirX;

        if (invDirX < 0.0f) {
            float temp = t0X;
            t0X = t1X;
            t1X = temp;
        }

        tMin = Math.max(tMin, t0X);
        tMax = Math.min(tMax, t1X);

        if (tMin > tMax) return
                Float.POSITIVE_INFINITY; //No intersection

        // For Y axis
        float invDirY = 1.0f / rayDirection.y;
        float t0Y = (min.y - rayOrigin.y) * invDirY;
        float t1Y = (max.y - rayOrigin.y) * invDirY;

        if (invDirY < 0.0f) {
            float temp = t0Y;
            t0Y = t1Y;
            t1Y = temp;
        }

        tMin = Math.max(tMin, t0Y);
        tMax = Math.min(tMax, t1Y);

        if (tMin > tMax)
            return Float.POSITIVE_INFINITY; //No intersection

        // For Z axis
        float invDirZ = 1.0f / rayDirection.z;
        float t0Z = (min.z - rayOrigin.z) * invDirZ;
        float t1Z = (max.z - rayOrigin.z) * invDirZ;

        if (invDirZ < 0.0f) {
            float temp = t0Z;
            t0Z = t1Z;
            t1Z = temp;
        }

        tMin = Math.max(tMin, t0Z);
        tMax = Math.min(tMax, t1Z);

        if (tMin > tMax)
            return Float.POSITIVE_INFINITY; //No intersection

        return tMin > 0 ? tMin : tMax; // Return nearest intersection time
    }

    @Override
    public Intersection hit(Ray ray) {
        float t = intersect(ray);
        if (t == Float.POSITIVE_INFINITY) {
            return null; // No hit
        }

        Vec3 hitPoint = ray.getOrigin().add(ray.getDirection().multScalar(t));
        Vec3 normal = computeNormal(hitPoint);

        return new Intersection(t, hitPoint, normal, material);
    }

    //normal at the hit point
    private Vec3 computeNormal(Vec3 hitPoint) {
        Vec3 normal = new Vec3(0, 0, 0);

        if (Math.abs(hitPoint.x - min.x) < 1e-6) normal = new Vec3(-1, 0, 0); // Hit left face
        else if (Math.abs(hitPoint.x - max.x) < 1e-6) normal = new Vec3(1, 0, 0); // Hit right face
        else if (Math.abs(hitPoint.y - min.y) < 1e-6) normal = new Vec3(0, -1, 0); // Hit bottom face
        else if (Math.abs(hitPoint.y - max.y) < 1e-6) normal = new Vec3(0, 1, 0); // Hit top face
        else if (Math.abs(hitPoint.z - min.z) < 1e-6) normal = new Vec3(0, 0, -1); // Hit front face
        else if (Math.abs(hitPoint.z - max.z) < 1e-6) normal = new Vec3(0, 0, 1); // Hit back face

        return normal;
    }
}
