package scene.models;

import raytracer.ray.Intersection;
import raytracer.ray.Ray;
import scene.Scene;

import java.util.List;
import static raytracer.ray.RayUtils.findClosestIntersection;

public class TriangleModel extends SceneObject {

    List<SceneObject> triangles;

    public TriangleModel(List<SceneObject> triangles) {
        this.triangles = triangles;
    }

    @Override
    public Intersection hit(Ray r) {
        return findClosestIntersection(triangles, r);
    }
}
