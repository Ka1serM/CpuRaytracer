package raytracer.ray;

import materials.Material;
import utils.algebra.Vec3;

public record Intersection(float distance, Vec3 position, Vec3 normal, Material material) {}