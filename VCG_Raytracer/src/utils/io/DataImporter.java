package utils.io;

//import com.interactivemesh.jfx.importer.obj.ObjModelImporter;
//import javafx.scene.shape.MeshView;

import java.io.File;

public class DataImporter {

	public DataImporter() {}

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
