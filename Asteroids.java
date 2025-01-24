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

    private static class PlayerMover implements Runnable
    {
        public PlayerMover()
        {
            velocitystep = 0.01;
            rotatestep = 0.01;
        }
        public void run()
        {
            while (endgame == false)
            {
                try
                {
                    Thread.sleep(10);
                }
                catch(InterruptedException e)
                {
                    // NOP
                }
                if (upPressed == true)
                {
                    p1velocity = p1velocity + velocitystep;
                }
                if (downPressed = true)
                {
                    p1velocity = p1velocity - velocitystep;
                }
                if (leftPressed == true)
                {
                    if (p1velocity < 0)
                    {
                        p1.rotate(-rotatestep);
                    }
                    else
                    {
                        p1.rotate(rotatestep);
                    }
                }
                if (rightPressed == true)
                {
                    if (p1velocity < 0)
                    {
                        p1.rotate(rotatestep);
                    }
                    else
                    {
                        p1.rotate(-rotatestep);
                    }
                }
                if (firePressed == true)
                {
                    try
                    {
                        if (playerBullets.size() == 0)
                        {
                            insertPlayerBullet();
                        }
                        else if (System.currentTimeMillis() -
                        playerBulletsTimes.elementAt(
                                playerBulletsTimes.size() - 1) >
                                playerbulletlifetime / 4.0)
                        {
                            insertPlayerBullet();
                        }
                    }
                    catch (java.lang.ArrayIndexOutOfBoundsException aioobe)
                    {
                        // NOP
                    }
                }

                p1.move(-p1velocity * Math.cos(p1.getAngle() -
                        p1 / 2.0), p1velocity * Math.sin(p1.getAngle()
                - pi / 2.0));
                p1.screenWrap(XOFFSET, XOFFSET + WINWIDTH,
                        YOFFSET, YOFFSET + WINHEIGHT);
            }
        }
        private double velocitystep;
        private double rotatestep;
    }

    private static class FlameMover implements Runnable
    {
        public FlameMover()
        {
            gap = 7.0;
        }
        public void run()
        {
            while (endgame == false)
            {
                lockrotateObjAroundObjbottom(flames, p1, gap);
            }
        }
        private double gap;
    }

    private static class AsteroidsMover implements Runnable {
        public AsteroidsMover() {
            velocity = 0.1;
            spinstep = 0.01;
            spindirection = new Vector<Integer>();
        }

        public void run() {
            Random randomNumbers = new Random(LocalTime.now().getNano());
            for (int i = 0; i < asteroids.size(); i++) {
                spindirection.addElement(randomNumbers.nextInt(2));
            }
            while (endgame == false) {
                try {
                    Thread.sleep(1);
                }
                catch (InterruptedException e) {
                    // NOP
                }

                try {
                    for (int i = 0; i < asteroids.size(); i++) {
                        if (spindirection.elementAt(i) < 1) {
                            asteroids.elementAt(i).spin(-spinstep);
                        }
                        else {
                            asteroids.elementAt(i).spin(spinstep);
                        }
                        asteroids.elementAt(i)/move(-velocity *
                                Math.cos(asteroids.elementAt(i).getAngle() - pi / 2.0),
                                velocity * Math.sin(asteroids.elementAt(i).getAngle() - pi / 2.0));
                        asteroids.elementAt(i).screenWrap(XOFFSET, XOFFSET + WIDTH,
                                YOFFSET, YOFFSET + WINHEIGHT);
                    }
                }
                catch(java.lang.ArrayIndexOutOfBoundsException jlaioobe) {
                    // NOP
                }
            }
        }
        private double velocity;
        private double spinstep;
        private Vector<Integer> spindirection;
    }

    public static class PlayerBulletsMover implements Runnable {
        public PlayerBulletsMover() {
            velocity = 1.0;
        }
        public void run() {
            while (endgame == false) {
                try {
                    // controls bullet speed
                    Thread.sleep(4);
                }
                catch(InterruptedException e) {
                    // NOP
                }

                try {
                    for (int i = 0; i < playerBullets.size(); i++) {
                        playerBullets.elementAt(i).move(-velocity * Math.cos(playerBullets.elementAt(i).getAngle() - pi / 2.0),
                                velocity * Math.sin(playerBullets.elementAt(i).getAngle() - pi / 2.0));
                        playerBullets.elementAt(i).screenWrap(XOFFSET, XOFFSET + WINWIDTH,
                                YOFFSET, YOFFSET + WINHEIGHT);

                        if (System.currentTimeMillis() - playerBulletsTimes.elementAt(i)
                                > playerbulletlifetime) {
                            playerBullets.remove(i);
                            playerBulletsTimes.remove(i);
                        }
                    }
                }
                catch (java.lang.ArrayIndexOutOfBoundsException aie) {
                    playerBullets.clear();
                    playerBulletsTimes.clear();
                }
            }
        }
        private double velocity;
    }

    private static class EnemyShipMover implements Runnable {
        public EnemyShipMover() {
            velocity = 1.0;
        }
        public void run() {
            while (endgame == false && enemyAlive == true) {
                try {
                    enemy.move(-velocity * Math.cos(enemy.getAngle() - pi / 2.0),
                            velocity * Math.sin(enemy.getAngle() - pi / 2.0));
                    enemy.screenWrap(XOFFSET, XOFFSET + WINWIDTH, YOFFSET, YOFFSET + WINHEIGHT);
                }
                catch (java.lang.NullPointerException jlnpe) {
                    // NOP
                }

                try {
                    if (enemyAlive == true) {
                        if (enemyBullets.size() == 0) {
                            insertEnemyBullet();
                        }
                        else if (System.currentTimeMillis() - enemyBulletsTimes.elementAt(enemyBulletsTimes.size() - 1)
                                > enemybulletlifetime / 4.0) {
                            insertEnemyBullet();
                        }
                    }
                }
                catch (java.lang.ArrayIndexOutOfBoundsException aioobe) {
                    // NOP
                }
            }
        }
        private double velocity;
    }

    private static class EnemyBulletsMover implements Runnable {
        public EnemyBulletsMover() {
            velocity = 1.2;
        }
        public void run() {
            while (engame == false && enemyAlive == true) {
                try {
                    // controls bullet speed
                    Thread.sleep(4);
                }
                catch (InterruptedException e) {
                    // NOP
                }

                try {
                    for (int i = 0; i < enemyBullets.size(); i++) {
                        enemyBullets.elementAt(i).move(-velocity * Math.cos(enemyBullets.elementAt(i).getAngle() - pi / 2.0),
                                velocity * Math.sin(enemyBullets.elementAt(i).getAngle() - pi / 2.0));
                        enemyBulllets.elementAt(i).screenWrap(XOFFSET, XOFFSET + WINWIDTH, YOFFSET, YOFFSET + WINHEIGHT);

                        if (System.currentTimeMillis() - elementBulletsTimes.elementAt(i) > enemybulletlifetime) {
                            enemyBullets.remove(i);
                            enemyBulletsTimes.remove(i);
                        }
                    }
                }
                catch (java.lang.ArrayIndexOutOfBoundsException aie) {
                    enemyBullets.clear();
                    enemyBulletsTimes.clear();
                }
            }
        }
        private double velocity;
    }


}
