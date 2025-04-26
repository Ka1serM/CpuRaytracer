package scene.models.primitives;

import materials.Material;
import raytracer.ray.Intersection;
import raytracer.ray.Ray;
import raytracer.ray.RayUtils;
import scene.models.Transform;
import utils.algebra.Matrix4x4;
import utils.algebra.Vec3;

public class Sphere extends Shape {
    private final Matrix4x4 transform;
    private final float radius;
    private final Material material;

    public Sphere(Transform transform, float radius, Material material) {
        this.transform = transform.getMatrix();
        this.radius = radius;
        this.material = material;
    }

    @Override
    public Intersection hit(Ray ray) {
        //transform the ray into the sphere local space
        Matrix4x4 transformInverted = transform.invert();

        Vec3 transformedOrigin = transformInverted.multVec3(ray.origin(), true);
        Vec3 transformedDirection = transformInverted.multVec3(ray.direction(), false);

        float a = transformedDirection.scalar(transformedDirection);
        float b = 2.0f * transformedOrigin.scalar(transformedDirection);
        float c = transformedOrigin.scalar(transformedOrigin) - radius * radius;

        float discriminant = b * b - 4.0f * a * c;

        //no real solution, no intersection
        if (discriminant < 0)
            return null;

        //one real solution, ray tangents the sphere
        else if (Math.abs(discriminant) < RayUtils.RAY_EPSYLON) {
            float t = -b / (2.0f * a);
            if (t < 0)
                return null; //Intersection is behind the ray origin

            else {

                Vec3 hitPoint = transformedOrigin.add(transformedDirection.multScalar(t));
                Vec3 normal = hitPoint.normalize();

                // Transform hit point and normal back to world space
                hitPoint = transform.multVec3(hitPoint, true);
                normal = transform.transpose().multVec3(normal, false).normalize();

                return new Intersection(t, hitPoint, normal, material);
            }

        //two real solutions, ray intersects the sphere
        } else {
            float sqrtDiscriminant = (float) Math.sqrt(discriminant);
            float t1 = (-b - sqrtDiscriminant) / (2.0f * a);
            float t2 = (-b + sqrtDiscriminant) / (2.0f * a);

            //choose the smallest positive t
            float t = (t1 > 0 && t1 < t2) ? t1 : (t2 > 0 ? t2 : Float.POSITIVE_INFINITY);

            if (t != Float.POSITIVE_INFINITY) {
                Vec3 hitPoint = transformedOrigin.add(transformedDirection.multScalar(t));
                Vec3 normal = hitPoint.normalize();

                // Transform hit point and normal back to world space
                hitPoint = transform.multVec3(hitPoint, true);
                normal = transformInverted.transpose().multVec3(normal, false).normalize();

                return new Intersection(t, hitPoint, normal, material);
            }
        }

        return null;
    }
}