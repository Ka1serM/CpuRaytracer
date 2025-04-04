package materials;

import lights.Light;
import models.ray.Ray;
import utils.RgbColor;
import utils.algebra.Vec3;

import java.util.List;

import static models.ray.RayUtils.randomInUnitSphere;
import static models.ray.RayUtils.reflect;

public class PhongMaterial extends LambertMaterial {
    private final RgbColor ambientColor;
    private final RgbColor specularColor;
    private final float shininess;

    public PhongMaterial(RgbColor ambientColor, RgbColor diffuseColor, RgbColor specularColor, float shininess) {
        super(diffuseColor);
        this.ambientColor = ambientColor;
        this.specularColor = specularColor;
        this.shininess = shininess;
    }

    @Override
    public RgbColor getColor(Vec3 position, Vec3 normal, Vec3 viewDir, List<Light> lights) {
        RgbColor accumulatedColor = super.getColor(position, normal, viewDir, lights); // Get the diffuse part from Lambert

        // Phong specular and ambient contributions
        Vec3 normalVector = normal.normalize();
        for (Light light : lights) {
            Vec3 lightDir = light.getPosition().sub(position).normalize();
            RgbColor lightColor = light.getColor(); // Get light color

            // Specular component (Phong reflection)
            Vec3 reflectDir = normalVector.multScalar(2 * normalVector.scalar(lightDir)).sub(lightDir);
            float specularFactor = (float) Math.pow(Math.max(0, viewDir.scalar(reflectDir)), shininess);
            RgbColor specular = specularColor.multScalar(specularFactor).multRGB(lightColor);

            // Add the specular contribution from the light to the accumulated color
            accumulatedColor = accumulatedColor.add(specular).multScalar(light.getIntensity());
        }

        // Add the ambient color
        return ambientColor.add(accumulatedColor);
    }

    @Override
    public Ray scatter(Vec3 position, Vec3 normal, Vec3 viewDir) {
        // Reflect the incoming view direction around the normal for the specular part
        Vec3 reflected = reflect(viewDir, normal);

        // Adjust the fuzziness based on shininess
        // Smaller shininess means more fuzziness, larger shininess means less fuzziness
        float fuzziness = 1.0f - (shininess / 128.0f);  // You can adjust the 128.0f factor to control the relationship
        fuzziness = Math.max(0.0f, Math.min(1.0f, fuzziness));  // Clamp fuzziness between 0 and 1

        // Add fuzziness based on the shininess factor
        Vec3 randomDirection = reflected.add(randomInUnitSphere().multScalar(fuzziness * 0.5f)); // Scale fuzziness

        return new Ray(position, randomDirection);
    }
}
