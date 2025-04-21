package scene.models.primitives;

import materials.Material;
import raytracer.ray.Intersection;
import raytracer.ray.Ray;
import scene.models.Hittable;
import utils.algebra.Vec2;
import utils.algebra.Vec3;

public class Plane extends Shape {
    private final Material material;
    private final Vec3 origin;
    private final Vec3 normal;
    private final Vec2 scale;

    public Plane(Vec3 origin, Vec3 normal, Vec2 scale, Material material) {
        this.origin = origin;
        this.normal = normal.normalize();  // Make sure normal is normalized
        this.scale = scale;
        this.material = material;
    }

    // Optimized Ray-plane intersection
    public float intersect(Ray ray) {
        // Precompute the dot product of normal and ray direction
        float denom = normal.scalar(ray.direction());

        // Check if the ray is parallel to the plane (denom close to zero)
        if (Math.abs(denom) > 1e-6) {
            // Compute the distance t to the plane
            float t = normal.scalar(origin.sub(ray.origin())) / denom;
            return (t >= 0) ? t : Float.POSITIVE_INFINITY; // Valid intersection if t >= 0
        }

        // Ray is parallel to the plane, return infinity (no intersection)
        return Float.POSITIVE_INFINITY;
    }

    @Override
    public Intersection hit(Ray ray) {
        // Get the intersection distance (t) from the ray-plane intersection
        float t = intersect(ray);

        // No intersection if t is infinity
        if (t == Float.POSITIVE_INFINITY)
            return null;

        // Calculate the hit point on the plane
        Vec3 hitPoint = ray.origin().add(ray.direction().multScalar(t));

        // Compute the local coordinates (u, v) in the plane
        // Tangents (localX and localY)
        Vec3 tangent1 = (Math.abs(normal.y) > 0.9f) ? new Vec3(1, 0, 0) : normal.cross(new Vec3(0, 1, 0)).normalize();
        Vec3 tangent2 = normal.cross(tangent1).normalize();

        float localX = hitPoint.sub(origin).scalar(tangent1);
        float localY = hitPoint.sub(origin).scalar(tangent2);

        // Check if the hit point is within the bounds of the plane
        if (Math.abs(localX) <= scale.x / 2 && Math.abs(localY) <= scale.y / 2)
            return new Intersection(t, hitPoint, normal, material);

        // If the hit point is outside the bounds of the plane
        return null;
    }
}
