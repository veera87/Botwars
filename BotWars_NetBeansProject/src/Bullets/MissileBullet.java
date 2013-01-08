package Bullets;

import botwars.Bullet;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

/**
 *
 * @author izaaz
 */
public class MissileBullet extends Bullet {

    private static BufferedImage missileImage = null;
    private static final int IMPACT_RADIUS = 30;
    private static final int DAMAGE = 20;
    private static final int BULLET_SPEED = 10;
    private static final boolean AERIAL = true;
    public static final int BULLET_SIZE = 5;
    private Color BULLET_COLOR = Color.YELLOW;

    public MissileBullet(Point current_coordinates, int angle_of_motion, int distance_left) {
        super(current_coordinates, 360 - angle_of_motion, distance_left);
        if (missileImage == null) {
            try {
                missileImage = ImageIO.read(this.getClass().getResource("/images/missile.bmp"));
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
    }

    @Override
    public int initImpactRadius() {
        return IMPACT_RADIUS;
    }

    @Override
    public int initDamage() {
        return DAMAGE;
    }

    @Override
    public int initBulletSpeed() {
        return BULLET_SPEED;
    }

    @Override
    public boolean initAerial() {
        return AERIAL;
    }

    @Override
    public void paint(Graphics2D g, int startX, int startY) {
        Point pt = new Point(currentCord);
        AffineTransform af = new AffineTransform();
        af.translate(pt.x, pt.y);
        af.rotate(Math.toRadians(angle_of_motion));
        g.drawImage(missileImage, af, null);
//        g.setColor(BULLET_COLOR);
//        g.fillOval(startX + pt.x - BULLET_SIZE, startY + pt.y - BULLET_SIZE, BULLET_SIZE * 2, BULLET_SIZE * 2);
    }

    @Override
    public boolean validateBullet() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getBulletSize() {
        return BULLET_SIZE;
    }

}
