/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package botwars;

import java.util.Properties;

/**
 *
 * @author izaaz
 */
public class ArgumentsParser {

    public static void parseArguments(String[] arguments) {
        for (int i = 0; i < arguments.length; i += 2) {
            System.setProperty(arguments[i].substring(1), arguments[i+1]);
        }
    }
    
    public static void printProperties(){
        Properties pp = System.getProperties();
        for(Object prop:pp.keySet()){
            System.out.println(prop + " - " + pp.getProperty((String)prop));
        }
    }
}
