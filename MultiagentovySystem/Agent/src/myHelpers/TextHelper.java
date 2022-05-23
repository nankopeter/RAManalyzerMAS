/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package myHelpers;

import java.text.SimpleDateFormat;
import java.util.Date;
import myOntology.Ontology;
import myMemory.Memory;

/**
 *  Text helper is for changing, replacing strings or texts
 * 
 * @author patrik.matiasko
 */
public class TextHelper {

    public TextHelper() {
        
    }
    
    /** 
     * Remove ontology property tag from string
     * 
     * @param property
     * @return 
     */
    public static String clearProperty(String property){
        
        return property.replace(Ontology.XML_STRING, "");
//        return property.replace((Ontology.BASE + "#"), "");
    }
    
    /**
     * Remove ontology owl base from string
     * 
     * @param string
     * @return 
     */
    public static String clearOWLbase(String string){
    
        return string.replace(Memory.getBase(), "");
    }
    
    /**
     * Change sender to class
     * 
     * @param sender
     * @return 
     */
    public static String senderToClass(String sender){
        
        sender = sender.replace("Sensor", "");
        sender = sender.replace("Agent", "");
        
        return sender;
    }
   
    /**
     * Transform agent to event
     * 
     * @param sensor
     * @return 
     */
    public static String agentToEvent(String sensor){
        
        return sensor.replace("Agent", "Event");
    }
    
    /**
     * Transform sender to event
     * 
     * @param sender
     * @return 
     */
    public static String senderToEvent(String sender){
        
        sender = sender.replace("Sensor", "Event");
        sender = sender.replace("Agent", "Event");
        
        return sender;
    }
    
    /**
     * Format message to exit message
     * 
     * @param message
     * @return 
     */
    public String formatExitMessage(String message){
        String date = new SimpleDateFormat("<YYYY-MM-dd HH:mm:ss>").format(new Date());
        
        return date + " " + message + "\n";
    }
    
    /**
     * Base function, transform data and create new individual name
     * 
     * @param base
     * @param pid
     * @param date
     * @param platform
     * @return 
     */
    public String createIndividualName(String base, String pid, String date, String platform){
        String name = base + "proces";
        name += "-pid-" + pid;
        name += "-created-" + date;
        name += "-platform-" + platform;
        return name;
    }
    
    
}
