/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package botwars;

import java.util.TimerTask;

/**
 *
 * @author izaaz
 */
public class TickTimerTask extends TimerTask {

    public void run() {

        Arena.getInstance().tick();

    }
}
