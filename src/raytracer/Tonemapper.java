package raytracer;

import utils.RgbColor;

public class Tonemapper {
    public static RgbColor ACES(RgbColor color) {
        float r = ACES(color.red());
        float g = ACES(color.green());
        float b = ACES(color.blue());

        r = Math.clamp(r, 0, 1);
        g = Math.clamp(g, 0, 1);
        b = Math.clamp(b, 0, 1);

        return new RgbColor(r, g, b);
    }

    public static float ACES(float x) {
        // ACES tonemapping function
        float a = 2.51f;
        float b = 0.03f;
        float c = 2.43f;
        float d = 0.59f;
        float e = 0.14f;
        return (x * (a * x + b)) / (x * (c * x + d) + e);
    }
}
