/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Bots;

import botwars.*;
import java.awt.Point;
import java.util.Random;

/**
 *
 * @author aditya
 */
public class Level3 extends Bot{

    boolean r_state;                            //roam state, relocate flag
    Random rn;                                  //rnd no
    int[][] AI_SCN;                             //AI Scan order
    int angle, e_dist;                          //enemy params
    int quad, e_quad, no, i;                    //quadrant params
    long stablize, s_const, recalc_tim, counter;//stableize the loop
    int spread;

    public Level3()
    {
        super("wall-$");        //wall-E v4

        rn = new Random();
        r_state = false;
        AI_SCN = new int[4][4];
        recalc_tim = 50;
        counter = 0;
        quad = -1;
        e_quad = 20;        //shouldnt be same as quad
        s_const = 60;
        e_dist = 0;

        AI_SCN[0][0] =3;AI_SCN[0][1] =2;AI_SCN[0][2] =4;AI_SCN[0][3] =1;
        AI_SCN[1][0] =4;AI_SCN[1][1] =1;AI_SCN[1][2] =3;AI_SCN[1][3] =2;
        AI_SCN[2][0] =1;AI_SCN[2][1] =4;AI_SCN[2][2] =2;AI_SCN[2][3] =3;
        AI_SCN[3][0] =2;AI_SCN[3][1] =3;AI_SCN[3][2] =1;AI_SCN[3][3] =4;
    }


    @SuppressWarnings("empty-statement")
    public void BotLogic() throws InterruptedException
    {
        while(true)
        {
        stablize = System.currentTimeMillis();

        PROTOCOL_FreeRoam();
        if(counter++ % recalc_tim == 0)
            angle = PROTOCOL_Locate();
        PROTOCOL_BurstFire();

        while((System.currentTimeMillis() - stablize) < s_const);
        }
    }

    @SuppressWarnings("static-access")
    int PROTOCOL_Locate() throws InterruptedException
    {
        double ang = 0;
        double tmp, dist = 0;
        int o_dist;
        Point p1, p2;
        int x, y;

        quad = getQUAD(super.getPosition());
        p1 = getPosition();
        super.stopBot();

            for(i = 0; i < 4; i++)
            {
                dist = (double)mY_Scan(quad, i);
                if(dist != -1)
                    break;
            }

        r_state = false;
        counter = 1;
        spread = 15;

        o_dist = (int)dist;
        e_quad = (quad - 1) % 4;
        quad = AI_SCN[e_quad][i];
        quad--;

        super.moveBot(no);
        super.fireBullet(quad * 90 + 45);
        this.sleep(700);
        super.stopBot();
        e_dist = super.scanRadar(quad * 90, quad * 90 + 60);

        if(e_dist == -1)
        {
            e_dist = o_dist;
            ang = quad * 90 + 75;

        }
        else
        {
            super.moveBot(no);
            this.sleep(700);
            super.stopBot();
            super.fireBullet(quad * 90 + 30);
            e_dist = super.scanRadar(quad * 90, quad * 90 + 30);
            ang = quad * 90;
            if(e_dist == -1)
            {
                e_dist = o_dist;
                ang = quad * 90 + 45;
            }
            else ang = quad * 90 + 15;
        }

        return (int) ang;
    }

    int mY_Scan(int qd, int i) throws InterruptedException
    {
        switch(AI_SCN[qd - 1][i])
        {
            case 1:
                return super.scanRadar(0, 89);
            case 2:
                return super.scanRadar(90, 179);
            case 3:
                return super.scanRadar(180, 269);
            default:
                return super.scanRadar(270, 359);
        }
    }

    int getQUAD(Point p)
    {
        if(p.x < 400 && p.y < 250)
            return 2;
        else if(p.x < 400 && p.y > 250)
            return 3;
        else if(p.x > 400 && p.y > 250)
            return 4;
        else return 1;
    }

    void PROTOCOL_BurstFire()
    {
        int F_Ang;

        rn.setSeed(new Random().nextInt());
        F_Ang = rn.nextInt(spread) * ((rn.nextInt() % 2)==0?-1:1) + angle;

        if(e_dist < 190 && super.getMissileAmmo() > 0)
            super.fireMissile(F_Ang, e_dist);
        else super.fireBullet(F_Ang);
    }

    void PROTOCOL_FreeRoam()
    {
        Point pos;
        int curnt_quad;

        curnt_quad = getQUAD(super.getPosition()) - 1;
        rn.setSeed(new Random().nextInt());
        no = rn.nextInt(360);
        pos = getPosition();

        if(e_dist > 600 && !r_state)
        {
            no = rn.nextInt(spread) * ((rn.nextInt() % 2)==0?-1:1) + angle;
            super.moveBot(no);
            r_state = true;
            return;
        }
        else if(quad == e_quad)
        {
            no = rn.nextInt(spread) * ((rn.nextInt() % 2)==0?-1:1) + (AI_SCN[e_quad][0] - 1) * 90 + 45;
            super.moveBot(no);
            r_state = true;
            return;
        }

        if(pos.x >= 750 && (no > 270 || no < 90))
        {
            no = rn.nextInt(180) + 90;
            r_state = false;
        }
        else  if(pos.x <= 50 && (no < 270 && no > 90))
        {
            no = rn.nextInt(90);
            r_state = false;
        }

        if(pos.y >= 450 && (no >= 180))
        {
            no -= 180;
            r_state = false;
        }
        else  if(pos.y <= 50 && (no <= 180))
        {
            no += 180;
            r_state = false;
        }

        if(!r_state)
        {
            moveBot(no);
            r_state = true;
        }
    }
}
