package models.primitives;

import materials.Material;
import models.ray.Intersection;
import models.ray.Ray;
import scene.SceneObject;
import utils.algebra.Vec2;
import utils.algebra.Vec3;

public class Plane implements SceneObject {
    private final Material material;
    private final Vec3 origin;   // Punkt auf der Ebene
    private final Vec3 normal;   // Normalenvektor der Ebene
    private final Vec2 scale;    // Ausdehnung der Ebene (z. B. Fläche)

    public Plane(Vec3 origin, Vec3 normal, Vec2 scale, Material material) {
        this.origin = origin;
        this.normal = normal.normalize(); // Normalisieren für korrekte Berechnungen
        this.scale = scale;
        this.material = material;
    }

    // Berechne Schnittpunkt-Strahl und Ebene
    public float intersect(Ray ray) {
        // Gegeben: Ebenengleichung: (P - O) · N = 0
        // Ray-Gleichung: P(t) = R_o + t * R_d
        // Einsetzen: (R_o + t * R_d - O) · N = 0
        // Umformen nach t: t = ((O - R_o) · N) / (R_d · N)

        float denom = normal.scalar(ray.direction()); // Nenner: R_d · N

        if (Math.abs(denom) > 1e-6) { // Nicht parallel?
            Vec3 diff = origin.sub(ray.origin());     // (O - R_o)
            float t = diff.scalar(normal) / denom;    // t = (O - R_o) · N / (R_d · N)
            return (t >= 0) ? t : Float.POSITIVE_INFINITY; // Nur positive t (vor dem Auge)
        }

        return Float.POSITIVE_INFINITY; // Kein Schnittpunkt (Strahl parallel zur Ebene)
    }

    @Override
    public Intersection hit(Ray ray) {
        float t = intersect(ray);

        if (t == Float.POSITIVE_INFINITY)
            return null; // Kein Schnittpunkt

        Vec3 hitPoint = ray.origin().add(ray.direction().multScalar(t)); // P(t) berechnen

        // Berechne zwei Tangentenvektoren (rechtwinklig zur Normalen)
        Vec3 tangent1 = (Math.abs(normal.y) > 0.9f)
                ? new Vec3(1, 0, 0)
                : normal.cross(new Vec3(0, 1, 0)).normalize();
        Vec3 tangent2 = normal.cross(tangent1).normalize();

        // Lokale Koordinaten relativ zum Ursprung der Ebene
        Vec3 localVec = hitPoint.sub(origin);
        float localX = localVec.scalar(tangent1); // Projektion auf Tangente 1
        float localY = localVec.scalar(tangent2); // Projektion auf Tangente 2

        // Überprüfen, ob Punkt innerhalb der "sichtbaren" Fläche liegt
        if (Math.abs(localX) <= scale.x / 2 && Math.abs(localY) <= scale.y / 2)
            return new Intersection(t, hitPoint, normal, material); // Treffer innerhalb Fläche

        return null; // Schnittpunkt liegt außerhalb der geplanten Fläche
    }
}
