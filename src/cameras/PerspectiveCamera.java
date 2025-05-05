package cameras;

import raytracer.ray.Ray;
import utils.algebra.Vec3;
public class PerspectiveCamera implements Camera {
    private Vec3 origin;
    private Vec3 viewVector;
    private Vec3 lowerLeft;
    private Vec3 horizontal;
    private Vec3 vertical;
    private Vec3 rightVector;
    private Vec3 upVector;

    private final float halfWidth;
    private final float halfHeight;


    public PerspectiveCamera(Vec3 origin, Vec3 lookAt, Vec3 up, float aspect, float fovDegrees) {
        this.origin = origin;

        float theta = (float) Math.toRadians(fovDegrees);
        this.halfHeight = (float) Math.tan(theta / 2);
        this.halfWidth = aspect * halfHeight;

        this.viewVector = origin.sub(lookAt).normalize();
        this.rightVector = up.cross(viewVector).normalize();
        this.upVector = viewVector.cross(rightVector).normalize();

        this.lowerLeft = origin
                .sub(rightVector.multScalar(halfWidth))
                .sub(upVector.multScalar(halfHeight))
                .sub(viewVector);

        this.horizontal = rightVector.multScalar(2 * halfWidth);
        this.vertical = upVector.multScalar(2 * halfHeight);
    }

    public Ray getRay(float s, float t) {
        Vec3 rayDirection = lowerLeft
                .add(horizontal.multScalar(s))
                .add(vertical.multScalar(t))
                .sub(origin)
                .normalize();

        return new Ray(origin, rayDirection);
    }

    @Override
    public Vec3 getOrigin() {
        return origin;
    }
}
