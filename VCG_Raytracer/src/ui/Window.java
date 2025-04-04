package ui;

import utils.RgbColor;
import utils.algebra.Vec2;
import utils.io.DataExporter;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Window {

    private int mWidth;
    private int mHeight;

    private BufferedImage mBufferedImage;

    private JFrame mFrame;

    /**
     Create render window with the given dimensions
     **/
    public Window(int width, int height){
        mWidth = width;
        mHeight = height;

        // we are using only one frame
        mBufferedImage = new BufferedImage(mWidth, mHeight, BufferedImage.TYPE_INT_RGB);

        createFrame();
    }

    public BufferedImage getBufferedImage(){
        return mBufferedImage;
    }

    /**
     Setup render frame with given parameters
     **/
    private void createFrame(){
        JFrame frame = new JFrame();

        frame.getContentPane().add(new JLabel(new ImageIcon(mBufferedImage)));
        frame.setSize(mBufferedImage.getHeight() + frame.getSize().height, mBufferedImage.getWidth() + frame.getSize().width);
        frame.pack();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);

        mFrame = frame;
    }

    /**
     Draw debug information
     **/
    private void setOutputLabel(String text, int recursions, int antiAliasing){
        Graphics graphic = mBufferedImage.getGraphics();
        graphic.setColor(Color.black);
        graphic.fill3DRect(0,mHeight - 30,mWidth,mHeight,true);
        graphic.setColor(Color.green);
        graphic.drawString("Elapsed rendering time: " + text + " sec, Recursions: " + recursions + ", AA: x" + antiAliasing, 10, mHeight - 10);

        mFrame.repaint();
    }

    /**
     Draw pixel to our render frame
     **/
    public void setPixel(BufferedImage bufferedImage, RgbColor color, Vec2 screenPosition){
        bufferedImage.setRGB((int)screenPosition.x, (int)screenPosition.y, color.getRGB());
        mFrame.repaint();
    }

    /**
     Export the rendering to an PNG image with rendering information
     **/
    public void exportRendering(String text, int recursions, int antiAliasing, boolean showLabel){

        if(showLabel) {
            setOutputLabel(text, recursions, antiAliasing);
        }
        DataExporter.exportImageToPng(mBufferedImage, "raytracing.png");
    }


}
