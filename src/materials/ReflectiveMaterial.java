package materials;

import lights.Light;
import raytracer.ray.Intersection;
import raytracer.ray.Ray;
import raytracer.ray.RayUtils;
import utils.RgbColor;
import utils.algebra.Vec3;

import java.util.List;

import static raytracer.ray.RayUtils.RAY_EPSYLON;
import static raytracer.ray.RayUtils.randomInHemisphere;

public class ReflectiveMaterial implements Material {

    //PHONG PARAMS
    protected final RgbColor diffuse;
    protected final RgbColor specular;

    protected final RgbColor ambient;

    protected final float shininess;


    //EXTRA
    protected final float roughness;
    protected final boolean metallic;


    public ReflectiveMaterial(RgbColor diffuse, RgbColor specular, RgbColor ambient, float shininess, float roughness, boolean metallic) {
        this.diffuse = diffuse;
        this.specular = specular;
        this.ambient = ambient;
        this.shininess = shininess;

        this.roughness = roughness;
        this.metallic = metallic;
    }

    @Override
    public float getReflectivity() {
        return 1 - roughness;
    }

    @Override
    public RgbColor getAlbedo() {
        if(metallic)
            return RgbColor.BLACK;
        return diffuse;
    }

    @Override
    public RgbColor getDirectLighting(Intersection intersection, Ray ray, List<Light> lights) {

        if (metallic)
            return RgbColor.BLACK;

        Vec3 V = ray.direction();
        Vec3 position = intersection.position();
        Vec3 normal = intersection.normal();

        RgbColor accumulatedColor = ambient.multRGB(diffuse);

        for (Light light : lights) {
            Vec3 L = light.getPosition().sub(position);
            float distance = L.length();
            L = L.normalize();
            float intensity = light.getIntensity() / (distance); // Inverse Square Law

            // Diffuse component (Lambertian reflection)
            float NdotL = normal.scalar(L);
            if(NdotL < 0f)
                continue;

            RgbColor diffuseComponent = diffuse.multScalar(NdotL);

            //Phong reflection
            //reflection vector R
            Vec3 R = RayUtils.reflect(L, normal);  //Reflect light direction L about normal N
            float RdotV = R.scalar(V);   //Dot product between reflection vector and view direction
            float kS = Math.max(0, RdotV);  //Ensure the dot product is non-negative
            kS = (float) Math.pow(kS, shininess);  //Apply shininess exponent
            RgbColor specularComponent = specular.multScalar(kS);

            accumulatedColor = accumulatedColor.add(diffuseComponent.add(specularComponent).multRGB(light.getColor()).multScalar(intensity));
        }

        return accumulatedColor;
    }



    @Override
    public Ray reflect(Intersection intersection, Ray ray) {
        Vec3 position = intersection.position();
        Vec3 normal = intersection.normal();
        Vec3 viewDir = ray.direction();

        Vec3 reflected = RayUtils.reflect(viewDir, normal);

        Vec3 randomDirection = randomInHemisphere(normal);

        //blend the reflected direction with a random direction in the hemisphere
        Vec3 finalDirection = reflected.multScalar(1.0f - roughness).add(randomDirection.multScalar(roughness));

        Vec3 offsetPosition = position.add(normal.multScalar(RAY_EPSYLON));
        return new Ray(offsetPosition, finalDirection);
    }
}
