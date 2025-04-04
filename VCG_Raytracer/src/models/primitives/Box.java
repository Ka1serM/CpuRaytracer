package models.primitives;

import materials.Material;
import models.ray.Intersection;
import models.ray.Ray;
import scene.SceneObject;
import utils.algebra.Vec3;

public class Box implements SceneObject {
    private final Material material;
    private final Vec3 min;   // Untere Ecke der Box (kleinste x, y, z)
    private final Vec3 max;   // Obere Ecke der Box (größte x, y, z)

    public Box(Vec3 min, Vec3 max, Material material) {
        this.min = min;
        this.max = max;
        this.material = material;
    }

    /**
     * Prüft, ob und wann der Ray die Box (AABB) trifft.
     * Verwendet die Slab-Methode: Wir schneiden den Ray mit den 6 Ebenen der Box
     * und schauen, ob es einen gemeinsamen Bereich auf dem Ray gibt, in dem er die Box durchquert.
     */
    public float intersect(Ray ray) {
        float tMin = Float.NEGATIVE_INFINITY; // Größter Eintrittspunkt
        float tMax = Float.POSITIVE_INFINITY; // Kleinster Austrittspunkt

        Vec3 rayOrigin = ray.origin();
        Vec3 rayDirection = ray.direction();

        // ====================
        // Schnitt mit X-Achse
        // ====================
        float invDirX = 1.0f / rayDirection.x; // Inverse Richtung für Multiplikation statt Division
        float t0X = (min.x - rayOrigin.x) * invDirX;
        float t1X = (max.x - rayOrigin.x) * invDirX;

        // Wenn Richtung negativ, tausche Ein-/Austritt
        if (invDirX < 0.0f) {
            float temp = t0X;
            t0X = t1X;
            t1X = temp;
        }

        // Aktualisiere das gültige t-Fenster
        tMin = Math.max(tMin, t0X);
        tMax = Math.min(tMax, t1X);

        // Wenn sich das Fenster schließt, kein Schnittpunkt
        if (tMin > tMax)
            return Float.POSITIVE_INFINITY;

        // ====================
        // Y-Achse
        // ====================
        float invDirY = 1.0f / rayDirection.y;
        float t0Y = (min.y - rayOrigin.y) * invDirY;
        float t1Y = (max.y - rayOrigin.y) * invDirY;

        if (invDirY < 0.0f) {
            float temp = t0Y;
            t0Y = t1Y;
            t1Y = temp;
        }

        tMin = Math.max(tMin, t0Y);
        tMax = Math.min(tMax, t1Y);

        if (tMin > tMax)
            return Float.POSITIVE_INFINITY;

        // ====================
        // Z-Achse
        // ====================
        float invDirZ = 1.0f / rayDirection.z;
        float t0Z = (min.z - rayOrigin.z) * invDirZ;
        float t1Z = (max.z - rayOrigin.z) * invDirZ;

        if (invDirZ < 0.0f) {
            float temp = t0Z;
            t0Z = t1Z;
            t1Z = temp;
        }

        tMin = Math.max(tMin, t0Z);
        tMax = Math.min(tMax, t1Z);

        if (tMin > tMax)
            return Float.POSITIVE_INFINITY;

        // Rückgabe: frühester gültiger Schnittpunkt
        return tMin > 0 ? tMin : tMax;
    }

    /**
     * Gibt die Intersection zurück, wenn der Ray die Box trifft.
     */
    @Override
    public Intersection hit(Ray ray) {
        float t = intersect(ray);
        if (t == Float.POSITIVE_INFINITY)
            return null; // Kein Treffer

        // Schnittpunkt berechnen: O + t * D
        Vec3 hitPoint = ray.origin().add(ray.direction().multScalar(t));

        // Normale an der getroffenen Fläche berechnen
        Vec3 normal = computeNormal(hitPoint);

        return new Intersection(t, hitPoint, normal, material);
    }

    /**
     * Bestimmt die Oberflächennormale basierend auf dem Ort des Schnittpunkts.
     * Dafür wird geprüft, ob die Koordinate des Schnittpunkts fast exakt einer Box-Seite entspricht.
     */
    private Vec3 computeNormal(Vec3 hitPoint) {
        Vec3 normal = new Vec3(0, 0, 0); // Standard: Null-Vektor
        float epsilon = 1e-6f; // Toleranz für Gleitkomma-Vergleich

        // Prüfe, ob der Schnittpunkt nahe einer der Flächen liegt
        if (Math.abs(hitPoint.x - min.x) < epsilon)
            normal = new Vec3(-1, 0, 0); // linke Fläche
        else if (Math.abs(hitPoint.x - max.x) < epsilon)
            normal = new Vec3(1, 0, 0);  // rechte Fläche
        else if (Math.abs(hitPoint.y - min.y) < epsilon)
            normal = new Vec3(0, -1, 0); // untere Fläche
        else if (Math.abs(hitPoint.y - max.y) < epsilon)
            normal = new Vec3(0, 1, 0);  // obere Fläche
        else if (Math.abs(hitPoint.z - min.z) < epsilon)
            normal = new Vec3(0, 0, -1); // vordere Fläche
        else if (Math.abs(hitPoint.z - max.z) < epsilon)
            normal = new Vec3(0, 0, 1);  // hintere Fläche

        return normal;
    }
}
