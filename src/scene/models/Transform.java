package scene.models;

import utils.algebra.Matrix4x4;
import utils.algebra.Vec3;

public class Transform {
    Matrix4x4 matrix;

    public Transform(Vec3 translation) {
        this(translation, new Vec3(0 , 0, 0), new Vec3(1, 1, 1));
    }

    public Transform(Vec3 translation, Vec3 rotation, Vec3 scale) {
        //translation
        Matrix4x4 translationMatrix = new Matrix4x4();
        translationMatrix.setValueAt(0, 3, translation.x);
        translationMatrix.setValueAt(1, 3, translation.y);
        translationMatrix.setValueAt(2, 3, translation.z);

        float rotationX = (float) Math.toRadians(rotation.x);
        float rotationY = (float) Math.toRadians(rotation.y);
        float rotationZ = (float) Math.toRadians(rotation.z);

        // rotation matrices
        double cosX = Math.cos(rotationX), sinX = Math.sin(rotationX);
        double cosY = Math.cos(rotationY), sinY = Math.sin(rotationY);
        double cosZ = Math.cos(rotationZ), sinZ = Math.sin(rotationZ);

        Matrix4x4 rotationXMatrix = new Matrix4x4();
        rotationXMatrix.setValueAt(1, 1, cosX);
        rotationXMatrix.setValueAt(1, 2, -sinX);
        rotationXMatrix.setValueAt(2, 1, sinX);
        rotationXMatrix.setValueAt(2, 2, cosX);

        Matrix4x4 rotationYMatrix = new Matrix4x4();
        rotationYMatrix.setValueAt(0, 0, cosY);
        rotationYMatrix.setValueAt(0, 2, sinY);
        rotationYMatrix.setValueAt(2, 0, -sinY);
        rotationYMatrix.setValueAt(2, 2, cosY);

        Matrix4x4 rotationZMatrix = new Matrix4x4();
        rotationZMatrix.setValueAt(0, 0, cosZ);
        rotationZMatrix.setValueAt(0, 1, -sinZ);
        rotationZMatrix.setValueAt(1, 0, sinZ);
        rotationZMatrix.setValueAt(1, 1, cosZ);

        //scale
        Matrix4x4 scaleMatrix = new Matrix4x4();
        scaleMatrix.setValueAt(0, 0, scale.x);
        scaleMatrix.setValueAt(1, 1, scale.y);
        scaleMatrix.setValueAt(2, 2, scale.z);

        Matrix4x4 rotationMatrix = rotationZMatrix.mult(rotationYMatrix).mult(rotationXMatrix);
        this.matrix = translationMatrix.mult(rotationMatrix).mult(scaleMatrix);
    }

    public Matrix4x4 getMatrix() {
        return this.matrix;
    }
}
