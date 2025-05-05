package utils.io;

//import com.interactivemesh.jfx.importer.obj.ObjModelImporter;
//import javafx.scene.shape.MeshView;

import materials.Material;
import scene.models.SceneObject;
import scene.models.Transform;
import scene.models.primitives.Triangle;
import utils.algebra.Vec3;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class DataImporter {

	public DataImporter() {}

	public static List<SceneObject> loadObjFile(String filePath, Transform transform, Material material) {
		List<Vec3> vertices = new ArrayList<>();
		List<Vec3> normals = new ArrayList<>();
		List<SceneObject> triangles = new ArrayList<>();

		try {
			BufferedReader br = new BufferedReader(new FileReader(filePath));
			String line;

			while ((line = br.readLine()) != null) {
				line = line.trim();
				String[] parts = line.split(" ");

				if (line.startsWith("v ")) { // Vertex
					float x = Float.parseFloat(parts[1]);
					float y = Float.parseFloat(parts[2]);
					float z = Float.parseFloat(parts[3]);
					vertices.add(new Vec3(x, y, z));

				} else if (line.startsWith("vn ")) { // Vertex Normal
					float nx = Float.parseFloat(parts[1]);
					float ny = Float.parseFloat(parts[2]);
					float nz = Float.parseFloat(parts[3]);
					normals.add(new Vec3(nx, ny, nz));

				} else if (line.startsWith("f ")) { // Face;
					String[] v1 = parts[1].split("//");
					String[] v2 = parts[2].split("//");
					String[] v3 = parts[3].split("//");

					// Indexes are 1-based in the .obj
					int vertexIndex1 = Integer.parseInt(v1[0]) - 1;
					int normalIndex1 = Integer.parseInt(v1[1]) - 1;

					int vertexIndex2 = Integer.parseInt(v2[0]) - 1;
					int normalIndex2 = Integer.parseInt(v2[1]) - 1;

					int vertexIndex3 = Integer.parseInt(v3[0]) - 1;
					int normalIndex3 = Integer.parseInt(v3[1]) - 1;

					triangles.add(
						new Triangle(
							transform.getMatrix().multVec3(vertices.get(vertexIndex1), true),
							transform.getMatrix().invert().transpose().multVec3(normals.get(normalIndex1), true),
							transform.getMatrix().multVec3(vertices.get(vertexIndex2), true),
							transform.getMatrix().invert().transpose().multVec3(normals.get(normalIndex2), true),
							transform.getMatrix().multVec3(vertices.get(vertexIndex3), true),
							transform.getMatrix().invert().transpose().multVec3(normals.get(normalIndex3), true),
							material
						)
					);
				}
			}

			br.close();

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return triangles;
	}



	public static void loadOBJ(String path) {
		File file = loadFile(path);

		//ObjModelImporter importer = new ObjModelImporter();
//		importer.read(file);
		//MeshView[] meshes = importer.getImport();

		Log.print(DataImporter.class, "... done!");
	}

	public static void loadTexture(String path) {
		File file = loadFile(path);

		Log.print(DataImporter.class, "... done!");
	}

	private static File loadFile(String path){
		Log.print(DataImporter.class, "Start importing " + path + " ...");

		return new File(path);
	}
}
