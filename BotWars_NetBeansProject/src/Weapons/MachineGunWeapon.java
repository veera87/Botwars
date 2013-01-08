package Weapons;


import botwars.Bullet;
import Bullets.MachineGunBullet;
import java.awt.Point;

/**
 *
 * @author izaaz
 */
public class MachineGunWeapon extends Weapon {

    static final int MG_AMMO = Weapon.INFINITE_AMMO;
    static final int MG_MAX_DIST = Bullet.INFINITE_DISTANCE;
    static final int MG_RELOAD_TIME = 5;
    
    public MachineGunWeapon() {
        super(MG_AMMO, MG_MAX_DIST, MG_RELOAD_TIME);
    }
    
    public Bullet fire(Point initial_coordinates, int angle, int distance) {
        return new MachineGunBullet(initial_coordinates, angle, distance);
    }
}
