import lights.Light;
import materials.*;
import scene.models.Transform;
import scene.models.TriangleModel;
import scene.models.primitives.Plane;
import scene.models.primitives.Sphere;
import cameras.PerspectiveCamera;
import raytracer.Raytracer;
import ui.Window;
import scene.Scene;
import utils.RgbColor;
import utils.algebra.Vec2;
import utils.algebra.Vec3;
import utils.io.DataImporter;

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

    static final int IMAGE_WIDTH = 400;
    static final int IMAGE_HEIGHT = 400;

    /** CORNELL_BOX_DIMENSION **/

    static final float BOX_DIMENSION = 2f;

    /** RAYTRACER **/

    static final int NUMBER_OF_SAMPLES = 128;
    static final int ANTI_ALIASING = 2;
    static final float ANTI_ALIASING_FILTER_WIDTH = 1.2f;

    static final boolean USE_SOFT_SHADOWS = false;

    /** LIGHT **/
    static final short LIGHT_DENSITY = 20;
    static final short LIGHT_SAMPLES = 1;

    static final RgbColor BACKGROUND_COLOR = RgbColor.GRAY;

    static final Vec3 LIGHT_POSITION = new Vec3(0f, 1.99f, 0f);
    static final float AREA_LIGHT_SIZE = 1.24f;

    /** GI **/
    static final boolean USE_GI = true;
    static final int GI_LEVEL = 5;
    static final int GI_SAMPLES = 1;

    static final RgbColor LIGHT_COLOR = RgbColor.WHITE;
    static final RgbColor AMBIENT_LIGHT = new RgbColor(0.01f, 0.01f, 0.01f);

    static final boolean USE_AO = false;
    static final int NUMBER_OF_AO_SAMPLES = 1;
    static final float AO_MAX_DISTANCE = 1.5f;

    /** CAMERA **/

    static final Vec3 CAM_POS = new Vec3(0, 0f, 7.6243f);
    static final Vec3 LOOK_AT = new Vec3(0, 0f, 0);
    static final Vec3 UP_VECTOR = new Vec3(0.0f, 1.0f, 0.0f);

    static final float VIEW_ANGLE = 39.5978f;

    static final Window renderWindow = new Window(IMAGE_WIDTH, IMAGE_HEIGHT);

    /** DEBUG **/

    static final boolean SHOW_PARAM_LABEL = false;

    /** Initial method. This is where the show begins. **/
    public static void main(String[] args) {
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
        //renderScene.addCamera(new OrthographicCamera(CAM_POS, LOOK_AT, UP_VECTOR, (float) IMAGE_WIDTH / IMAGE_HEIGHT, 5));

        renderScene.addCamera(new PerspectiveCamera(CAM_POS, LOOK_AT, UP_VECTOR, (float) IMAGE_WIDTH / IMAGE_HEIGHT, VIEW_ANGLE));

        renderScene.addLight(new Light(LIGHT_POSITION.sub(new Vec3(0f, 0.05f, 0f)), LIGHT_COLOR, 0.9f, 1f));
        renderScene.addObject(new Plane(new Transform(LIGHT_POSITION), new Vec3(0, -1, 0), new Vec2(AREA_LIGHT_SIZE / 2, AREA_LIGHT_SIZE /2), new UnlitMaterial(LIGHT_COLOR)));

        Material white = new ReflectiveMaterial(RgbColor.WHITE, RgbColor.WHITE, AMBIENT_LIGHT, 32f, 1f, false);
        Material yellow = new ReflectiveMaterial(RgbColor.YELLOW, RgbColor.WHITE, AMBIENT_LIGHT, 32f, 1f, false);
        Material green = new ReflectiveMaterial(RgbColor.RED, RgbColor.WHITE, AMBIENT_LIGHT, 32f, 1f, false);

        Vec2 planeScale = new Vec2(BOX_DIMENSION, BOX_DIMENSION);
        // Floor (White)
        renderScene.addObject(new Plane(new Transform(new Vec3(0, -BOX_DIMENSION, 0)), new Vec3(0, 1, 0), planeScale, white));

        // Cieling (White)
        renderScene.addObject(new Plane(new Transform(new Vec3(0, BOX_DIMENSION, 0)), new Vec3(0, -1, 0), planeScale, white));

        // Left Wall (Red)
        renderScene.addObject(new Plane(new Transform(new Vec3(-BOX_DIMENSION, 0, 0)), new Vec3(1, 0, 0), planeScale, yellow));

        // Right Wall (Green)
        renderScene.addObject(new Plane(new Transform(new Vec3(BOX_DIMENSION, 0, 0)), new Vec3(-1, 0, 0), planeScale, green));

        // Back Wall (White)
        renderScene.addObject(new Plane(new Transform(new Vec3(0, 0, -BOX_DIMENSION)), new Vec3(0, 0, 1), planeScale, white));

        Material metallic = new ReflectiveMaterial(RgbColor.MAGENTA, RgbColor.WHITE, AMBIENT_LIGHT, 64f, 0.1f, true);
        renderScene.addObject(new Sphere(new Transform(new Vec3(-0.869228f, -1.50883f, -0.088344f)), 0.5f, metallic));

        Material refractive = new RefractiveMaterial(1.45f);
        renderScene.addObject(new Sphere(new Transform(new Vec3(0.271078f, -1.50987f, 1.13429f), new Vec3(0 , 0, 0), new Vec3(1, 1, 1)), 0.5f, refractive));

        //renderScene.addObject(new Sphere(new Transform(new Vec3(-2, -3, 0)), 1f, metallic));

        //renderScene.addObject(new Sphere(new Transform(new Vec3(0f, -3, 2)), 1f, refractive));

        renderScene.addObject(new Sphere(new Transform(new Vec3(-0.75f, 0, -0.5f), new Vec3(45 , 0, 45), new Vec3(1, 1, 2)), 0.5f, white));

        //renderScene.addObject(new TriangleModel(DataImporter.loadObjFile("assets/teapot.obj", new Transform(new Vec3(0, -2, 0), new Vec3(0 ,0, 0), new Vec3(0.5f, 0.5f, 0.5f)), metallic)));
    }

    /** Create our personal renderer and give it all of our items and prefs to calculate our scene **/
    private static void raytraceScene(Window renderWindow, Scene renderScene) {
        Raytracer raytracer = new Raytracer(
                renderScene,
                renderWindow,
                NUMBER_OF_SAMPLES,
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
