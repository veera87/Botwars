/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package botwars;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

/**
 *
 * @author izaaz
 */
public class BotWarLogger {

    String outputFile;
    PrintWriter writer;
    boolean doNotLog = false;

    public BotWarLogger(String outputFile) throws IOException {
        this.outputFile = outputFile;
        File file = new File(outputFile);
        file.createNewFile();
        writer = new PrintWriter(file);
    }

    public BotWarLogger() {
        doNotLog = true;
    }

    public void disableLogger() {
        doNotLog = true;
    }

    public void enableLogger() {
        doNotLog = true;
    }

    public void writeFrame(String frame) {
        if (doNotLog == true) {
            return;
        }
        writer.println(frame);
    }

    public void closeLogger() {
        if (doNotLog == true) {
            return;
        }
        writer.close();
    }
}
