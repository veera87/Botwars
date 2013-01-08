package Weapons;


import botwars.Bullet;
import java.awt.Point;

/**
 * This class defines an abstract class Weapon.
 * A new type of weapon to be added in the game must be a subclass of Weapon 
 * 
 * @author izaaz
 */
public abstract class Weapon {
    public int ammo, max_distance, reload_time;    
    final static int INFINITE_AMMO = -1;

    public Weapon(int ammo, int max_distance, int reload_time) {
        this.ammo = ammo;
        this.max_distance = max_distance;
        this.reload_time = reload_time;
    }    
    
    public abstract Bullet fire(Point initialCoordinates, int angle, int distance);
}