import lights.Light;
import materials.*;
import models.primitives.Plane;
import models.primitives.Sphere;
import raytracer.Camera;
import raytracer.Raytracer;
import ui.Window;
import scene.Scene;
import utils.RgbColor;
import utils.algebra.Vec2;
import utils.algebra.Vec3;

/*
    - THE RAYTRACER -

    TEAM:

    1.
    2.
    3.
    4.
 */

// Main application class. This is the routine called by the JVM to run the program.
public class Main {

    /** RESOLUTION **/

    static final int IMAGE_WIDTH = 800;
    static final int IMAGE_HEIGHT = 800;

    /** CORNELL_BOX_DIMENSION **/

    static final float BOX_DIMENSION = 4f;

    /** RAYTRACER **/

    static final int RECURSIONS = 2024;
    static final int ANTI_ALIASING = 1;
    static final float ANTI_ALIASING_FILTER_WIDTH = 2f;

    static final boolean USE_SOFT_SHADOWS = true;

    /** LIGHT **/
    static final short LIGHT_DENSITY = 20;
    static final short LIGHT_SAMPLES = 4;

    static final RgbColor BACKGROUND_COLOR = RgbColor.GRAY;

    static final Vec3 LIGHT_POSITION = new Vec3(0f, 3.95f, 0f);
    static final short AREA_LIGHT_SIZE = 2;

    /** GI **/
    static final boolean USE_GI = true;
    static final int GI_LEVEL = 3;
    static final int GI_SAMPLES = 1;

    static final RgbColor LIGHT_COLOR = RgbColor.WHITE;
    static final RgbColor AMBIENT_LIGHT = new RgbColor(0.1f, 0.1f, 0.1f);

    static final boolean USE_AO = true;
    static final int NUMBER_OF_AO_SAMPLES = 4;
    static final float AO_MAX_DISTANCE = 0.75f;

    /** CAMERA **/

    static final Vec3 CAM_POS = new Vec3(0, -0.33f, -14);
    static final Vec3 LOOK_AT = new Vec3(0, -0.33f, -15);
    static final Vec3 UP_VECTOR = new Vec3(0.0f, -1.0f, 0.0f);

    static final float VIEW_ANGLE = 50f;

    /** DEBUG **/

    static final boolean SHOW_PARAM_LABEL = true;

    /** Initial method. This is where the show begins. **/
    public static void main(String[] args) {
        Window renderWindow = new Window(IMAGE_WIDTH, IMAGE_HEIGHT);
        draw(renderWindow);
    }

    /**  Draw the scene using our Raytracer **/
    private static void draw(Window renderWindow) {
        Scene renderScene = new Scene();

        setupScene(renderScene);

        raytraceScene(renderWindow, renderScene);
    }

    /** Setup all components that we want to see in our scene **/
    private static void setupScene(Scene renderScene) {
        setupCameras(renderScene);
        setupCornellBox(renderScene);
        setupObjects(renderScene);
        setupLights(renderScene);
    }

    private static void setupLights(Scene renderScene) {
    }

    private static void setupObjects(Scene renderScene) {
    }

    private static void setupCameras(Scene renderScene) {
    }

    private static void setupCornellBox(Scene renderScene) {
        renderScene.addCamera(new Camera(CAM_POS, LOOK_AT, UP_VECTOR, (float) IMAGE_WIDTH / IMAGE_HEIGHT, VIEW_ANGLE));

        renderScene.addLight(new Light(LIGHT_POSITION, LIGHT_COLOR, 8f, 5f));
        renderScene.addObject(new Plane(LIGHT_POSITION, new Vec3(0, -1, 0), new Vec2(AREA_LIGHT_SIZE, AREA_LIGHT_SIZE), new UnlitMaterial(LIGHT_COLOR)));

        Material white = new LambertMaterial(RgbColor.WHITE);
        Material yellow = new LambertMaterial(RgbColor.YELLOW);
        Material green = new LambertMaterial(RgbColor.GREEN);

        Vec2 planeScale = new Vec2(BOX_DIMENSION * 2, BOX_DIMENSION * 2);
        // Floor (White)
        renderScene.addObject(new Plane(new Vec3(0, -BOX_DIMENSION, 0), new Vec3(0, 1, 0), planeScale, white));

        // Cieling (White)
        renderScene.addObject(new Plane(new Vec3(0, BOX_DIMENSION, 0), new Vec3(0, -1, 0), planeScale, white));

        // Left Wall (Red)
        renderScene.addObject(new Plane(new Vec3(-BOX_DIMENSION, 0, 0), new Vec3(1, 0, 0), planeScale, yellow));

        // Right Wall (Green)
        renderScene.addObject(new Plane(new Vec3(BOX_DIMENSION, 0, 0), new Vec3(-1, 0, 0), planeScale, green));

        // Back Wall (White)
        renderScene.addObject(new Plane(new Vec3(0, 0, BOX_DIMENSION), new Vec3(0, 0, -1), planeScale, white));

        Material emissive = new EmissiveMaterial(new RgbColor(0.5f, 0.1f, 0f));
        renderScene.addObject(new Sphere(new Vec3(-2, -3, 0), 1f, emissive));

        //Material refractive = new RefractiveMaterial(0.1f);
        Material refractive = new LambertMaterial(RgbColor.MAGENTA);
        renderScene.addObject(new Sphere(new Vec3(1.5f, -3, -1), 1f, refractive));

        Material metallic = new MetallicMaterial(RgbColor.WHITE, 0.02f);
        renderScene.addObject(new Sphere(new Vec3(-1, -3, -3f), 1f, metallic));
    }

    /** Create our personal renderer and give it all of our items and prefs to calculate our scene **/
    private static void raytraceScene(Window renderWindow, Scene renderScene) {
        Raytracer raytracer = new Raytracer(
                renderScene,
                renderWindow,
                RECURSIONS,
                BACKGROUND_COLOR,
                AMBIENT_LIGHT,
                ANTI_ALIASING,
                ANTI_ALIASING_FILTER_WIDTH,
                USE_GI,
                GI_SAMPLES,
                GI_LEVEL,
                USE_AO,
                NUMBER_OF_AO_SAMPLES,
                AO_MAX_DISTANCE,
                USE_SOFT_SHADOWS,
                LIGHT_SAMPLES,
                LIGHT_DENSITY,
                SHOW_PARAM_LABEL);

        raytracer.renderScene();
    }
}
