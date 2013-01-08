package Bots;

@SuppressWarnings("unused")
public class Level2 extends botwars.Bot {

    class checkhealth implements Runnable {

        public void run() {
            int health;
            int prevhealth = Level2.this.getArmour();
            int[] p = new int[4];
            try {
                while (true) {
                    health = Level2.this.getArmour();
                    if (health != prevhealth) {
                        /*
                        p=doscan(0, 89, 90);
                        if(p[3]>0 && p[3]<=90)
                        BotA.this.moveBot(180);
                        else if(p[3]>90 && p[3]<=180)
                        BotA.this.moveBot(0);
                        else if(p[3]>180 && p[3]<=270)
                        BotA.this.moveBot(90);
                        else
                        BotA.this.moveBot(270);
                        //BotA.this.moveBot(p[3]);*/
                        if (Level2.this.isStationary()) {
                            Level2.this.moveBot(((int) (Math.random() * 100000)) % 360);
                            Level2.sleep(3000);
                        }
                    } else {
                        if (!Level2.this.isStationary()) {
                            Level2.this.stopBot();
                        }
                    }
                    prevhealth = health;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @param args
     */
    public Level2() {
        super("Level 2 Bot");
    }

    public int[] doscan(int sta, int end, int factor) throws InterruptedException {
        int x = 0, cnt = 0;
        while (true) {
            x = this.scanRadar(sta, end);
            if (x != -1) {
                if (end - sta < 20) {
                    break;
                } else {
                    factor /= 2;
                    end = sta + factor;
                }
            } else {
                sta = end;
                end = sta + factor;
            }
            if (++cnt == 12) {
                sta = 0;
                end = 89;
                factor = 90;
            }
        }
        int[] p = new int[4];
        p[0] = sta;
        p[1] = end;
        p[2] = factor;
        p[3] = x;
        return p;
    }

    public void BotLogic() throws InterruptedException {
        checkhealth c = new checkhealth();
        Thread thread1 = new Thread(c);
        thread1.start();
        int x, sta, end, factor;
        boolean flag = true;
        int[] p = new int[4];
        while (this.getArmour() != 0 && this.getOpponentArmour() != 0) {
            sta = 0;
            end = 89;
            factor = 90;
            int val;
            p = doscan(sta, end, factor);
            sta = p[0];
            end = p[1];
            factor = p[2];
            x = p[3];
            while (true) {
                if (x > 20 && x < 150) {
                    this.fireMissile(sta + (factor / 2), x);
                    if (!this.isStationary()) {
                        this.stopBot();
                    }
                } else {
                    for (int i = 0; i < 4; i++) {
                        this.fireBullet(sta + (i * 5));
                        sleep(500);
                    }
                }
                this.moveBot(((int) (Math.random() * 100000)) % 360);
                sleep(2000);
                this.stopBot();
                p = doscan(sta, end, factor);
                sta = p[0];
                end = p[1];
                factor = p[2];
                x = p[3];
                val = sta + (factor / 2);
                if (x == -1) {
                    flag = false;
                    break;
                }
            }
            if (!this.isStationary()) {
                this.stopBot();
            }
        }
    }

}
