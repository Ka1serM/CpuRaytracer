package scene;

import models.ray.Intersection;
import models.ray.Ray;

public interface SceneObject {

    Intersection hit(Ray r);
   
}