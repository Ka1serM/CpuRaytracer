package scene.models.primitives;

import materials.Material;
import raytracer.ray.Intersection;
import raytracer.ray.Ray;
import scene.models.Hittable;
import utils.algebra.Vec3;

import static raytracer.ray.RayUtils.RAY_EPSYLON;

    public class Triangle extends Shape implements Hittable {
        private Vec3 vertex0, vertex1, vertex2;
        private Vec3 normal0, normal1, normal2;
        private Material material;

    public Triangle(Vec3 v0, Vec3 vn0, Vec3 v1, Vec3 vn1, Vec3 v2, Vec3 vn2, Material material) {
        this.vertex0 = v0;
        this.normal0 = vn0;
        this.vertex1 = v1;
        this.normal1 = vn1;
        this.vertex2 = v2;
        this.normal2 = vn2;
        this.material = material;
    }

        @Override
        public Intersection hit(Ray r) {
            Vec3 edge1;
            Vec3 edge2;
            Vec3 h;
            Vec3 s;
            Vec3 q;
            float a, f, u, v;

            // Calculate edges of the triangle
            edge1 = vertex1.sub(vertex0);  // Using sub method to get the edge vector (creates a new Vec3)
            edge2 = vertex2.sub(vertex0);  // Using sub method to get the edge vector (creates a new Vec3)

            // Compute the determinant (a)
            h = r.direction().cross(edge2);  // Using cross to compute h
            a = edge1.scalar(h);

            if (a > -RAY_EPSYLON && a < RAY_EPSYLON) {
                return null;    // This ray is parallel to this triangle.
            }

            f = 1.0f / a;
            s = r.origin().sub(vertex0);  // Calculate s = ray origin - vertex0
            u = f * s.scalar(h);

            if (u < 0.0 || u > 1.0) {
                return null;
            }

            q = s.cross(edge1);  // Compute q = s x edge1
            v = f * r.direction().scalar(q);

            if (v < 0.0 || u + v > 1.0) {
                return null;
            }

            // Calculate the distance to the intersection point
            float t = f * edge2.scalar(q);
            if (t > RAY_EPSYLON) {  // ray intersection
                // Compute the intersection point
                Vec3 intersectionPoint = r.origin().add(r.direction().multScalar(t)); // intersection point = origin + direction * t

                // Interpolate normals based on barycentric coordinates
                Vec3 interpolatedNormal = normal0.multScalar(1 - u - v)
                        .add(normal1.multScalar(u))
                        .add(normal2.multScalar(v));

                // Normalize interpolated normal
                interpolatedNormal = interpolatedNormal.normalize();

                // Create and return the Intersection object
                return new Intersection(t, intersectionPoint, interpolatedNormal, this.material);
            } else {  // No ray intersection
                return null;
            }
        }
}

