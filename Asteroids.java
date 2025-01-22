// javac Asteroids.java
// java Asteroids

import java.util.Vector;
import java.util.Random;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.AbstractAction;
import javax.swing.JComboBox;

import javax.imageio.ImageIO;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.awt.Graphics;
import java.awt.Graphics2D;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;

public class Asteroids {
    public Asteroids()
    {
        setup();
    }

    public static void setup() {
        appFrame = new JFrame("Asteroids");
        XOFFSET = 0;
        YOFFSET = 40;
        WINWIDTH = 500;
        WINHEIGHT = 500;

        pi = 3.14159265358979;
        twoPi = 2.0 * 3.14159265358979;

        endgame = false;

        p1width = 25;
        p1height = 25;
        p1originalX = (double)XOFFSET + ((double)WINWIDTH / 2.0) -
                (p1width / 2.0);
        p1originalY = (double)YOFFSET + ((double)WINHEIGHT / 2.0) -
                (p1height / 2.0);

        playerBullets = new Vector<ImageObject>();
        playerBulletsTimes = new Vector<Long>();
        bulletWidth = 5;
        playerbulletlifetime = new Long(1600); // 0.75;
        enemybulletlifetime = new Long(1600);
        explosionlifetime = new Long(800);
        playerbulletgap = 1;
        flamecount = 1;
        flamewidth = 12.0;
        expcount = 1;

        level = 3;

        asteroids = new Vector<ImageObject>();
        asteroidsTypes = new Vector<Integer>();
        ast1width = 32;
        ast2width = 21;
        ast3width = 26;

        try
        {
            background = ImageIO.read(newFile("space.png"));

            player = ImageIO.read(newFile("player.png"));
            flame1 = ImageIO.read(newFile("flameleft.png"));
            flame2 = ImageIO.read(newFile("flamecenter.png"));
            flame3 = ImageIO.read(newFile("flameright.png"));
            flame4 = ImageIO.read(newFile("blueflameleft.png"));
            flame5 = ImageIO.read(newFile("blueflamecenter.png"));
            flame6 = ImageIO.read(newFile("blueflameright.png"));

            ast1 = ImageIO.read(newFile("ast1.png"));
            ast2 = ImageIO.read(newFile("ast2.png"));
            ast3 = ImageIO.read(newFile("ast3.png"));

            playerBullet = ImageIO.read(newFile("playerbullet.png"));
            enemyShip = ImageIO.read(newFile("enemy.png"));
            enemyBullet = ImageIO.read(newFile("enemybullet.png"));

            exp1 = ImageIO.read(newFile("explosion1.png"));
            exp2 = ImageIO.read(newFile("explosion2.png"));
        }
        catch(IOException ioe)
        {
            // NOP
        }
    }

    private static class Animate implements Runnable
    {
        public void run()
        {
            while (endgame == false)
            {
                backgroundDraw();
                asteroidsDraw();
                explosionsDraw();
                enemyBulletsDraw();
                enemyDraw();
                playerBulletsDraw();
                playerDraw();
                flameDraw();

                try
                {
                    Thread.sleep(32);
                }
                catch(InterruptedException e)
                {
                    // NOP
                }
            }
        }
    }

    private static void insertPlayerBullet()
    {
        ImageObject bullet = new ImageObject(0, 0, bulletWidth,
                bulletWidth, p1.getAngle());
        lockrotateObjAroundObjtop(bullet, p1, p1width / 2.0);
        playerBullets.addElement(bullet);
        playerBulletsTimes.addElement(System.currentTimeMillis());
    }

    private static void insertEnemyBullet()
    {
        try
        {
            // randomize angle here
            Random randomNumbers = new Random(LocalTime.now().getNano());

            ImageObject bullet = new ImageObject(enemy.getX() +
                    enemy.getWidth()/2.0, enemy.getY() + enemy.getHeight() / 2.0,
                    bulletWidth, bulletWidth, randomNumbers.nextInt(360));
            //lockrorateObjAroundObjbottom(bullet, enemy, enemy.getWidth()/2.0);
            enemyBullets.addElement(bullet);
            enemyBulletsTimes.addElement(System.currentTimeMillis());
        }
        catch(java.lang.NullPointerException jlnpe)
        {
            // NOP
        }
    }


}
