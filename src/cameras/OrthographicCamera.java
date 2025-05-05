package cameras;

import raytracer.ray.Ray;
import utils.algebra.Vec3;

public class OrthographicCamera implements Camera {
    private final Vec3 lowerLeft;
    private final Vec3 horizontal;
    private final Vec3 vertical;
    private final Vec3 origin;

    private Vec3 viewVector, rightVector, upVector;

    public OrthographicCamera(Vec3 origin, Vec3 lookAt, Vec3 UP, float aspect, float scale) {
        this.origin = origin;

        this.viewVector = origin.sub(lookAt).normalize();
        this.rightVector = UP.cross(viewVector).normalize();
        this.upVector = viewVector.cross(rightVector).normalize();

        float halfWidth = 1/aspect * scale;

        this.lowerLeft = origin
                .sub(rightVector.multScalar(halfWidth))
                .sub(upVector.multScalar(scale));

        this.horizontal = rightVector.multScalar(2 * halfWidth);
        this.vertical = upVector.multScalar(2 * scale);
    }

    @Override
    public Ray getRay(float u, float v) {
        Vec3 rayOrigin = lowerLeft
                .add(horizontal.multScalar(u))
                .add(vertical.multScalar(v));
        return new Ray(rayOrigin, viewVector.negate());
    }


    @Override
    public Vec3 getOrigin() {
        return origin;
    }
}
