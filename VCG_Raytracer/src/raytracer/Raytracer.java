/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    1. Send primary ray
    2. intersection test with all shapes
    3. if hit:
    3a: send secondary ray to the light source
    3b: 2
        3b.i: if hit:
            - Shape is in the shade
            - Pixel color = ambient value
        3b.ii: in NO hit:
            - calculate local illumination
    4. if NO hit:
        - set background color

~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/

package raytracer;

import lights.Light;
import materials.LambertMaterial;
import materials.Material;
import models.primitives.Sphere;
import models.ray.Intersection;
import models.ray.Ray;
import scene.Scene;
import scene.SceneObject;
import ui.Window;
import utils.*;
import utils.algebra.Vec3;
import utils.io.Log;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static models.ray.RayUtils.EPSILON;
import static models.ray.RayUtils.randomInHemisphere;

public class Raytracer {

    private BufferedImage mBufferedImage;

    private Scene mScene;
    private Window mRenderWindow;

    private int mMaxRecursions;


    private RgbColor mBackgroundColor;
    private RgbColor mAmbientLight;

    private int mAntiAliasingSamples;
    private float mAntiAliasingFilterWidth;

    private boolean mDebug;
    private long tStart;

    private int mGiSamples;

    private boolean mUseGi;
    private int mGiLevel;

    private boolean mUseAo;
    private int mAoSamples;

    private float mAoDistance;

    private boolean mSoftShadows;

    private int mLightSamples;

    private int mLightDensity;

    /**  Constructor **/

    public Raytracer(Scene scene, Window renderWindow, int recursions, RgbColor backColor, RgbColor ambientLight, int antiAliasingSamples, float antiAliasingFilterSize, boolean useGi, int giSamples, int giLlevel, boolean useAo, int aoSamples, float aoDistance, boolean softShadows, int lightSamples, int lightDensity, boolean debugOn){
        Log.print(this, "Init");
        mMaxRecursions = recursions;

        mBufferedImage = renderWindow.getBufferedImage();

        mAntiAliasingSamples = antiAliasingSamples;
        mAntiAliasingFilterWidth = antiAliasingFilterSize;
        mGiSamples = giSamples;
        mUseGi = useGi;
        mAoDistance = aoDistance;
        mAoSamples = aoSamples;
        mGiLevel = giLlevel;
        mLightDensity = lightDensity;
        mLightSamples = lightSamples;
        mSoftShadows = softShadows;
        mUseAo = useAo;

        mBackgroundColor = backColor;
        mAmbientLight = ambientLight;
        mScene = scene;
        mRenderWindow = renderWindow;
        mDebug = debugOn;
        tStart = System.currentTimeMillis();
    }

    /**  Send the created window to the frame delivered by JAVA to display our result **/
    public void exportRendering(){
        mRenderWindow.exportRendering(String.valueOf(stopTime(tStart)), mMaxRecursions, mAntiAliasingSamples, mDebug);
    }

    /**  Stop time of rendering **/
    private static double stopTime(long tStart){
        long tEnd = System.currentTimeMillis();
        long tDelta = tEnd - tStart;
        return tDelta / 1000.0;
    }

    public void renderScene() {
        Log.print(this, "Prepare rendering at " + stopTime(tStart));

        Camera camera = mScene.getCameras().getFirst();
        int width = mBufferedImage.getWidth();
        int height = mBufferedImage.getHeight();
        RgbColor[] hdrColors = new RgbColor[width * height];

        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        AtomicInteger totalSamples = new AtomicInteger(0);

        while (totalSamples.get() < mMaxRecursions) {
            List<Callable<Void>> tasks = new ArrayList<>();

            // Divide the image into larger blocks (32x32 pixels per task)
            int blockSize = 32;
            for (int rowStart = 0; rowStart < height; rowStart += blockSize) {
                for (int colStart = 0; colStart < width; colStart += blockSize) {
                    final int startX = colStart;
                    final int startY = rowStart;
                    final int endX = Math.min(startX + blockSize, width);
                    final int endY = Math.min(startY + blockSize, height);

                    tasks.add(() -> {
                        for (int y = startY; y < endY; y++) {
                            for (int x = startX; x < endX; x++) {

                                int aaSampleIndex = 0;
                                while (aaSampleIndex < mAntiAliasingSamples) {
                                    // AA jitter offset
                                    float offsetX = (float) (Math.random() * mAntiAliasingFilterWidth);
                                    float offsetY = (float) (Math.random() * mAntiAliasingFilterWidth);

                                    Ray primaryRay = camera.getRay((x + offsetX) / width, (y + offsetY) / height);
                                    RgbColor sampleColor = scatter(primaryRay);

                                    int pixelIndex = y * width + x;

                                    float blendFactor = 1f / (totalSamples.get() + aaSampleIndex + 1);

                                    // Progressive Blending
                                    if (hdrColors[pixelIndex] == null)
                                        hdrColors[pixelIndex] = sampleColor;
                                    else
                                        hdrColors[pixelIndex] = hdrColors[pixelIndex].multScalar(1 - blendFactor).add(sampleColor.multScalar(blendFactor));

                                    // Update the pixel color
                                    mBufferedImage.setRGB(x, y, Tonemapper.ACES(hdrColors[pixelIndex]).getRGB());
                                    aaSampleIndex++;
                                }
                            }
                        }
                        return null;
                    });
                }
            }

            long startTime = System.currentTimeMillis();

            try {
                executor.invokeAll(tasks);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                Log.print(this, "Rendering interrupted.");
                break;
            }

            totalSamples.incrementAndGet();
            this.exportRendering();

            long elapsedTime = System.currentTimeMillis() - startTime;
            Log.print(this, "Rendering: " + totalSamples.get() + " samples, " + elapsedTime + " milliseconds per sample.");
        }

        executor.shutdown();
    }


    private RgbColor scatter(Ray ray) {
        RgbColor outColor = mBackgroundColor;
        Ray currentRay = ray;

        //Direct lighting
        Intersection closestIntersection = findClosestIntersection(currentRay);
        if (closestIntersection != null) {
            Vec3 viewDir = closestIntersection.point().sub(mScene.getCameras().getFirst().getOrigin()).normalize();
            outColor = computeDirectLighting(closestIntersection, viewDir);

            //Ambient Occlusion
            if (mUseAo)
                outColor = outColor.multRGB(computeAmbientOcclusion(closestIntersection));
        }

        if (!mUseGi)
            return outColor;

        int giDepth = 0;
        while (giDepth < mGiLevel) {
            //GI Samples
            outColor = outColor.add(computeGiSamples(currentRay, mGiSamples));

            closestIntersection = findClosestIntersection(currentRay);
            if (closestIntersection == null)
                break;

            Vec3 viewDir = closestIntersection.point().sub(mScene.getCameras().getFirst().getOrigin()).normalize();
            Ray scatteredRay = closestIntersection.material().scatter(closestIntersection.point(), closestIntersection.normal(), viewDir);

            //no scattered ray
            if (scatteredRay == null)
                break;

            currentRay = scatteredRay;
            giDepth++;
        }

        return outColor;
    }


    private Intersection findClosestIntersection(Ray ray) {
        Intersection closestIntersection = null;
        float closestDistance = Float.MAX_VALUE;

        for (SceneObject obj : mScene.getObjects()) {
            Intersection hit = obj.hit(ray);
            if (hit != null && hit.distance() < closestDistance) {
                closestDistance = hit.distance();
                closestIntersection = hit;
            }
        }

        return closestIntersection;
    }

    private RgbColor computeDirectLighting(Intersection intersection, Vec3 viewDir) {
        RgbColor directLighting = intersection.material().sampleColor(intersection.point(), intersection.normal(), viewDir, mScene.getLights());

        if (isInShadow(intersection))
            directLighting = RgbColor.BLACK;

        return directLighting;
    }

    private boolean isInShadow(Intersection intersection) {
        for (Light light : mScene.getLights()) {
            if (mSoftShadows) {

                int shadowHits = 0;
                for (int i = 0; i < mLightSamples; i++) {

                    float radius = light.getRadius();
                    Vec3 jitter = new Vec3(
                            (float)(Math.random() - 0.5f) * radius,
                            (float)(Math.random() - 0.5f) * radius,
                            (float)(Math.random() - 0.5f) * radius
                    );

                    Vec3 jitteredLightPos = light.getPosition().add(jitter);
                    Vec3 lightDirection = jitteredLightPos.sub(intersection.point()).normalize();
                    float lightDistance = jitteredLightPos.sub(intersection.point()).length();

                    Vec3 shadowRayOrigin = intersection.point().add(lightDirection.multScalar(EPSILON));
                    Ray shadowRay = new Ray(shadowRayOrigin, lightDirection);

                    Intersection shadowIntersection = findClosestIntersection(shadowRay);

                    if (shadowIntersection != null && shadowIntersection.distance() < lightDistance - EPSILON)
                        shadowHits++;
                }

                //in shadow if most samples are occluded
                if (shadowHits > mLightSamples / 2)
                    return true;

            } else {
                //single ray check
                Vec3 lightDirection = light.getPosition().sub(intersection.point()).normalize();
                float lightDistance = light.getPosition().sub(intersection.point()).length();

                Vec3 shadowRayOrigin = intersection.point().add(lightDirection.multScalar(EPSILON));
                Ray shadowRay = new Ray(shadowRayOrigin, lightDirection);

                Intersection shadowIntersection = findClosestIntersection(shadowRay);

                if (shadowIntersection != null && shadowIntersection.distance() < lightDistance - EPSILON)
                    return true;
            }
        }

        return false;
    }

    private RgbColor computeGiSamples(Ray ray, int samples) {
        RgbColor giColor = RgbColor.BLACK;

        //GI samples
        for (int sampleCount = 0; sampleCount < samples; sampleCount++) {
            RgbColor sampleColor = RgbColor.BLACK;
            Ray bounceRay = ray;

            //GI Depth
            int bounceDepth = 0;
            while (bounceDepth < mGiLevel) {
                Intersection giIntersection = findClosestIntersection(bounceRay);

                if (giIntersection == null)
                    break;  // No intersection found

                // Sample color from the material at the intersection
                Vec3 viewDir = mScene.getCameras().getFirst().getOrigin().sub(giIntersection.point()).normalize();
                RgbColor giSampleColor = giIntersection.material().sampleColor(giIntersection.point(), giIntersection.normal(), viewDir, mScene.getLights());

                // Add the GI contribution from the sample
                sampleColor = sampleColor.add(giSampleColor);

                Ray nextRay = giIntersection.material().scatter(giIntersection.point(), giIntersection.normal(), viewDir);
                if (nextRay == null)
                    break;  // No more scattering, exit recursion

                bounceRay = nextRay;
                bounceDepth++;
            }

            // Average from all bounces
            giColor = giColor.add(sampleColor.multScalar(1.0f / (mGiLevel + 1)));
        }

        // Average out the GI over the samples
        return giColor.multScalar(1.0f / samples);  // Normalize by number of samples
    }

    private RgbColor computeAmbientOcclusion(Intersection intersection) {
        int occlusionCount = 0;

        Vec3 normal = intersection.normal();
        Vec3 point = intersection.point();

        for (int i = 0; i < mAoSamples; i++) {
            Vec3 randomDir = randomInHemisphere(normal);

            // Offset to avoid self-intersection
            Vec3 aoOrigin = point.add(normal.multScalar(EPSILON));
            Ray aoRay = new Ray(aoOrigin, randomDir);

            Intersection aoHit = findClosestIntersection(aoRay);

            if (aoHit != null && aoHit.distance() < mAoDistance)
                occlusionCount++;
        }

        float occlusionFactor = 1.0f - (occlusionCount / (float) mAoSamples);
        return new RgbColor(occlusionFactor, occlusionFactor, occlusionFactor);
    }


    public int[] render(int samples) {
        int width = mBufferedImage.getWidth();
        int height = mBufferedImage.getHeight();
        int[] pixels = new int[width * height];

        Camera camera = mScene.getCameras().getFirst();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // Generate random offsets for anti-aliasing (for the current sample)
                float offsetX = (float) (Math.random() - 0.5) / width;
                float offsetY = (float) (Math.random() - 0.5) / height;

                // Get the primary ray with the random offset
                Ray primaryRay = camera.getRay((float) (x + offsetX) / width, (float) (y + offsetY) / height);
                RgbColor pixelColor = mBackgroundColor;  // Default color (background)

                Intersection closestIntersection = null;
                float closestDistance = Float.MAX_VALUE;

                // Find the closest intersection
                for (SceneObject obj : mScene.getObjects()) {
                    Intersection hit = obj.hit(primaryRay);

                    if (hit != null && hit.distance() < closestDistance) {
                        closestDistance = hit.distance();
                        closestIntersection = hit;
                    }
                }

                // If an intersection was found, use the object's material color
                if (closestIntersection != null) {
                    Vec3 viewDir = camera.getOrigin().sub(closestIntersection.point()).normalize();
                    pixelColor = closestIntersection.material().sampleColor(closestIntersection.point(), closestIntersection.normal(), viewDir, mScene.getLights());

                    // TODO: Shadow Rays, Ambient occlusion, etc.
                }

                pixels[y * width + x] = Tonemapper.ACES(pixelColor).getRGB();
            }
        }

        return pixels;
    }

    public int[] generateRadialGradient() {
        int width = mBufferedImage.getWidth();
        int height = mBufferedImage.getHeight();
        int[] pixels = new int[width * height];

        int centerX = width / 2;
        int centerY = height / 2;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                float dx = (float) (x - centerX) / centerX;
                float dy = (float) -(y - centerY) / centerY;

                float angle = (float) Math.atan2(dy, dx);
                float u = (float) (Math.cos(angle) * 0.5 + 0.5);
                float v = (float) (Math.sin(angle) * 0.5 + 0.5);

                int r = (int) (u * 255);
                int g = (int) (v * 255);
                int b = 128;

                pixels[y * width + x] = (r << 16) | (g << 8) | b;  //bitshift um rgb in 32bit integer zu quetschen
            }
        }

        return pixels;
    }


    public int[] generateUVGradient() {
        int width = mBufferedImage.getWidth();
        int height = mBufferedImage.getHeight();
        int[] pixels = new int[width * height];

        for (int y = 0; y < height; y++) {
            float v = (float) y / (height - 1);
            for (int x = 0; x < width; x++) {
                float u = (float) x / (width - 1);

                int r = (int) (u * 255);
                int g = (int) (v * 255);
                int b = 0;

                pixels[y * width + x] = (r << 16) | (g << 8) | b;  //bitshift um rgb in 32bit integer zu quetschen
            }
        }
        return pixels;
    }

    public int[] generateDiskIntersection(int radius) {
        int width = mBufferedImage.getWidth();
        int height = mBufferedImage.getHeight();
        int[] pixels = new int[width * height];

        int centerX = width / 2;
        int centerY = height / 2;
        int radiusSquared = radius * radius; //Radius Squared statt Wurzel für bessere Performance!

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int dx = x - centerX;
                int dy = y - centerY;
                int distanceSquared = dx * dx + dy * dy;

                if (distanceSquared <= radiusSquared)
                    pixels[y * width + x] = (0 << 16) | (255 << 8) | 0; //bitshift um rgb in 32bit integer zu quetschen

                else
                    pixels[y * width + x] = (0 << 16) | (0 << 8) | 255; //bitshift um rgb in 32bit integer zu quetschen
            }
        }

        return pixels;
    }

    public int[] generateRaySphereIntersections() {
        int width = mBufferedImage.getWidth();
        int height = mBufferedImage.getHeight();
        int[] pixels = new int[width * height];

        // Camera setup (position, direction, fov, aspect ratio, etc.)
        Camera camera = new Camera(new Vec3(0, 0, 0), new Vec3(0, 0, -1), new Vec3(0.0f, 1.0f, 0.0f), (float) width / height, 90f);

        // Sphere setup (position, radius)
        Material material = new LambertMaterial(RgbColor.MAGENTA);

        Sphere sphere = new Sphere(new Vec3(0 , 0, 0), 1f, material);

        Light light = new Light(new Vec3(10, -10, 0), RgbColor.RED, 1f, 1f);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Ray primaryRay = camera.getRay((float) x / width, (float) y / height);

                //RgbColor hitColor = sphere.hit(primaryRay);
                //if (hitColor != null)
                //    pixels[y * width + x] = hitColor.getRGB();
                //else
                pixels[y * width + x] = mBackgroundColor.getRGB();
            }
        }

        return pixels;
    }
}
