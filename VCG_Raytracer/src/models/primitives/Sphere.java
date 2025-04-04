package models.primitives;

import materials.LambertMaterial;
import materials.Material;
import models.ray.Intersection;
import models.ray.Ray;
import scene.SceneObject;
import utils.RgbColor;
import utils.algebra.Vec3;

public class Sphere implements SceneObject {
    private Vec3 center;
    private float radius;
    private Material material;

    public Sphere(Vec3 center, float radius, Material material) {
        this.center = center;
        this.radius = radius;
        this.material = material;
    }

    public Sphere(Vec3 center) {
        this.center = center;
        this.radius = 1.0f;
        this.material = new LambertMaterial(RgbColor.WHITE);
    }

    @Override
    public Intersection hit(Ray ray) {
        Vec3 oc = ray.getOrigin().sub(center);
        float a = ray.getDirection().scalar(ray.getDirection());
        float b = 2.0f * oc.scalar(ray.getDirection());
        float c = oc.scalar(oc) - radius * radius;

        float discriminant = b * b - 4.0f * a * c;

        if (discriminant >= 0) {
            float sqrtDiscriminant = (float) Math.sqrt(discriminant);
            float t1 = (-b - sqrtDiscriminant) / (2.0f * a);
            float t2 = (-b + sqrtDiscriminant) / (2.0f * a);

            float t = t1 > 0 ? t1 : t2;

            if (t > 0) {
                Vec3 hitPoint = ray.getOrigin().add(ray.getDirection().multScalar(t));
                Vec3 normal = hitPoint.sub(center).normalize();
                return new Intersection(t, hitPoint, normal, material);
            }
        }
        return null; // No intersection
    }
}
