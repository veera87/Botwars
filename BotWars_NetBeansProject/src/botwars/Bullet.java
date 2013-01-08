package botwars;

import Bullets.MachineGunBullet;
import botwars.Utils;
import java.awt.Graphics2D;
import java.awt.Point;

/**
 * This class defines an abstract Bullet.
 * Any new type of bullet that needs to be incorporated into the game should be a subclass of Bullet
 * 
 * @author izaaz
 */
public abstract class Bullet {

    public static final int INFINITE_DISTANCE = -1;
    public Point currentCord;
    public int angle_of_motion,  distanceLeft;
    final int impactRadius = initImpactRadius();
    protected boolean collided = false;
    final int damage = initDamage();
    /**
     * number of units per tick. speed value of 1 is slower than speed value of 10
     */
    final int bullet_speed = initBulletSpeed();
    final boolean aerial = initAerial();

    /**
     * 
     * @return false if bullet is no longer valid
     */
    boolean tick() {

        currentCord = Utils.getNewPoint(currentCord, angle_of_motion, bullet_speed);

        //reduce distance left
        if (distanceLeft != Bullet.INFINITE_DISTANCE) {
            distanceLeft -= bullet_speed;
            if (distanceLeft < 0) {
                distanceLeft = 0;
            }
        }

        //check for bullet outside bounds
        if (Arena.getInstance().checkIfPointInsideArena(currentCord, getBulletSize()) != null) {
            return false;
        }
        return true;
    }

    int getDistanceLeft() {
        return distanceLeft;
    }

    public abstract boolean validateBullet();

    public abstract int getBulletSize();

    int calculateImpact(Point centre, int radius) {
        this.collided = true;
        double dist = Utils.distance(centre, currentCord);
        //dist -= (radius);
        dist -= (radius + this.getBulletSize());
        if (dist <= impactRadius) {
            if (dist <= 0) {
                //full damage
                return damage;
            } else {
                //calculate normalized impact
                return (int) Math.ceil(((impactRadius - dist) / (impactRadius)) * damage);
            }
        }
        return 0;
    }

    public Point getCurrentCordinates() {
        return currentCord;
    }

    public abstract void paint(Graphics2D g, int startX, int startY);

    /**
     * This method should be implemented in the subclass of Bullet that initializes the impact radius of the bullet
     * 
     * @return impact radius value as int
     */
    abstract public int initImpactRadius();

    /**
     * This method should be implemented in the subclass of Bullet that initializes the damage of the bullet
     * 
     * @return damage value as int
     */
    abstract public int initDamage();

    /**
     * This method should be implemented in the subclass of Bullet that initializes the speed of the bullet
     * 
     * @return speed value as int
     */
    abstract public int initBulletSpeed();

    /**
     * This method should be implemented in the subclass of Bullet that initializes whether the bullet is aerial
     * 
     * @return aerial value as boolean
     */
    abstract public boolean initAerial();

    public Bullet(Point current_coordinates, int angle_of_motion, int distanceLeft) {

        this.currentCord = current_coordinates;
        this.angle_of_motion = angle_of_motion;
        this.distanceLeft = distanceLeft;
    }

    @Override
    public final String toString() {
        StringBuilder returnString = new StringBuilder("[");
        returnString.append(currentCord.x + ",");
        returnString.append(currentCord.y + ",");
        returnString.append(angle_of_motion + ",");
        returnString.append(collided ? impactRadius : "0");
        returnString.append("]");
        return returnString.toString();
    }
}