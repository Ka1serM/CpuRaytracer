package models.primitives;

import materials.Material;
import models.ray.Intersection;
import models.ray.Ray;
import scene.SceneObject;
import utils.algebra.Vec2;
import utils.algebra.Vec3;

public class Plane implements SceneObject {
    private final Material material;
    private final Vec3 origin;
    private final Vec3 normal;
    private final Vec2 scale;

    public Plane(Vec3 origin, Vec3 normal, Vec2 scale, Material material) {
        this.origin = origin;
        this.normal = normal.normalize();
        this.scale = scale;
        this.material = material;
    }

    //Ray-plane intersection
    public float intersect(Ray ray) {
        float denom = normal.scalar(ray.getDirection());
        if (Math.abs(denom) > 1e-6) {
            float t = (origin.sub(ray.getOrigin())).scalar(normal) / denom;
            return (t >= 0) ? t : Float.POSITIVE_INFINITY; //Return valid t or infinity
        }
        return Float.POSITIVE_INFINITY; // No intersection
    }

    @Override
    public Intersection hit(Ray ray) {
        float t = intersect(ray);

        if (t == Float.POSITIVE_INFINITY)
            return null; //No hit

        Vec3 hitPoint = ray.getOrigin().add(ray.getDirection().multScalar(t));

        //tangent vectors to the plane
        Vec3 tangent1 = (Math.abs(normal.y) > 0.9f) ? new Vec3(1, 0, 0) : normal.cross(new Vec3(0, 1, 0)).normalize();
        Vec3 tangent2 = normal.cross(tangent1).normalize();

        //local coordinates relative to plane origin
        float localX = hitPoint.sub(origin).scalar(tangent1);
        float localY = hitPoint.sub(origin).scalar(tangent2);

        //if within plane
        if (Math.abs(localX) <= scale.x / 2 && Math.abs(localY) <= scale.y / 2)
            return new Intersection(t, hitPoint, normal, material);

        return null; // Outside the plane
    }
}
