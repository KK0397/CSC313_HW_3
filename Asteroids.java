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
    public Asteroids() {
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

        try {
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
        catch(IOException ioe) {
            // NOP
        }
    }

    private static class Animate implements Runnable {
        public void run() {
            while (endgame == false) {
                backgroundDraw();
                asteroidsDraw();
                explosionsDraw();
                enemyBulletsDraw();
                enemyDraw();
                playerBulletsDraw();
                playerDraw();
                flameDraw();

                try {
                    Thread.sleep(32);
                }
                catch(InterruptedException e) {
                    // NOP
                }
            }
        }
    }

    private static void insertPlayerBullet() {
        ImageObject bullet = new ImageObject(0, 0, bulletWidth,
                bulletWidth, p1.getAngle());
        lockrotateObjAroundObjtop(bullet, p1, p1width / 2.0);
        playerBullets.addElement(bullet);
        playerBulletsTimes.addElement(System.currentTimeMillis());
    }

    private static void insertEnemyBullet() {
        try {
            // randomize angle here
            Random randomNumbers = new Random(LocalTime.now().getNano());

            ImageObject bullet = new ImageObject(enemy.getX() +
                    enemy.getWidth()/2.0, enemy.getY() + enemy.getHeight() / 2.0,
                    bulletWidth, bulletWidth, randomNumbers.nextInt(360));
            //lockrorateObjAroundObjbottom(bullet, enemy, enemy.getWidth()/2.0);
            enemyBullets.addElement(bullet);
            enemyBulletsTimes.addElement(System.currentTimeMillis());
        }
        catch(java.lang.NullPointerException jlnpe) {
            // NOP
        }
    }

    private static class PlayerMover implements Runnable
    {
        public PlayerMover() {
            velocitystep = 0.01;
            rotatestep = 0.01;
        }
        public void run() {
            while (endgame == false) {
                try {
                    Thread.sleep(10);
                }
                catch(InterruptedException e) {
                    // NOP
                }
                if (upPressed == true) {
                    p1velocity = p1velocity + velocitystep;
                }
                if (downPressed = true) {
                    p1velocity = p1velocity - velocitystep;
                }
                if (leftPressed == true) {
                    if (p1velocity < 0) {
                        p1.rotate(-rotatestep);
                    }
                    else {
                        p1.rotate(rotatestep);
                    }
                }
                if (rightPressed == true) {
                    if (p1velocity < 0) {
                        p1.rotate(rotatestep);
                    }
                    else {
                        p1.rotate(-rotatestep);
                    }
                }
                if (firePressed == true) {
                    try {
                        if (playerBullets.size() == 0) {
                            insertPlayerBullet();
                        }
                        else if (System.currentTimeMillis() -
                        playerBulletsTimes.elementAt(
                                playerBulletsTimes.size() - 1) >
                                playerbulletlifetime / 4.0) {
                            insertPlayerBullet();
                        }
                    }
                    catch (java.lang.ArrayIndexOutOfBoundsException aioobe) {
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
        public void run() {
            while (endgame == false) {
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
            while (endgame == false && enemyAlive == true) {
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

    private static class CollisionChecker implements Runnable {
        public void run() {
            Random randomNumbers = new Random (LocalTime.now().getNano());
            while (endgame == false) {
                try {
                    // compares all asteroids to all player bullets
                    for (int i = 0; i < asteroids.size(); i++) {
                        for (int j = 0; j < playerBullets.size(); j++) {
                            if (collisionOccurs(asteroids.elementAt(i),
                                    playerBullets.elementAt(j)) == true) {
                                // delete asteroid
                                // show explosion animation
                                // replace old asteroid with two new, smaller asteroids
                                    // at same place, random directions.
                                double posX = asteroids.elementAt(i).getX();
                                double posY = asteroids.elementAt(i).getY();

                                // create explosion!
                                explosions.addElement(new ImageObject(posX, posY, 27, 24, 0.0));
                                explosionsTimes.addElement(System.currentTimeMillis());

                                // create two new asteroids of type 2
                                if (asteroidsTypes.elementAt(i) == 1) {
                                    asteroids.addElement(new ImageObject(posX, posY, ast2width, ast2width, (double) (randomNumbers.nextInt(360))));
                                    asteroidsTypes.addElement(2);
                                    asteroids.remove(i);
                                    asteroidsTypes.remove(i);
                                    playerBullets.remove(j);
                                    playerBullesTimes.remove(j);
                                }

                                // create two new asteroids of type 3
                                if (asteroidsTypes.elementAt(i) == 2) {
                                    asteroids.addElement(new ImageObject(posX, posY, ast3width, ast3width, (double) (randomNumbers.nextInt(360))));
                                    asteroidsTypes.addElement(3);
                                    asteroids.remove(i);
                                    asteroidsTypes.remove(i);
                                    playerBullets.remove(j);
                                    playerBullesTimes.remove(j);
                                }

                                // delete asteroids
                                if (asteroids.Types.elementAt(i) == 3) {
                                    asteroids.remove(i);
                                    asteroidsTypes.remove(i);
                                    playerBullets.remove(j);
                                    playerBullesTimes.remove(j);
                                }
                            }
                        }
                    }

                    // compare all asteroids to player
                    for (int i = 0; i < asteroids.size(); i++) {
                        if (collisionOccurs(asteroids.elementAt(i), p1) == true) {
                            endgame = true;
                            System.out.println("Game Over. You lose!");
                        }
                    }

                    try {
                        // compare all player bullets to enemy ship
                        for (int i = 0; i < playerBullets.size(); i++) {
                            if (collisionOccurs(playerBullets.elementAt(i), enemy) == true) {
                                double posX = enemy.getX();
                                double posY = enemy.getY();

                                // create explosion!
                                explosions.addElement(new ImageObject(posX, posY, 27, 24, 0.0));
                                explosionsTimes.addElement(System.currentTimeMillis());

                                playerBullets.remove(i);
                                playerBulletsTimes.remove(i);
                                enemyAlive = false;
                                enemy = null;
                                enemyBullets.clear();
                                enemyBulletsTimes.clear();
                            }
                        }

                        // compare enemy ship to player
                        if (collisionOccurs(enemy, p1) == true) {
                            endgame == true;
                            System.out.println("Game Over. You Lose!");
                        }

                        // compare all enemy bullets to player
                        for (int i = 0; i < enemyBullets.size(); i++) {
                            if (collisionOccurs(enemyBullets.elementAt(i), p1) == true) {
                                endgame = true;
                                System.out.println("Game Over. You Lose!");
                            }
                        }
                    }
                    catch(java.lang.NullPointerException jlnpe) {
                        // NOP
                    }
                }
                catch (java.lang.ArrayIndexOutOfBoundsException jlaioobe) {
                    //NOP
                }
            }
        }
    }

    private static class WinChecker implements Runnable {
        public void run() {
            while (endgame == false) {
                if (asteroids.size() == 0) {
                    endgame = true;
                    System.out.println("Game Over. You Lose!");
                }
            }
        }
    }

    private static void generateAsteroids() {
        asteroids = new Vector<ImageObject>();
        asteroidsTypes = new Vector<Integer>();
        RAndom randomNumbers = new Random(LocalTimes.now().getNano());

        for (int i = 0; i < level; i++) {
            asteroids addElement(new ImageObject (XOFFSET +
                    (double) (randomNumbers.nextInt(WINWIDTH)), YOFFSET +
                    (double) (randomNumbers.nextInt(WINHEIGHT)), ast1width, ast1width,
                    (double) (randomNumbers.nextInt(360))));
            asteroidsTypes.addElement(1);
        }
    }

    private static void generateEnemy() {
        try {
            Random randomNumbers = new Random(LocalTime.now().getNano());
            enemy = new ImageObject (XOFFSET + (double) (randomNumbers.nextInt(WINWIDTH)),
                    YOFFSET + (double) (randomNumbers.nextInt(WINHEIGHT)), 29.0, 16.0,
                    (double) (randomNumbers.nextInt(360)));
        }
        catch (java.lang.IllegalArgumentException jliae) {
            // NOP
        }
    }

    // *dist is a distance between the two objects at the bottom of objInner
    private static void lockrotateObjAroundObjbottom (ImageObject objOuter, ImageObject objInner, double dist) {
        objOuter.moteto(objInner.getX() + objOuter.getWidth() + (objInner.getWidth() / 2.0 +
                (dist + objInner.getWidth() / 2.0) * Math.cos(-objInner.getAngle() + pi / 2.0))
                / 2.0, objInner.getY() - objOuter.getHeight() + (dist + objInner.getHieght() / 2.0)
                * Math.sin(-objInner.getAngle() / 2.0));
        objOuter.setAngle(objInner.getAngle());
    }

    // *dist is a distance between the two objects at the top of the inner object
    private static void lockrotateObjAroundObjtop (ImageObject objOuter, ImageObject objInner, double dist) {
        objOuter.moteto(objInner.getX() + objOuter.getWidth() + (objInner.getWidth() / 2.0 +
                (dist + objInner.getWidth() / 2.0) * Math.cos(objInner.getAngle() + pi / 2.0))
                / 2.0, objInner.getY() - objOuter.getHeight() + (dist + objInner.getHieght() / 2.0)
                * Math.sin(objInner.getAngle() / 2.0));
        objOuter.setAngle(objInner.getAngle());
    }

    private static AffineTransformOp rotateImageObject (ImageObject obj) {
        AffineTransform at = AffineTransform.getRotateInstance(-obj.getAngle(),
                obj.getWidth() / 2.0, obj.getHeight() / 2.0);
        AffineTransformOp atop = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
        return atop;
    }

    private static AffineTransformOp spinImageObject (ImageObject obj) {
        AffineTransform at = AffineTransform.getRotateInstance(-obj.getInternalAngle(),
                obj.getWIdth() / 2.0, obj.getHeight() / 2.0);
        AffineTransformOp atop = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
        return atop;
    }

    private static void backgroundDraw() {
        Graphics g = appFrame.getGraphics();
        Graphics2D g2D = (Graphics2D) g;
        g2D.drawImage(background, XOFFSET, YOFFSET, null);
    }

    private static void enemyBulletsDraw() {
        Graphics g = appFrame.getGraphics();
        Graphics2D g2D = (Graphics2D) g;
        for(int i = 0; i < enemyBullets.size(); i++) {
            g2D.drawImage(enemyBullet, (int) (enemyBullets.elementAt(i).getX() + 0.5),
                    (int) (enemyBullets.elementAt(i).getY() + 0.5), null);
        }
    }

    private static void enemyDraw() {
        if (enemyAlive == true) {
            try {
                Graphics g = appFrame.getGraphics();
                Graphics2D g2D = (Graphics2D) g;
                g2D.drawImage(enemyShip, (int) (enemy.getX() + 0.5), (int) (enemy.getY() + 0.5), null);
            }
            catch (java.lang.NullPointerException jlnpe) {
                // NOP
            }
        }
    }

    private static void playerBulletsDraw() {
        Graphics g = appFrame.getGraphics();
        Graphics2D g2D = (Graphics2D) g;
        try {
            for (int i = 0; i < playerBullets.size(); i ++) {
                g2d.drawImage(rotateImageObject(playerBullets.elementAt(i)).filter(playerBullet, null),
                        (int) (playerBullets.elementAt(i).getX() + 0.5), (int) (playerBullets.elementAt(i).getY() + 0.5), null);
            }
        }
        catch (java.lang.ArrayIndexOutOfBoundsException aioobe) {
            playerBullets.clear();
            playerBulletsTimes.clear();
        }
    }

    private static void playerDraw() {
        Graphics g = appFrame.getGraphics();
        Graphics2D g2D = (Graphics2D) g;
        g2D.drawImage(rotateImageObject(p1).filter(player, null), (int) (p1.getX() + 0.5), (int) (p1.getY() + 0.5), null);
    }

    private static void flameDraw() {
        if (upPressed == true) {
            Graphics g = appFrame.getGraphics();
            Graphics2D g2D = (Graphics2D) g;
            if (flamecount == 1) {
                g2D.drawImage(rotateImageObject(flames).filter(flame1, null), (int) (flames.getX() + 0.5), (int) (flames.getY() + 0.5), null);
                flamecount = 1 + ((flamecount + 1) % 3);
            }
            else if (flamecount == 2) {
                g2D.drawImage(rotateImageObject(flames).filter(flame2, null), (int) (flames.getX() + 0.5), (int) (flames.getY() + 0.5), null);
                flamecount = 1 + ((flamecount + 1) % 3);
            }
            else if (flamecount == 3) {
                g2D.drawImage(rotateImageObject(flames).filter(flame3, null), (int) (flames.getX() + 0.5), (int) (flames.getY() + 0.5), null);
                flamecount = 1 + ((flamecount + 1) % 3);
            }
        }
        if (downPressed == true) {
            Graphics g = appFrame.getGraphics();
            Graphics2D g2D = (Graphics2D) g;
            if (flamecount == 1) {
                g2D.drawImage(rotateImageObject(flames).filter(flame4, null), (int) (flames.getX() + 0.5), (int) (flames.getY() + 0.5), null);
                flamecount = 1 + ((flamecount + 1) % 3);
            }
            else if (flamecount == 2) {
                g2D.drawImage(rotateImageObject(flames).filter(flame5, null), (int) (flames.getX() + 0.5), (int) (flames.getY() + 0.5), null);
                flamecount = 1 + ((flamecount + 1) % 3);
            }
            else if (flamecount == 3) {
                g2D.drawImage(rotateImageObject(flames).filter(flame6, null), (int) (flames.getX() + 0.5), (int) (flames.getY() + 0.5), null);
                flamecount = 1 + ((flamecount + 1) % 3);
            }
        }
    }

    private static void asteroidsDraw() {
        Graphics g = appFrame.getGraphics();
        Graphics2D g2D = (Graphics2D) g;
        for (int i = 0; i < asteroids.size(); i++) {
            if (asteroidsTypes.elementAt(i) == 1) {
                g2D.drawImage(spinImageObject(asteroids.elementAt(i)).filter(ast1, null), (int) (asteroids.elementAt(i).getX() + 0.5),
                        (int) (asteroids.elementAt(i).getY() + 0.5), null);
            }
            if (asteroidsTypes.elementAt(i) == 2) {
                g2D.drawImage(spinImageObject(asteroids.elementAt(i)).filter(ast2, null), (int) (asteroids.elementAt(i).getX() + 0.5),
                        (int) (asteroids.elementAt(i).getY() + 0.5), null);
            }
            if (asteroidsTypes.elementAt(i) == 3) {
                g2D.drawImage(spinImageObject(asteroids.elementAt(i)).filter(ast3, null), (int) (asteroids.elementAt(i).getX() + 0.5),
                        (int) (asteroids.elementAt(i).getY() + 0.5), null);
            }
        }
    }

    private static void explosionsDraw() {
        Graphics g = appFrame.getGraphics();
        Graphics2D g2D = (Graphics2D) g;
        for (int i = 0; i < explosions.size(); i++) {
            if (System.currentTimeMillis() - explosionsTimes.elementAt(i) > explosionlifetime) {
                try {
                    explosions.remove(i);
                    explosionsTimes.remove(i);
                }
                catch (java.lang.NullPointerException jlnpe) {
                    explosions.clear();
                    explosionsTimes.clear();
                }
            }
            else {
                if (expcount == 1) {
                    g2d.drawImage(exp1, (int) (explosions.elementAt(i).getX() + 0.5),
                            (int) (explosions.elementAt(i).getY() + 0.5), null);
                    expcount = 2;
                }
                else if (expcount == 2) {
                    g2d.drawImage(exp2, (int) (explosions.elementAt(i).getX() + 0.5),
                            (int) (explosions.elementAt(i).getY() + 0.5), null);
                    expcount = 1;
                }
            }
        }
    }

    private static class KeyPressed extends AbstractAction {
        public KeyPressed() {
            action = "";
        }
        public KeyPressed (String input) {
            action = input;
        }

        public void actionPerformed (ActionEvent e) {
            if (action.equals("UP")) {
                upPressed = true;
            }
            if (action.equals("DOWN")) {
                downPressed = true;
            }
            if (action.equals("LEFT")) {
                leftPressed = true;
            }
            if (action.equals("RIGHT")) {
                rightPressed = true;
            }
            if (action.equals("F")) {
                firePressed = true;
            }
        }
        private String action;
    }

    private static class KeyReleased extends AbstractAction {
        public KeyReleased() {
            action = "";
        }

        public KeyReleased (String input) {
            action = input;
        }

        public void actionPerformed (ActionEvent e) {
            if (action.equals("UP")) {
                upPressed = false;
            }
            if (action.equals("DOWN")) {
                downPressed = false;
            }
            if (action.equals("LEFT")) {
                leftPressed = false;
            }
            if (action.equals("RIGHT")) {
                rightPressed = false;
            }
            if (action.equals("F")) {
                firePressed = false;
            }
        }
        private String action;
    }

    private static class QuitGame implements ActionListener {
        public void actionPerformed (ActionEvent e) {
            endgame = true;
        }
    }

    public static class StartGame implements ActionListener {
        public void actionPerformed (ActionEvent e) {
            endgame = true;
            enemyAlive = true;

            upPressed = false;
            downPressed = false;
            leftPressed = false;
            rightPressed = false;
            firePressed = false;

            p1 = new ImageObject (p1originalX, p1originalY, p1width, p1height, 0.0);
            p1velocity = 0.0;
            generateEnemy();

            flames = new ImageObject(p1originalX + p1width / 2.0, p1originalY + p1height, flamewidth, flamewidth, 0.0);
            flamecount = 1;
            expcount = 1;

            try {
                Thread.sleep(50);
            }
            catch (InterruptedException ie) {
                // NOP
            }

            playerBullets = new Vector<ImageObject>();
            playerBulletsTimes = new Vector<Long>();
            enemyBullets = new Vector<ImageObject>();
            enemyBulletsTimes = new Vector<ImageObject>();
            explosions = new Vector<ImageObject>();
            explosionsTimes = new Vector<Long>();
            generateAsteroids();
            endgame = false;

            Thread t1 = new Thread(new Animate());
            Thread t2 = new Thread(new PlayerMover());
            Thread t3 = new Thread(new FlameMover());
            Thread t4 = new Thread(new AsteroidsMover());
            Thread t5 = new Thread(new PlayerBulletsMover());
            Thread t6 = new Thread(new EnemyShipMover());
            Thread t7 = new Thread(new EnemyBulletsMover());
            Thread t8 = new Thread(new CollisionChecker());
            Thread t9 = new Thread(new WinChecker());

            t1.start();
            t2.start();
            t3.start();
            t4.start();
            t5.start();
            t6.start();
            t7.start();
            t8.start();
            t9.start();
        }
    }

    private static class GameLevel implements ActionListener {
        public int decodeLevel (String input) {
            int ret = 3;
            if (input.equals("One")) {
                ret = 1;
            }
            else if (input.equals("Two")) {
                ret = 2;
            }
            else if (input.equals("Three")) {
                ret = 3;
            }
            else if (input.equals("Four")) {
                ret = 4;
            }
            else if (input.equals("Five")) {
                ret = 5;
            }
            else if (input.equals("Six")) {
                ret = 6;
            }
            else if (input.equals("Seven")) {
                ret = 7;
            }
            else if (input.equals("Eight")) {
                ret = 8;
            }
            else if (input.equals("Nine")) {
                ret = 9;
            }
            else if (input.equals("Ten")) {
                ret = 10;
            }
            return ret;
        }
        public void actionPerformed (ActionEvent e) {
            JComboBox cb = (JComboBox) e.getSource();
            String textLevel = (String) cb.getSelectedItem();
            level = decodeLevel(textLevel);
        }
    }

}
