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
public class MachineGunBullet extends Bullet {

    private static BufferedImage bulletImage = null;
    private static final int IMPACT_RADIUS = 0;
    private static final int DAMAGE = 10;
    private static final int BULLET_SPEED = 7; //tick is called 5 times. So actual speed is 50
    private static final boolean AERIAL = false;
    public static final int BULLET_SIZE = 3;
    private static final Color BULLET_COLOR = Color.RED;

    public MachineGunBullet(Point current_coordinates, int angle_of_motion, int distance) {
        super(current_coordinates, 360 - angle_of_motion, Bullet.INFINITE_DISTANCE);
        if (bulletImage == null) {
            try {
                bulletImage = ImageIO.read(this.getClass().getResource("/images/bullet.bmp"));
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
        //g.setColor(BULLET_COLOR);
        AffineTransform af = new AffineTransform();
        af.translate(pt.x, pt.y);

        af.rotate(Math.toRadians(angle_of_motion));
        g.drawImage(bulletImage, af, null);
    //g.fillOval(startX + pt.x - BULLET_SIZE, startY + pt.y - BULLET_SIZE, BULLET_SIZE * 2, BULLET_SIZE * 2);
    }

    @Override
    public boolean validateBullet() {
        return false;
    }

    @Override
    public int getBulletSize() {
        return BULLET_SIZE;
    }
}
