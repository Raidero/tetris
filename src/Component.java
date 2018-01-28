/**
 * Created by Grzegorz on 2017-05-22.
 */
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;

/**
 * shows what is inside the frame
 */
public class Component extends JPanel {
    /**
     * width of the frame
     */
    private static final int width = 400;
    /**
     * height of the frame
     */
    private static final int height = 480;
    /**
     * describes how many pixels has side of one Tetris Block
     */
    private static final int sideBlockSize = 24;
    /**
     * size of the font used in program
     */
    private static final int fontSize = 20;
    /**
     * links Module class with Component to show get to know where Tetris Blocks are
     */
    private Module module;
    private BufferedImage imageI;
    private BufferedImage imageJ;
    private BufferedImage imageL;
    private BufferedImage imageO;
    private BufferedImage imageS;
    private BufferedImage imageT;
    private BufferedImage imageZ;
    /**
     * constructor of component class, set size of frame, default background color, loads all images that are needed
     * and thats about it
     * @param module links component with module to be able to get position of all Tetris Blocks on map
     */
    public Component(Module module) {
        this.module = module;
        setPreferredSize(new Dimension(width, height));
        setBackground(Color.black);
        File imageFile = new File("ImageI.png");
        try {
            imageI = ImageIO.read(imageFile);
        } catch (IOException e) {
            System.err.println("Error: cannot read image file\n");
            e.printStackTrace();
        }
        imageFile = new File("ImageJ.png");
        try {
            imageJ = ImageIO.read(imageFile);
        } catch (IOException e) {
            System.err.println("Error: cannot read image file\n");
            e.printStackTrace();
        }
        imageFile = new File("ImageL.png");
        try {
            imageL = ImageIO.read(imageFile);
        } catch (IOException e) {
            System.err.println("Error: cannot read image file\n");
            e.printStackTrace();
        }
        imageFile = new File("ImageO.png");
        try {
            imageO = ImageIO.read(imageFile);
        } catch (IOException e) {
            System.err.println("Error: cannot read image file\n");
            e.printStackTrace();
        }
        imageFile = new File("ImageS.png");
        try {
            imageS = ImageIO.read(imageFile);
        } catch (IOException e) {
            System.err.println("Error: cannot read image file\n");
            e.printStackTrace();
        }
        imageFile = new File("ImageT.png");
        try {
            imageT = ImageIO.read(imageFile);
        } catch (IOException e) {
            System.err.println("Error: cannot read image file\n");
            e.printStackTrace();
        }
        imageFile = new File("ImageZ.png");
        try {
            imageZ = ImageIO.read(imageFile);
        } catch (IOException e) {
            System.err.println("Error: cannot read image file\n");
            e.printStackTrace();
        }
    }
    /**
     * refreshes all things that are painted inside the frame. It paint all blocks on map, score, and, if needed,
     * message that inform that the game is over when the function is called
     * @param g it is an 2d graphics object used to draw all things like strings, images and rectanges on our map
     */
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setFont(new Font("TimesNewRoman", 1, fontSize));
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 20; j++) {
                switch (module.getAreaPoint(i, j)) {
                    case 1:
                        g2d.drawImage(imageI, i*sideBlockSize, j*sideBlockSize, this);
                        break;
                    case 2:
                        g2d.drawImage(imageJ, i*sideBlockSize, j*sideBlockSize, this);
                        break;
                    case 3:
                        g2d.drawImage(imageL, i*sideBlockSize, j*sideBlockSize, this);
                        break;
                    case 4:
                        g2d.drawImage(imageO, i*sideBlockSize, j*sideBlockSize, this);
                        break;
                    case 5:
                        g2d.drawImage(imageS, i*sideBlockSize, j*sideBlockSize, this);
                        break;
                    case 6:
                        g2d.drawImage(imageT, i*sideBlockSize, j*sideBlockSize, this);
                        break;
                    case 7:
                        g2d.drawImage(imageZ, i*sideBlockSize, j*sideBlockSize, this);
                        break;
                    default:
                        g2d.drawRect(i*sideBlockSize, j*sideBlockSize, sideBlockSize, sideBlockSize);
                        break;
                }
            }
        }
        for (int j = 0; j < 4; j++) {
            switch (module.getNextFigure()) {
                case 1:
                    g2d.drawImage(imageI, sideBlockSize*(module.getXnextFigure(j) + 12), sideBlockSize*(module.getYnextFigure(j) + 1), this);
                    break;
                case 2:
                    g2d.drawImage(imageJ, sideBlockSize*(module.getXnextFigure(j) + 12), sideBlockSize*(module.getYnextFigure(j) + 1), this);
                    break;
                case 3:
                    g2d.drawImage(imageL, sideBlockSize*(module.getXnextFigure(j) + 12), sideBlockSize*(module.getYnextFigure(j) + 1), this);
                    break;
                case 4:
                    g2d.drawImage(imageO, sideBlockSize*(module.getXnextFigure(j) + 12), sideBlockSize*(module.getYnextFigure(j) + 1), this);
                    break;
                case 5:
                    g2d.drawImage(imageS, sideBlockSize*(module.getXnextFigure(j) + 12), sideBlockSize*(module.getYnextFigure(j) + 1), this);
                    break;
                case 6:
                    g2d.drawImage(imageT, sideBlockSize*(module.getXnextFigure(j) + 12), sideBlockSize*(module.getYnextFigure(j) + 1), this);
                    break;
                case 7:
                    g2d.drawImage(imageZ, sideBlockSize*(module.getXnextFigure(j) + 12), sideBlockSize*(module.getYnextFigure(j) + 1), this);
                    break;
                default:
                    break;
            }
        }
        g2d.setColor(Color.WHITE);
        if(module.isPaused()) {
            g2d.drawString("Przegrales. Twoj wynik to: " + module.getScore(), fontSize, height>>1);
            g2d.drawString("Nacisnij r aby sprobowac jeszcze raz", fontSize, (height>>1) + fontSize
            );
        }
        g2d.drawString("Wynik: " + module.getScore(), 11 * sideBlockSize, height - fontSize);
    }
}

