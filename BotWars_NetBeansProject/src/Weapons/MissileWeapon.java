package Weapons;


import botwars.Bullet;
import Bullets.MissileBullet;
import java.awt.Point;

/**
 *
 * @author izaaz
 */
public class MissileWeapon extends Weapon {

    static final int MISSILE_AMMO = 50;
    static final int MISSILE_MAX_DIST = 500;
    static final int MISSILE_RELOAD_TIME = 15;
    
    public MissileWeapon() {
        super(MISSILE_AMMO, MISSILE_MAX_DIST, MISSILE_RELOAD_TIME);
    }

    public Bullet fire(Point initial_coordinates, int angle, int distance) {
        if (ammo == 0) {
            return null;
        } else {
            ammo--;
            return new MissileBullet(initial_coordinates, angle, distance);
        }
    }
}
