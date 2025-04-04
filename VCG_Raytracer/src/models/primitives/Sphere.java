package models.primitives;

import materials.EmissiveMaterial;
import materials.Material;
import models.ray.Intersection;
import models.ray.Ray;
import scene.SceneObject;
import utils.algebra.Vec3;

public class Sphere implements SceneObject {
    private Vec3 center;
    private float radius;
    private Material material;

    // Konstruktor für die Kugel
    public Sphere(Vec3 center, float radius, Material material) {
        this.center = center;
        this.radius = radius;
        this.material = material;
    }

    @Override
    public Intersection hit(Ray ray) {
        // Berechne den Vektor vom Kugelmittelpunkt zum Ursprung des Strahls (ray.origin)
        Vec3 oc = ray.origin().sub(center);  // Vektor vom Strahlursprung zum Kugelmittelpunkt

        // Strahlengleichung: P(t) = O + t * D
        // Kugelgleichung: |P - C|^2 = r^2
        // Setze P(t) in die Kugelgleichung ein: |(O + t * D) - C|^2 = r^2
        // Daraus ergibt sich eine quadratische Gleichung in t

        // Berechne die Parameter der quadratischen Gleichung
        // a = D·D (Skalarprodukt der Richtung mit sich selbst)
        float a = ray.direction().scalar(ray.direction());

        // b = 2 * D·(O - C) (2 mal das Skalarprodukt der Strahldirection mit dem Vektor vom Ursprung zum Mittelpunkt)
        float b = 2.0f * oc.scalar(ray.direction());

        // c = |O - C|^2 - r^2 (Abstand zwischen Strahlursprung und Kugelmittelpunkt quadriert minus der Quadrat des Radius)
        float c = oc.scalar(oc) - radius * radius;

        // Berechne die Diskriminante der quadratischen Gleichung Δ = b² - 4ac
        float discriminant = b * b - 4.0f * a * c; // Diskriminante

        // Wenn die Diskriminante >= 0, gibt es Schnittpunkte
        if (discriminant >= 0) {
            // Berechne die Wurzeln der quadratischen Gleichung, um die möglichen Schnittpunkte zu finden
            float sqrtDiscriminant = (float) Math.sqrt(discriminant); // Wurzel aus der Diskriminante

            // Berechne die beiden möglichen Schnittpunkte (t1 und t2)
            float t1 = (-b - sqrtDiscriminant) / (2.0f * a);  // Erste Lösung der quadratischen Gleichung
            float t2 = (-b + sqrtDiscriminant) / (2.0f * a);  // Zweite Lösung der quadratischen Gleichung

            // Wähle die kleinere positive Lösung für t (den tatsächlichen Schnittpunkt)
            // t1 und t2 sind die Schnittpunkte des Strahls mit der Kugeloberfläche
            float t = t1 > 0 ? t1 : t2;

            // Wenn t > 0, dann gibt es einen tatsächlichen Schnittpunkt
            if (t > 0) {
                // Berechne den Schnittpunkt des Strahls mit der Kugel
                Vec3 hitPoint = ray.origin().add(ray.direction().multScalar(t));  // Treffpunkt P(t)

                // Berechne die Normalenrichtung an diesem Punkt auf der Kugel
                Vec3 normal = hitPoint.sub(center).normalize();  // Normalenvektor am Schnittpunkt

                // Wenn das Material der Kugel ein EmissiveMaterial ist, drehe die Normalenrichtung um
                if (material instanceof EmissiveMaterial) {
                    normal = center.sub(hitPoint).normalize();  // Invertiere den Normalenvektor für EmissiveMaterial
                }

                // Erstelle ein Intersection-Objekt und gebe es zurück
                return new Intersection(t, hitPoint, normal, material);
            }
        }

        return null;  // Kein Schnittpunkt, daher null zurückgeben
    }
}
