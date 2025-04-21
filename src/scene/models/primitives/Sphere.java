package scene.models.primitives;

import materials.Material;
import raytracer.ray.Intersection;
import raytracer.ray.Ray;
import scene.models.Hittable;
import scene.models.SceneObject;
import utils.algebra.Vec3;

public class Sphere extends Shape {
    private final Vec3 center;
    private final float radius;
    private final Material material;

    public Sphere(Vec3 center, float radius, Material material) {
        this.center = center;
        this.radius = radius;
        this.material = material;
    }

    @Override
    public Intersection hit(Ray ray) {
        Vec3 oc = ray.origin().sub(center);
        float a = ray.direction().scalar(ray.direction());
        float b = 2.0f * oc.scalar(ray.direction());
        float c = oc.scalar(oc) - radius * radius;

        float discriminant = b * b - 4.0f * a * c;

        if (discriminant >= 0) {
            float sqrtDiscriminant = (float) Math.sqrt(discriminant);
            float t1 = (-b - sqrtDiscriminant) / (2.0f * a);
            float t2 = (-b + sqrtDiscriminant) / (2.0f * a);

            float t = t1 > 0 ? t1 : (t2 > 0 ? t2 : Float.POSITIVE_INFINITY);

            if (t != Float.POSITIVE_INFINITY) {
                Vec3 hitPoint = ray.origin().add(ray.direction().multScalar(t));
                Vec3 normal = hitPoint.sub(center).normalize();

                return new Intersection(t, hitPoint, normal, material);
            }
        }

        return null;
    }

}
