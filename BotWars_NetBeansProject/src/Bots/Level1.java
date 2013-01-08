/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Bots;

/**
 *
 * @author Richie
 */
public class Level1 extends botwars.Bot{

    public Level1(){
         super ("Level 1 Bot");
    }
    public void LocateEnemyBotSide( int startAng,int endAng) throws InterruptedException{
        int botLoc=scanRadar(startAng,endAng);
        int EnemyArmour=getOpponentArmour();
        if(botLoc==-1)
        {
           LocateEnemyBotSide((startAng-90+360)%360, (endAng-90+360)%360);
        }
        else if(botLoc<70)
        {
            do{

                EnemyArmour=getOpponentArmour();
                fireMissile(((startAng+360+endAng+360)/2)%360, botLoc);
                 while(getRecoilTime()!=0)
               sleep(100);
            }while(getOpponentArmour()<EnemyArmour);
        }
        else if(botLoc<300)
        {
           do
            {
               EnemyArmour=getOpponentArmour();
                fireMissile(((startAng+360+endAng+360)/2)%360, botLoc);
                while(getRecoilTime()!=0)
               sleep(100);

             } while(getOpponentArmour()<EnemyArmour);
            moveBot(((startAng+360+endAng+360)/2)%360);
            sleep(2000);
            stopBot();
        }
        else
        {
           // fireMissile(((startAng+360+endAng+360)/2)%360, botLoc);
            fireBullet(((startAng+360+endAng+360)/2)%360);
            moveBot(((startAng+360+endAng+360)/2)%360);
            sleep(2000);
            stopBot();
            LocateEnemyBotSide((startAng-45+360)%360,(startAng+45+360)%360);
            LocateEnemyBotSide((endAng-45+360)%360,(endAng+45+360)%360);
        }
}

    @Override
    public void BotLogic() throws InterruptedException {
        while(getOpponentArmour()>1){


        LocateEnemyBotSide(270,0);
        }
   }
}
