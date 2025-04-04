package materials;

import lights.Light;
import utils.RgbColor;
import utils.algebra.Vec3;

import java.util.List;

public class UnlitMaterial extends LambertMaterial {

    public UnlitMaterial(RgbColor color) {
        super(color);
    }

    @Override
    public RgbColor sampleColor(Vec3 position, Vec3 normal, Vec3 viewDir, List<Light> lights) {
        return this.albedo;
    }
}