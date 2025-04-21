package scene;

import lights.Light;
import cameras.Camera;
import scene.models.Hittable;
import scene.models.SceneObject;
import utils.io.Log;

import java.util.ArrayList;
import java.util.List;


public class Scene {

    private final List<Camera> cameras;
    private final List<SceneObject> objects;
    private final List<Light> lights;

    public Scene() 
    {
        Log.print(this, "Init");

        this.cameras = new ArrayList<>();
        this.objects = new ArrayList<>();
        this.lights = new ArrayList<>();
    }

    public void addCamera(Camera camera)
    {
        this.cameras.add(camera);
    }

    public void addObject(SceneObject object)
    {
        this.objects.add(object);
    }

    public void addLight(Light light)
    {
        this.lights.add(light);
    }

    public List<SceneObject> getObjects()
    {
        return this.objects;
    }

    public List<Light> getLights()
    {
        return this.lights;
    }

    public List<Camera> getCameras()
    {
        return this.cameras;
    }
}
