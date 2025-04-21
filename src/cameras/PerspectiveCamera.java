package cameras;

import raytracer.ray.Ray;
import utils.algebra.Vec3;

import javax.swing.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.Set;

public class PerspectiveCamera implements Camera, KeyListener {
    private Vec3 origin;
    private Vec3 prevOrigin;
    private Vec3 viewVector;
    private Vec3 prevViewVector;
    private Vec3 lowerLeft;
    private Vec3 horizontal;
    private Vec3 vertical;
    private Vec3 rightVector;
    private Vec3 upVector;

    private final float halfWidth;
    private final float halfHeight;

    private final float mouseSensitivity = 0.002f;
    private final float moveSpeed = 0.1f;
    private int prevMouseX = -1;
    private int prevMouseY = -1;
    private final Set<Integer> pressedKeys = new HashSet<>();

    private final float movementThreshold = 0.001f;  // Threshold to consider the camera as "moved"

    public PerspectiveCamera(JFrame jframe, Vec3 origin, Vec3 lookAt, Vec3 up, float aspect, float fovDegrees) {
        this.origin = origin;
        this.prevOrigin = origin;

        float theta = (float) Math.toRadians(fovDegrees);
        this.halfHeight = (float) Math.tan(theta / 2);
        this.halfWidth = aspect * halfHeight;

        setupCameraOrientation(origin, lookAt, up);
        updateCameraProperties();

        setupMouseInput(jframe);

        jframe.addKeyListener(this);
        jframe.setFocusable(true);
        jframe.requestFocusInWindow();
    }

    private void setupCameraOrientation(Vec3 origin, Vec3 lookAt, Vec3 up) {
        this.viewVector = origin.sub(lookAt).normalize();
        this.rightVector = up.cross(viewVector).normalize();
        this.upVector = viewVector.cross(rightVector).normalize();

        this.prevViewVector = this.viewVector;
    }

    private void updateCameraProperties() {
        this.lowerLeft = origin
                .sub(rightVector.multScalar(halfWidth))
                .sub(upVector.multScalar(halfHeight))
                .sub(viewVector);

        this.horizontal = rightVector.multScalar(2 * halfWidth);
        this.vertical = upVector.multScalar(2 * halfHeight);
    }

    private void setupMouseInput(JFrame frame) {
        frame.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                mouseMoved(e);
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();

                if (prevMouseX != -1 && prevMouseY != -1) {
                    int dx = x - prevMouseX;
                    int dy = y - prevMouseY;

                    float yaw = -dx * mouseSensitivity;
                    float pitch = -dy * mouseSensitivity;

                    viewVector = rotateAroundAxis(viewVector, upVector, yaw).normalize();
                    rightVector = upVector.cross(viewVector).normalize();
                    viewVector = rotateAroundAxis(viewVector, rightVector, pitch).normalize();
                    upVector = viewVector.cross(rightVector).normalize();

                    updateCameraProperties();
                    prevViewVector = viewVector;
                }

                prevMouseX = x;
                prevMouseY = y;
            }
        });
    }

    private void updateKeyboardMovement() {
        Vec3 move = new Vec3();

        if (pressedKeys.contains(KeyEvent.VK_W))
            move = move.sub(viewVector.multScalar(moveSpeed));
        if (pressedKeys.contains(KeyEvent.VK_S))
            move = move.add(viewVector.multScalar(moveSpeed));
        if (pressedKeys.contains(KeyEvent.VK_A))
            move = move.sub(rightVector.multScalar(moveSpeed));
        if (pressedKeys.contains(KeyEvent.VK_D))
            move = move.add(rightVector.multScalar(moveSpeed));
        if (pressedKeys.contains(KeyEvent.VK_Q))
            move = move.sub(upVector.multScalar(moveSpeed));
        if (pressedKeys.contains(KeyEvent.VK_E))
            move = move.add(upVector.multScalar(moveSpeed));

        if (move.length() > 0) {
            origin = origin.add(move);
            updateCameraProperties();
            prevOrigin = origin;
        }
    }

    private Vec3 rotateAroundAxis(Vec3 v, Vec3 axis, float angle) {
        float cos = (float) Math.cos(angle);
        float sin = (float) Math.sin(angle);
        return v.multScalar(cos)
                .add(axis.cross(v).multScalar(sin))
                .add(axis.multScalar(axis.scalar(v) * (1 - cos)));
    }

    public Ray getRay(float s, float t) {
        Vec3 rayDirection = lowerLeft
                .add(horizontal.multScalar(s))
                .add(vertical.multScalar(t))
                .sub(origin)
                .normalize();

        return new Ray(origin, rayDirection);
    }

    @Override
    public boolean isMoving() {
        return origin.sub(prevOrigin).length() > movementThreshold || viewVector.sub(prevViewVector).length() > movementThreshold;
    }

    public Vec3 getOrigin() {
        return origin;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        pressedKeys.add(e.getKeyCode());
        updateKeyboardMovement();
    }

    @Override
    public void keyReleased(KeyEvent e) {
        pressedKeys.remove(e.getKeyCode());
        updateKeyboardMovement();
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Not used
    }
}
