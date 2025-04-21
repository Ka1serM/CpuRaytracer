package scene.models;

import raytracer.ray.Intersection;
import raytracer.ray.Ray;
import scene.Scene;

public interface Hittable {

    Intersection hit(Ray r);
}