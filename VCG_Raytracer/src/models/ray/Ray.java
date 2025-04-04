package models.ray;

import utils.algebra.Vec3;

public class Ray {
    Vec3 origin;
    Vec3 direction;

    public Ray(Vec3 origin, Vec3 direction){
        this.origin = origin;
        this.direction = direction;
    }

    public Vec3 getPoint(float t){
        return origin.add(direction.multScalar(t));
    }

    public Vec3 getOrigin(){
        return origin;
    }

    public Vec3 getDirection(){
        return direction;
    }
}
