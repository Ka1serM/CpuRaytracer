package raytracer;

import models.ray.Ray;
import utils.algebra.Vec3;

public class Camera {
    private Vec3 lowerLeft; // Lower-left corner of the viewplane
    private Vec3 horizontal; // Horizontal direction for the viewplane
    private Vec3 vertical; // Vertical direction for the viewplane
    private Vec3 origin; // Camera position

    private Vec3 viewVector, rightVector, upVector;

    // Constructor without depth of field
    public Camera(Vec3 origin, Vec3 lookAt, Vec3 UP, float aspect, float fov) {
        this.origin = origin;

        // Compute the camera's coordinate system
        float theta = (float) (fov * Math.PI / 180.0f); // Convert fov from degrees to radians
        float halfHeight = (float) Math.tan(theta / 2); // Half height of the viewplane
        float halfWidth = aspect * halfHeight; // Half width of the viewplane

        this.viewVector = lookAt.sub(origin).normalize(); // Camera direction (lookAt - origin)
        this.rightVector = UP.cross(viewVector).normalize(); // Right vector (cross product of up and view)
        this.upVector = viewVector.cross(rightVector).normalize(); // Up vector (cross product of view and right)

        // Set the camera's viewplane (lower left corner, horizontal, vertical)
        this.lowerLeft = origin
                .sub(viewVector.multScalar(1.0f))  // Move along the view vector (adjusted for the view distance)
                .sub(rightVector.multScalar(halfWidth))  // Offset along the right vector (horizontal)
                .sub(upVector.multScalar(halfHeight));  // Offset along the up vector (vertical)

        // Horizontal and vertical vectors that define the width and height of the viewplane
        this.horizontal = rightVector.multScalar(2 * halfWidth);  // Horizontal width
        this.vertical = upVector.multScalar(2 * halfHeight);  // Vertical height
    }

    public Ray getRay(float u, float v) {
        // Calculate where the ray should go based on u, v coordinates
        Vec3 rayDirection = lowerLeft
                .add(horizontal.multScalar(u))  // Add the horizontal vector scaled by u
                .add(vertical.multScalar(v))    // Add the vertical vector scaled by v
                .sub(origin)  // Direction from the camera origin to the image plane
                .normalize(); // Normalize the direction to ensure the ray has a unit direction

        return new Ray(origin, rayDirection); // Return the ray
    }

    public Vec3 getOrigin() {
        return this.origin;
    }
}
