/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package botwars;

import Bullets.MachineGunBullet;
import Bullets.MissileBullet;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.Timer;
import javax.swing.JFrame;

/**
 *
 * @author izaaz
 */
public class Arena extends JFrame {

    private static String BASE_DIR = "E:\\univ appln\\Final Docs\\portfolio\\Botwars\\netbeans\\output";
    private static String OUTPUT_FILE = BASE_DIR + "botwarsOutput.log";
    private static String BOT1_PATH = "Bots.Level1";
    private static String BOT2_PATH = "Bots.Level2";
    public static final String GAME_OVER_FONT_NAME = "ARIAL";
    private static final int GAME_OVER_FONT_SIZE = 30;
    private static final String MATCH_DRAW_MESSAGE = "MATCH DRAW";
    private static final String MATCH_WINNER_MESSAGE = " WINS";
    private static Arena arenaInstance;
    private final int topX = 20;
    private final int topY = 100;
    static final int width = 800;
    static final int height = 500;
    private final int windowWidth = width + topX * 2;
    private final int windowHeight = height + topY * 2;
    private String gameOverMessage = null;
    static int NUM_OF_MINS = 3;
    Rectangle bounds = new Rectangle(topX, topY, width, height);
    static final int TIMEOUT = 100;
    ArrayList<Bot> bots = new ArrayList<Bot>();
    static int ticks;
    static private final int TICKS_PER_SECOND = (1000 / TIMEOUT);
    static final int maxTicks = TICKS_PER_SECOND * 60 * NUM_OF_MINS;
    ArrayList<Bullet> bullets = new ArrayList<Bullet>();
    TickTimerTask tickTask;
    Timer timer = new Timer();
    ArmourCanvas armourCanvas = new ArmourCanvas();
    ArenaCanvas arenaCanvas = new ArenaCanvas();
    static BotWarLogger logger;
    private int BORDER_THICKNESS = 3;

    /**
     * returns the armour of the opponent bot
     * @param bot current Bot
     * @return armour of the opponent
     */
    int getOpponentArmour(Bot bot) {
        for (Bot bt : bots) {
            if (bt.equals(bot)) {
                continue;
            }
            return bt.getArmour();
        }
        return 0;
    }

    private boolean checkForBotCollision() {
        Point pt1 = bots.get(0).getPosition();
        Point pt2 = bots.get(1).getPosition();
        double distance = Utils.distance(pt1, pt2);
        if (distance <= 2 * Bot.BOT_SIZE + 8) {
            return true;
        }
        return false;
    }

    class ArmourCanvas extends Canvas {

        Font ArmourFont = new Font(GAME_OVER_FONT_NAME, Font.BOLD, 15);
        Font TimeRemainingFont = new Font(GAME_OVER_FONT_NAME, Font.ITALIC, 16);

        public ArmourCanvas() {
            //this.setBackground(Color.BLACK);
            this.setBounds(0, 0, windowWidth, topY - 1);
            this.setVisible(true);
        }

        @Override
        public void paint(Graphics g) {
            g.setColor(Color.BLACK);
            g.setFont(ArmourFont);
            displayArmour(g);
            displayTimer(g);
        }

        void displayArmour(Graphics g) {
            int armour1 = bots.get(0).getArmour();
            int armour2 = bots.get(1).getArmour();


            String bot1Armour = bots.get(0).getBotName() + " : " + armour1;
            String bot2Armour = bots.get(1).getBotName() + " : " + armour2;

            g.drawString(bot1Armour, 10, getHeight() - 25);
            int strLen = g.getFontMetrics().stringWidth(bot2Armour);
            g.drawString(bot2Armour, getWidth() - strLen - 10, getHeight() - 25);
        }

        private void displayTimer(Graphics g) {

            int remTicks = maxTicks - ticks;
            int mins = remTicks / (TICKS_PER_SECOND * 60);
            int secs = (remTicks / (TICKS_PER_SECOND)) % 60;

            String timeRemaining = "Ticks Remaining : " + (mins < 10 ? "0" + mins : mins) + ":" + (secs < 10 ? "0" + secs : secs);
            int strLen = g.getFontMetrics().stringWidth(timeRemaining);
            g.drawString(timeRemaining, (getWidth() - strLen) / 2, getHeight() - 50);
        //g.drawString("5", 160,50);
        }
    }

    class ArenaCanvas extends Canvas {

        public ArenaCanvas() {
            this.setBackground(Color.LIGHT_GRAY);
            this.setBounds(topX, topY, width, height);
            this.setVisible(true);
        }

        @Override
        public void paint(Graphics g1) {
            Graphics2D g = (Graphics2D) g1;
            g.drawRect(0, 0, width - 1, height - 1);
            g.drawRect(1, 1, width - 3, height - 3);
            drawRadar(g);
            drawBots(g);
            drawBullets(g);

            if (gameOverMessage != null) {
                displayGameOver(g);
            }
        }

        private void drawBullets(Graphics2D g) {
            for (Bullet bullet : bullets) {
                bullet.paint(g, 0, 0);
            }
        }

        private void displayGameOver(Graphics2D g) {
            g.setColor(Color.BLACK);
            Font font = new Font(GAME_OVER_FONT_NAME, Font.BOLD, GAME_OVER_FONT_SIZE);
            g.setFont(font);
            int length = g.getFontMetrics(font).stringWidth(gameOverMessage);
            int displayX = (width - length) / 2;
            int displayY = height / 2;

            g.drawString(gameOverMessage, displayX, displayY);

        }

        void drawBots(Graphics2D g) {
            for (Bot bot : bots) {
                bot.paint(0, 0, g);
            }
        }

        private void drawRadar(Graphics2D g) {
            for (Bot bot : bots) {
                bot.drawRadar(0, 0, g);
            }
        }
    }

    synchronized private void createTimer() {
        tickTask = new TickTimerTask();
        timer.schedule(tickTask, new Date(), TIMEOUT);

    }

    private Arena() {
        try {
            if (OUTPUT_FILE == null || OUTPUT_FILE.isEmpty()) {
                logger = new BotWarLogger();
                logger.disableLogger();
            } else {
                logger = new BotWarLogger(OUTPUT_FILE);
            }
        } catch (Exception e) {
            logger.disableLogger();
            e.printStackTrace();
            System.err.println("Logger has not been initialized");
            System.exit(1);
        }

        Bot bot1 = null, bot2 = null;
        try {
            bot1 = (Bot) Class.forName(BOT1_PATH).newInstance();
            Random rand = new Random(new Date().getTime() % 100);
            bot1.setCurrentCord(new Point(30 + rand.nextInt(width / 2 - 60), 30 + rand.nextInt(height - 50)));
            //bot1.setCurrentCord(new Point(100, 100));
            bot1.setBotImage("tank1");
            bot2 = (Bot) Class.forName(BOT2_PATH).newInstance();
            bot2.setCurrentCord(new Point(30 + width / 2 + rand.nextInt(width / 2 - 50), 30 + rand.nextInt(height - 50)));
            bot2.setBotImage("tank2");
        //bot2.setCurrentCord(new Point(300, 300));
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Unable to create bots" + e);
            System.exit(1);
        }

        setLayout(null);
        this.add(armourCanvas);
        this.add(arenaCanvas);
        bots.add(bot1);
        bots.add(bot2);
        initializeUI();
        logger.writeFrame(this.toString());
        bot1.start();
        bot2.start();


    }

    /**
     * 
     * @param pt
     * @return returns null if point is inside arena. If point is outside arena,
     * it returns a point on the bounds
     */
    Point checkIfPointInsideArena(Point pt, int size) {
        int x = pt.x;
        int y = pt.y;

        boolean outsideBounds = false;
        if (x < size) {
            outsideBounds = true;
            x = size;
        }

        if (x + size > width) {
            outsideBounds = true;
            x = width - size;
        }

        if (y < size) {
            outsideBounds = true;
            y = size;
        }

        if (y + size > height) {
            outsideBounds = true;
            y = height - size;
        }

        if (outsideBounds) {
            return new Point(x, y);
        }

        return null;
    }

    synchronized static Arena getInstance() {
        if (arenaInstance == null) {
            arenaInstance = new Arena();
            arenaInstance.createTimer();
        }

        return arenaInstance;
    }

    synchronized public void addBullet(Bullet bullet) {
        bullets.add(bullet);
    }

    /**
     * returns the opponent bot's coordinates (Assuming only 2 bots are available)
     * @param bot current Bot
     * @return
     */
    Point getOpponentBotCoordinates(Bot bot) {
        for (Bot bt : bots) {
            if (bt.equals(bot)) {
                continue;
            }

            return bt.getPosition();
        }
//should never come here
        return null;
    }

    synchronized public void tick() {
        //all tick manipulations
        //tick for al the bots
        this.repaint(topX, topY, width, height);
        boolean botCollided = checkForBotCollision();
        if (botCollided) {
            bots.get(0).stopBot();
            bots.get(1).stopBot();
        }

        for (Bot bot : bots) {
            bot.tick();
        }
        ArrayList<Bullet> invalidBullets = new ArrayList<Bullet>();
        //bullet movements
        for (Bullet bullet : bullets) {
            //move the bullets
            if (bullet.getDistanceLeft() == Bullet.INFINITE_DISTANCE) {
                for (int i = 0; i < 5; i++) {
                    boolean valid = bullet.tick();
                    if (!valid) {
                        invalidBullets.add(bullet);
                        break;
                    }
                    //check for impact of bullet
                    boolean collided = checkForBulletCollision(bullet);
                    if (collided) {
                        invalidBullets.add(bullet);
                        break;
                    }

                }
            } else {
                boolean valid = bullet.tick();
                if (!valid) {
                    invalidBullets.add(bullet);
                    break;
                }
                //check for impact of bullet
                int distLeft = bullet.getDistanceLeft();

                if (distLeft == 0) {
                    updateBotWithImpact(bullet);
                    invalidBullets.add(bullet);
                    break;
                }
            }

        }


        ticks++;

        if (isGameOver()) {
            //stop the game
            tickTask.cancel();
            for (Bot bot : bots) {
                bot.interrupt();
            }

            int maxArmour = Integer.MIN_VALUE;
            for (Bot bot : bots) {
                int armour = bot.getArmour();
                if (armour > maxArmour) {
                    maxArmour = armour;
                }

            }

            ArrayList<Bot> winners = new ArrayList<Bot>();
            for (Bot bot : bots) {
                if (bot.getArmour() == maxArmour) {
                    winners.add(bot);
                }

            }

            if (winners.size() > 1) {
                gameOverMessage = MATCH_DRAW_MESSAGE;
            } else {
                gameOverMessage = winners.get(0).getBotName() + MATCH_WINNER_MESSAGE;
            }
            logger.writeFrame(currentFrame());
            logger.closeLogger();

            if (OUTPUT_FILE != null) {
                System.exit(0);
            }
        }
        logger.writeFrame(currentFrame());
        //writing to the file

        bullets.removeAll(invalidBullets);
    }

    private String currentFrame() {
        StringBuilder returnString = new StringBuilder("(");
        returnString.append(bots.get(0).toString() + ",");
        returnString.append(bots.get(1).toString() + ",");

        //Writing Missile Bullets
        returnString.append("{");
        for (Bullet bullet : bullets) {
            if (bullet instanceof MissileBullet) {
                returnString.append(bullet.toString() + ",");
            }

        }
        if (returnString.charAt(returnString.length() - 1) == ',') {
            returnString.deleteCharAt(returnString.length() - 1);
        }

        returnString.append("},");

        //writing MachineGun Bullets
        returnString.append("{");
        for (Bullet bullet : bullets) {
            if (bullet instanceof MachineGunBullet) {
                returnString.append(bullet.toString() + ",");
            }

        }
        if (returnString.charAt(returnString.length() - 1) == ',') {
            returnString.deleteCharAt(returnString.length() - 1);
        }

        returnString.append("}");

        returnString.append(")");
        return returnString.toString();

    }

    @Override
    public void paint(Graphics g1) {
        super.paint(g1);
        armourCanvas.repaint();
        arenaCanvas.repaint();
        Graphics2D g = (Graphics2D) g1;
    //g.setColor(Color.WHITE);
    //g.fillRect(topX, topY, width, height);

    }

    private boolean checkForBulletCollision(Bullet bullet) {
        boolean ret = false;
        for (Bot bot : bots) {
            if (Utils.hasCollided(bot.getPosition(), bullet.getCurrentCordinates(), Bot.BOT_SIZE, bullet.getBulletSize())) {
                //reduce bot health
                int damage = bullet.calculateImpact(bot.getPosition(), Bot.BOT_SIZE);
                bot.reduceArmour(damage);
                ret = true;
            }

        }
        return ret;
    }

    /**
     * calculates the impact of the bullet with any of the bots and updates
     * the armour of the bot with respect to the impact. This method is called
     * when aerial bullets explode
     * @param bullet
     */
    public void updateBotWithImpact(Bullet bullet) {
        for (Bot bot : bots) {
            int damage = bullet.calculateImpact(bot.getPosition(), bot.BOT_SIZE);
            bot.reduceArmour(damage);
        }

    }

    private boolean isGameOver() {
        for (Bot bot : bots) {
            if (bot.getArmour() == 0) {
                return true;
            }

        }
        boolean finished = false;
        if (ticks == maxTicks) {
            return true;
        }
        for (Bot bot : bots) {
            finished = finished && bot.finished;
        }

        return finished;
    }

    private void initializeUI() {
        this.setTitle("BotWar");
        this.setBounds(0, 0, windowWidth, windowHeight);
        this.setBackground(Color.BLACK);
        if (OUTPUT_FILE == null) {
            this.setVisible(true);
        }
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

    }

    @Override
    public String toString() {
        StringBuilder returnString = new StringBuilder("[");
        returnString.append(bots.get(0).getBotName() + ",");
        returnString.append(bots.get(1).getBotName() + ",");
        returnString.append(Bot.BOT_SIZE + ",");
        returnString.append(MissileBullet.BULLET_SIZE + ",");
        returnString.append(MachineGunBullet.BULLET_SIZE + ",");
        returnString.append(width + ",");
        returnString.append(height + ",");
        returnString.append(maxTicks);
        returnString.append("]");
        return returnString.toString();
    }

    /**
     * Starts a match between the two bots
     * @param botPath1 - Path of the bot1 - Provided as a string like "MyPackage.MyBotName"
     * @param botPath2 - Path of the bot2 - Similar to the previous parameter
     */
    public static void playGame(String botPath1, String botPath2) {
        BOT1_PATH = botPath2;
        BOT2_PATH = botPath1;
        OUTPUT_FILE = null;
        Arena.getInstance();

    }

    /**
     * Starts a match between the two bots
     * @param botPath1 - Path of the bot1 - Provided as a string like "MyPackage.MyBotName"
     * @param level - Difficulty of the opponent bot - takes values from 1 to 3
     */
    public static void playGame(String botPath1, int level) {
        BOT1_PATH = botPath1;
        switch (level) {
            case 1:
                BOT2_PATH = "Bots.Level1";
                break;
            case 2:
                BOT2_PATH = "Bots.Level2";
                break;
            case 3:
                BOT2_PATH = "Bots.Level3";
                break;
            default:
                System.err.println("Invalid value for difficulty");
                return;
        }
        OUTPUT_FILE = null;
        Arena.getInstance();
    }

    public static void main(String[] args) {

        ArgumentsParser.parseArguments(args);

        String bot1 = System.getProperty("bot1");
        String bot2 = System.getProperty("bot2");
        Integer minsParam = null;
        String mode = System.getProperty("mode");
        String outputFile = System.getProperty("outputFile");


        try {
            minsParam = Integer.parseInt(System.getProperty("mins"));
        } catch (Exception e) {
            System.err.println("Invalid value for minutes");
            System.exit(1);
        }

        if (minsParam == null) {
            System.err.println("argument maxTicks is missing");
            System.exit(1);
        }

        if (bot1 == null || bot2 == null) {
            System.err.println("parameters bot1 and bot2 must be specified");
            System.exit(1);
        }

        if (mode == null) {
            System.err.println("Parameter mode missing. Takes two values - file or applet");
            System.exit(1);
        }

        if (mode.equals("file")) {
            if (outputFile == null) {
                System.err.println("Parameter outputFile missing");
            } else {
                OUTPUT_FILE = outputFile;
            }

        } else if (mode.equals("applet")) {
            OUTPUT_FILE = null;
        }

        NUM_OF_MINS = minsParam;
        BOT1_PATH = bot1;
        BOT2_PATH = bot2;
        Arena.getInstance();

    }
}
