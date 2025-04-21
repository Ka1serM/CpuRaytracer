package cameras;

import raytracer.ray.Ray;
import utils.algebra.Vec3;

public interface Camera {

    Ray getRay(float u, float v);

    Vec3 getOrigin();

    boolean isMoving();
}
