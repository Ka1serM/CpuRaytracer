package scene.models.primitives;

import materials.Material;
import raytracer.ray.Intersection;
import raytracer.ray.Ray;
import raytracer.ray.RayUtils;
import scene.models.Transform;
import utils.algebra.Matrix4x4;
import utils.algebra.Vec2;
import utils.algebra.Vec3;

public class Plane extends Shape {
    private final Material material;
    private final Matrix4x4 transform;
    private final Vec3 normal;
    private final Vec2 scale;

    public Plane(Transform transform, Vec3 normal, Vec2 scale, Material material) {
        this.transform = transform.getMatrix();
        this.normal = normal.normalize();  //make sure normal is normalized
        this.scale = scale;
        this.material = material;
    }

    @Override
    public Intersection hit(Ray ray) {
        // Transform the ray into the plane's local space
        Matrix4x4 invTransform = transform.invert();
        Vec3 transformedOrigin = invTransform.multVec3(ray.origin(), true);
        Vec3 transformedDirection = invTransform.multVec3(ray.direction(), false);

        Vec3 localNormal = invTransform.multVec3(normal, false).normalize();
        Vec3 planeOrigin = new Vec3(0, 0, 0);

        float denom = localNormal.scalar(transformedDirection);
        if (Math.abs(denom) <= RayUtils.RAY_EPSYLON)
            return null;

        float t = localNormal.scalar(planeOrigin.sub(transformedOrigin)) / denom;
        if (t < 0)
            return null;

        Vec3 localHitPoint = transformedOrigin.add(transformedDirection.multScalar(t));

        //Tangents for local coordinates
        Vec3 tangent1 = (Math.abs(localNormal.y) > 0.9f) ? new Vec3(1, 0, 0) : localNormal.cross(new Vec3(0, 1, 0)).normalize();
        Vec3 tangent2 = localNormal.cross(tangent1).normalize();

        //Local (u, v) coordinates
        Vec3 offset = localHitPoint.sub(planeOrigin);
        float localX = offset.scalar(tangent1);
        float localY = offset.scalar(tangent2);

        //check bounds
        if (Math.abs(localX) > scale.x || Math.abs(localY) > scale.y)
            return null;

        //Transform hit point and normal back to world space
        Vec3 hitPoint = transform.multVec3(localHitPoint, true);
        Vec3 worldNormal = invTransform.transpose().multVec3(localNormal, false).normalize();

        return new Intersection(t, hitPoint, worldNormal, material);
    }
}
