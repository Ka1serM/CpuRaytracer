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

import cameras.Camera;
import cameras.PerspectiveCamera;
import lights.Light;
import materials.Material;
import materials.UnlitMaterial;
import scene.models.Transform;
import scene.models.primitives.Sphere;
import raytracer.ray.Intersection;
import raytracer.ray.Ray;
import scene.Scene;
import ui.Window;
import utils.*;
import utils.algebra.Vec3;
import utils.io.Log;

import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static raytracer.ray.RayUtils.*;

public class Raytracer {

    private BufferedImage mBufferedImage;

    private Scene mScene;
    private Window mRenderWindow;

    private int mMaxNumSamples;


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
        mMaxNumSamples = recursions;

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
        mRenderWindow.exportRendering(String.valueOf(stopTime(tStart)), mMaxNumSamples, mAntiAliasingSamples, mDebug);
    }

    /**  Stop time of rendering **/
    private static double stopTime(long tStart){
        long tEnd = System.currentTimeMillis();
        long tDelta = tEnd - tStart;
        return tDelta / 1000.0;
    }

    public void renderScene() {
        //gradient();
        //sphere();
        raytrace();
    }

    private void gradient() {

        int width = mBufferedImage.getWidth();
        int height = mBufferedImage.getHeight();

        PerspectiveCamera camera = new PerspectiveCamera(new Vec3(0, 0, 17f), new Vec3(0, 0, 0), new Vec3(0.0f, 1.0f, 0.0f), (float) width / height, 170f);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                float u = ((float)x + 0.5f) / width;
                float v = 1 - ((float)y + 0.5f) / height;
                Ray primaryRay = camera.getRay(u, v);
                Vec3 direction = primaryRay.direction().normalize();

                float r = 0.5f * (direction.x + 1.0f);
                float g = 0.5f * (direction.y + 1.0f);
                float b = 0.5f * (direction.z + 1.0f);

                mRenderWindow.setPixel(new RgbColor(r, g, b), x, y);
            }
        }

        this.exportRendering();
    }

    private void sphere() {

        int width = mBufferedImage.getWidth();
        int height = mBufferedImage.getHeight();

        PerspectiveCamera camera = new PerspectiveCamera(new Vec3(0, 0, 4f), new Vec3(0, 0, 0), new Vec3(0.0f, 1.0f, 0.0f), (float) width / height, 70f);

        Sphere sphere = new Sphere(new Transform(new Vec3(0, 0, 0)), 1f, new UnlitMaterial(RgbColor.RED));

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                float u = (float) x / width;
                float v = (float) y / height;
                Ray primaryRay = camera.getRay(u, v);

                Intersection hit = sphere.hit(primaryRay);

                RgbColor color = RgbColor.BLUE;
                if(hit != null)
                    color = RgbColor.GREEN;

                mRenderWindow.setPixel(color, x, y);
            }
        }

        this.exportRendering();
    }

    private void raytrace() {
        Log.print(this, "Prepare rendering at " + stopTime(tStart));

        Camera camera = mScene.getCameras().getFirst();
        int width = mBufferedImage.getWidth();
        int height = mBufferedImage.getHeight();

        RgbColor[] hdrColors = new RgbColor[width * height];
        Arrays.fill(hdrColors, RgbColor.BLACK);

        int numberOfBlocks = 64;
        ExecutorService executor = Executors.newFixedThreadPool(numberOfBlocks);

        int blocksX = (int) Math.sqrt(numberOfBlocks);
        int blocksY = numberOfBlocks / blocksX;
        int blockWidth = (int) Math.ceil((float) width / blocksX);
        int blockHeight = (int) Math.ceil((float) height / blocksY);

        final AtomicInteger globalSampleCount = new AtomicInteger(0);

        CyclicBarrier barrier = new CyclicBarrier(numberOfBlocks, () -> {
            int currentSample = globalSampleCount.incrementAndGet();
            Log.print(this, "Sample " + currentSample + "/" + mMaxNumSamples);

            for (int py = 0; py < height; py++) {
                int rowOffset = py * height;
                for (int px = 0; px < width; px++)
                    mRenderWindow.setPixel(hdrColors[rowOffset + px], px, py);
            }

            //Save Image every 8 Samples
            //if (currentSample % 8 == 0)
            //    this.exportRendering();
        });

        for (int by = 0; by < blocksY; by++) {
            for (int bx = 0; bx < blocksX; bx++) {
                final int startX = bx * blockWidth;
                final int endX = Math.min(startX + blockWidth, width);
                final int startY = by * blockHeight;
                final int endY = Math.min(startY + blockHeight, height);

                executor.submit(() -> {
                    for (int localSample = 0; localSample < mMaxNumSamples; localSample++) {
                        for (int py = startY; py < endY; py++) {
                            int rowOffset = py * width;
                            for (int px = startX; px < endX; px++) {
                                int index = rowOffset + px;

                                RgbColor colorAccum = new RgbColor(0, 0, 0);

                                //set filter width to zero if no AA so we dont offset
                                mAntiAliasingFilterWidth = (mAntiAliasingSamples == 1) ? 0 : mAntiAliasingFilterWidth;
                                for (int i = 0; i < mAntiAliasingSamples; i++) {
                                    float offsetX = RANDOM.nextFloat() * mAntiAliasingFilterWidth;
                                    float offsetY = RANDOM.nextFloat() * mAntiAliasingFilterWidth;

                                    Ray ray = camera.getRay((px + offsetX + 0.5f / width) / width, 1f - (py + offsetY + 0.5f / height) / height);

                                    colorAccum = colorAccum.add(traceRecursive(ray, 0));
                                }

                                colorAccum = colorAccum.multScalar(1f / mAntiAliasingSamples);

                                RgbColor oldColor = hdrColors[index];
                                RgbColor blended = colorAccum.add(oldColor.multScalar(localSample)).multScalar(1f / (localSample + 1f));
                                hdrColors[index] = blended;
                            }
                        }

                        Thread.yield();

                        try {
                            barrier.await();
                        } catch (InterruptedException | BrokenBarrierException e) {
                            Thread.currentThread().interrupt();
                        }
                    }
                });
            }
        }

        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private RgbColor traceRecursive(Ray ray, int depth) {
        Intersection intersection = findClosestIntersection(mScene.getObjects(), ray);
        if (intersection == null)
            return RgbColor.BLACK;

        Material material = intersection.material();
        RgbColor color = getDirectLighting(intersection, ray);

        if (mUseGi && depth < mGiLevel) {
            RgbColor giColor = RgbColor.BLACK;

            int samples = 0;
            while (samples < mGiSamples) {
                // Refraction
                Ray refractedRay = material.refract(intersection, ray);
                if (refractedRay != null)
                    giColor = giColor.add(traceRecursive(refractedRay, depth + 1).multScalar(material.getTransparency()));

                // Reflection
                Ray reflectedRay = material.reflect(intersection, ray);
                if (reflectedRay != null)
                    giColor = giColor.add(traceRecursive(reflectedRay, depth + 1).multScalar(material.getTransparency()));

                samples++;
            }

            // Average the GI results
            if (samples > 0) {
                giColor = giColor.multScalar(1.0f / samples);
                color = color.add(giColor);
            }
        }

        return color;
    }

    private RgbColor getDirectLighting(Intersection intersection, Ray ray) {

        //if unlit, just return the ambient color
        //if (intersection.material() instanceof UnlitMaterial)
        //    return intersection.material().getAlbedo();

        //If not in shadow, calculate material color
        RgbColor color = RgbColor.BLACK;
        if (!isInShadow(intersection))
            color = intersection.material().getDirectLighting(intersection, ray, mScene.getLights());

        if (mUseAo)
            color = color.multRGB(ambientOcclusion(intersection));

        return color;
    }

    private boolean isInShadow(Intersection intersection) {
        //mSoftShadows = false;

        Vec3 hitPosition = intersection.position();
        Vec3 normal = intersection.normal();

        //offset to avoid self-intersection (shadow acne)
        Vec3 origin = hitPosition.add(normal.multScalar(SHADOW_EPSYLON));

        for (Light light : mScene.getLights()) {
            Vec3 lightPosition = light.getPosition();
            float lightRadius = light.getRadius();

            int shadowHits = 0;
            int samples = mSoftShadows ? mLightSamples : 1;

            for (int i = 0; i < samples; i++) {
                //If soft shadows, jitter the light position within the light radius
                Vec3 jitteredLightPos = lightPosition;
                if (mSoftShadows) {
                    Vec3 jitter = new Vec3(
                            (2* RANDOM.nextFloat() - 1f) * lightRadius,
                            ((2 * RANDOM.nextFloat()) - 1f) * lightRadius,
                            ((2 * RANDOM.nextFloat()) - 1f) * lightRadius
                    );
                    jitteredLightPos = lightPosition.add(jitter);
                }

                Vec3 toLight = jitteredLightPos.sub(hitPosition);

                //cast ray from hit position to light
                Ray shadowRay = new Ray(origin, toLight.normalize());
                Intersection shadowHit = findClosestIntersection(mScene.getObjects(), shadowRay);

                // If the hit is closer than the light distance and it is not the same object, it is shadowed
                if (shadowHit != null && shadowHit.distance() < toLight.length()) {
                    if (!mSoftShadows)
                        return true;
                    shadowHits++; // Increment shadow hit counter
                }
            }

            //if more than half of the rays hit, its shadowed
            if (mSoftShadows && shadowHits > samples / 2)
                return true; // In shadow
        }

        return false;
    }




    private RgbColor ambientOcclusion(Intersection intersection) {
        int occlusionCount = 0;

        Vec3 normal = intersection.normal();
        Vec3 position = intersection.position();

        for (int i = 0; i < mAoSamples; i++) {
            Vec3 randomDir = randomInHemisphere(normal);

            //Offset origin along the normal to avoid self-intersection
            Vec3 aoOrigin = position.add(normal.multScalar(RAY_EPSYLON));
            Ray aoRay = new Ray(aoOrigin, randomDir);

            Intersection aoHit = findClosestIntersection(mScene.getObjects(), aoRay);

            // Only count occlusion if something is hit within max AO range
            if (aoHit != null && aoHit.distance() < mAoDistance)
                occlusionCount++;
        }

        // AO factor: 1 = fully lit, 0 = fully occluded
        float occlusionFactor = 1.0f - (occlusionCount / (float) mAoSamples);
        return new RgbColor(occlusionFactor, occlusionFactor, occlusionFactor);
    }
}
