package botwars;

import Weapons.MachineGunWeapon;
import Weapons.MissileWeapon;
import Weapons.Weapon;
import java.awt.BasicStroke;
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
public abstract class Bot extends Thread {

    boolean finished = false;
    String botName;
    BufferedImage botImage;
    BufferedImage nozzleImage;
    private boolean stationary = true;
    private Color color;
    public static final int BOT_SIZE = 20;
    private Point currentCord;
    private final int BOT_SPEED = 5;
    final static int NOZZLE_SIZE = BOT_SIZE;
    private final static int NOZZLE_THICKNESS = 5;
    private final static Color NOZZLE_COLOR = Color.MAGENTA;
    private int armour = 100;
    Graphics2D botImageGraphics;
    /**
     * angle of -1 indicates stationary bot
     */
    private int angle = 0;
    /**
     * once a bot fires a bullet from a weapon, the bot's recoil time is set
     * to the weapon's reloat time. Only if recoil time is 0 can the bot fire again
     */
    private int recoilTime = 0;
    private int currentNozzleDirection;
    private Radar radar = new Radar(this);
    private Weapon[] weapons = {new MissileWeapon(), new MachineGunWeapon()};

    final void setColor(Color c) {
        this.color = c;
    }

    final void drawRadar(int startX, int startY, Graphics2D g) {
        radar.paint(startX, startY, g);
    }

    final void paint(int startX, int startY, Graphics2D g) {
        Color currentColor = g.getColor();
        g.setColor(color);
        //g.fillOval(startX + currentCord.x - BOT_SIZE, startY + currentCord.y - BOT_SIZE, BOT_SIZE * 2, BOT_SIZE * 2);
        //g.rotate(angle);
        AffineTransform af = new AffineTransform();
        af.translate(currentCord.x - BOT_SIZE, currentCord.y - BOT_SIZE);
        af.rotate(Math.toRadians(angle), BOT_SIZE, BOT_SIZE);
        g.drawImage(botImage, af, null);
        //g.rotate(-angle);
        drawNozzle(g, startX, startY);
        g.setColor(currentColor);
        g.setColor(Color.CYAN);

    }

    final void drawNozzle(Graphics2D g, int startX, int startY) {
        g.setStroke(new BasicStroke(NOZZLE_THICKNESS));
        Point end_pt = Utils.getNewPoint(currentCord, currentNozzleDirection, NOZZLE_SIZE);
        g.setColor(NOZZLE_COLOR);
        AffineTransform af = new AffineTransform();
        af.translate(currentCord.x, currentCord.y);
        af.rotate(Math.toRadians(currentNozzleDirection));
        g.drawImage(nozzleImage, af, null);
    }

    final String getBotName() {
        if (botName == null || botName.isEmpty()) {
            return "Nameless Bot";
        }
        return botName;
    }

    /**
     * Constructor
     * @param botName Sets your botName
     */
    public Bot(String botName) {
        this.botName = botName;
    }

    /**
     * Checks the current state of your bot
     * @return true - if the bot is stationary
     * <br/>fasle - if the bot is moving
     */
    public final boolean isStationary() {
        return stationary;
    }

    /**
     * The current direction the bot is moving in.
     * @return a value between 0 and 359
     */
    public final int getCurrentDirection() {
        return (360 - angle) % 360;
    }

    final void setBotImage(String imageName) {
        try {

            botImage = ImageIO.read(this.getClass().getResource("/images/" + imageName + ".bmp"));
            nozzleImage = ImageIO.read(this.getClass().getResource("/images/nozzle.bmp"));
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

    }

    final void tick() {
        //move current coords
        moveBot();
        //decrement recoil time
        if (recoilTime != 0) {
            recoilTime--;
        }
    }

    final private void moveBot() {
        if (stationary) {
            return;
        }
        currentCord = Utils.getNewPoint(currentCord, angle, BOT_SPEED);

        Point newCoordinates = Arena.getInstance().checkIfPointInsideArena(currentCord, BOT_SIZE);
        if (newCoordinates == null) {
            return;
        } else {
            currentCord = newCoordinates;
            stationary = true;
        }

    }

    /**
     * Total number of ticks available for the current match
     * @return
     */
    final public int getTotalTicks() {
        return Arena.maxTicks;
    }

    /**
     * Ticks remaining for the match to get over. Its the total_ticks - ticks_elappsed
     * @return
     */
    final public int getTicksRemaining() {
        return Arena.maxTicks - Arena.ticks;
    }

    @Override
    public final void run() {
        try {
            BotLogic();
        } catch (InterruptedException e) {
            System.out.println("Bot Interrupted");
            finished = true;
        }
    }

    abstract public void BotLogic() throws InterruptedException;

    /**
     * Makes the bot to stop in its current position
     */
    final public void stopBot() {
        stationary = true;
    }

    synchronized final Integer scanRadar(int startAngle, int endEngle, int distance) throws InterruptedException {
        if (!stationary) {
            return -1;
        }

        Integer dt = radar.scanRadar(startAngle, endEngle, distance);
        radar.setRadarInUse(true);
        sleep(Radar.RADAR_WAITING_TIME * Arena.TIMEOUT);
        radar.setRadarInUse(false);
        return dt;
    }

    /**
     * Scan for the presence of a bot between startAngle and endAngle. 
     * The bot has to be stationary when scanning. 
     * Note - angle moves in counter clockwise direction. Calling a scan radar induces
     * a delay of half a secondi seconds when the bot cannot perform any action.
     * @param startAngle start angle
     * @param endAngle end angle
     * @return distance between the bots if present else returns -1
     */
    synchronized public final int scanRadar(int startAngle, int endAngle) throws InterruptedException {
        if ((endAngle - startAngle + 360) % 360 > 90) {
            return -1;
        }
        return scanRadar(startAngle, endAngle, Radar.RADAR_INFINITE_DISTANCE);

    }

    /**
     * moves the bot in a specific angle until a stop function is called
     * @param angle direction of movement
     */
    synchronized public final void moveBot(int angle) {
        stationary = false;
        this.angle = 360 - angle;
    }

    /**
     * fires a missile
     * @param angle sets the nozzle in the direction and the missile is fired
     * in this angle
     * @param distance target distance from the current bot position
     */
    synchronized public final boolean fireMissile(int fireAngle, int distance) {
        if (recoilTime != 0) {
            return false;
        }
        currentNozzleDirection = 360 - fireAngle;
        Bullet newBullet = weapons[0].fire(Utils.getNewPoint(currentCord, currentNozzleDirection, NOZZLE_SIZE), fireAngle, distance);
        //Bullet newBullet = weapons[0].fire(currentCord, fireAngle, distance);
        if (newBullet == null) {
            //out of ammo
            return false;
        }
        recoilTime = weapons[0].reload_time;
        Arena.getInstance().addBullet(newBullet);
        return true;
    }

    final void reduceArmour(int impact) {
        armour -= impact;
        if (armour < 0) {
            armour = 0;
        }
    }

    /**
     * Fires a bullet in the angle specified by fireAngle
     * @param fireAngle - angle of fire (0 - 359)
     * @return
     */
    synchronized public final boolean fireBullet(int fireAngle) {
        if (recoilTime != 0) {
            return false;
        }
        currentNozzleDirection = 360 - fireAngle;
        Bullet newBullet = weapons[1].fire(Utils.getNewPoint(currentCord, currentNozzleDirection, Bot.BOT_SIZE + 10), fireAngle, -1);
        //Bullet newBullet = weapons[1].fire(currentCord, fireAngle, -1);
        recoilTime = weapons[1].reload_time;
        Arena.getInstance().addBullet(newBullet);
        return true;
    }

    final void setArmour(int armour) {
        this.armour = armour;
    }

    final void setCurrentCord(Point currentCord) {
        this.currentCord = currentCord;
    }

    final void setCurrentNozzleDirection(int currentNozzleDirection) {
        this.currentNozzleDirection = currentNozzleDirection;
    }

    final void setDirection(int direction) {
        this.angle = direction;
    }

    final void setRecoilTime(int recoilTime) {
        this.recoilTime = recoilTime;
    }

    final public Point getPosition() {
        return currentCord;
    }

    /**
     * Armour of your bot
     * @return armour
     */
    public final int getArmour() {
        return armour;
    }

    /**
     * get armour of the opponent bot
     * @return opponent bot's armour
     */
    public final int getOpponentArmour() {
        return Arena.getInstance().getOpponentArmour(this);
    }

    final int getCurrentNozzleDirection() {
        return currentNozzleDirection;
    }

    final int getDirection() {
        return angle;
    }

    /**
     * This method returns the amount of time left before the weapon can be fired again.
     * If this value is 0, it means that the next weapon can be fired.
     * @return
     */
    final public int getRecoilTime() {
        return recoilTime;
    }

    /**
     * Ammo left for the Missile weapon
     * @return
     */
    public final int getMissileAmmo() {
        return weapons[0].ammo;
    }

    @Override
    final public String toString() {
        StringBuilder returnString = new StringBuilder("[");
        returnString.append(botName + ",");
        returnString.append(armour + ",");
        returnString.append(360 - angle + ",");
        returnString.append(360 - currentNozzleDirection + ",");
        returnString.append(currentCord.x + ",");
        returnString.append(currentCord.y + ",");
        if (radar.inUse()) {
            returnString.append(radar.getStartAngle() + ",");
            returnString.append(radar.getEndAngle());
        } else {
            returnString.append("-1,-1");
        }
        returnString.append("]");
        return returnString.toString();
    }
}

class Radar {

    private final static Color RADAR_COLOR = new Color(150, 255, 150);
    private final static Color RESULT_POINT_COLOR = new Color(0, 0, 0);
    private final static int RESULT_POINT_SIZE = 7;
    private Bot bot;
    private int startAngle;
    private int endAngle;
    private int distance;
    static final int RADAR_WAITING_TIME = 5;
    static final int RADAR_INFINITE_DISTANCE = -1;
    private boolean radarInUse = false;
    private Point resultPoint = null;

    public Radar(Bot bot) {
        this.bot = bot;
    }

    final int getStartAngle() {
        return startAngle;
    }

    final int getEndAngle() {
        return endAngle;
    }

    final boolean inUse() {
        return radarInUse;
    }

    final int scanRadar(int startAngle, int endAngle, int distance) {
        this.startAngle = startAngle;
        this.endAngle = endAngle;
        this.distance = 1000;
        Point center = bot.getPosition();
        Point pt1 = Arena.getInstance().getOpponentBotCoordinates(bot);
        int angle = Utils.angleBetweenTwoPoints(center, pt1);
        int distanceBetweenBots = (int) Utils.distance(center, pt1);
        int theta1 = (endAngle - startAngle + 360) % 360;
        int theta2 = (angle - startAngle + 360) % 360;
        int theta3 = (endAngle - angle + 360) % 360;
        if (theta2 <= theta1 && theta3 <= theta1) {
            if (distance == Radar.RADAR_INFINITE_DISTANCE || distance >= distanceBetweenBots) {
//                this.distance = distanceBetweenBots;
                return distanceBetweenBots;
            } else {
                return -1;
            }
        }



        return -1;
    }

    final void paint(int startx, int starty, Graphics2D g) {
        if (radarInUse) {
//            Currently no result Point as distance will be returned
//            if (resultPoint != null && (resultPoint.x != -1 || resultPoint.y != -1)) {
//                g.setColor(RESULT_POINT_COLOR);
//                g.fillOval(startx + resultPoint.x, starty + resultPoint.y, RESULT_POINT_SIZE, RESULT_POINT_SIZE);
//            }
            g.setColor(Radar.RADAR_COLOR);
            Point bot_coordinates = new Point(bot.getPosition());
            bot_coordinates.x += startx;
            bot_coordinates.y += starty;
            g.fillArc(bot_coordinates.x - distance, bot_coordinates.y - distance, distance * 2, distance * 2, startAngle, (endAngle - startAngle + 360) % 360);
        }
    }

    final void setRadarInUse(boolean radar_in_use) {
        this.radarInUse = radar_in_use;
    }
}
