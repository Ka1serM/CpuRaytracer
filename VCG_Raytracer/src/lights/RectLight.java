package lights;

import utils.RgbColor;
import utils.algebra.Vec2;
import utils.algebra.Vec3;

public class RectLight extends Light {
    private final Vec2 size;
    private final Vec3 direction;

    public RectLight(Vec3 position, RgbColor color, float intensity, Vec2 size, Vec3 direction) {
        super(position, color, intensity, 1f);
        this.size = size;
        this.direction = direction.normalize();
    }

    public Vec2 getSize() {
        return this.size;
    }

    public Vec3 getDirection() {
        return this.direction;
    }
}
