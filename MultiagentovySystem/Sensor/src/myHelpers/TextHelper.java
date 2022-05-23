package myHelpers;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
     * Remove senzor name
     *
     * @param sensor
     * @return
     */
    public static String sensorToClass(String sensor){

        return sensor.replace("Sensor", "");
    }

    /**
     * Remove anget name
     *
     * @param sensor
     * @return
     */
    public static String agentToClass(String sensor){

        return sensor.replace("Agent", "");
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

    public String createIndividualProcessName(String base, String pid,String process_name, String date, String platform){
        String name = base + "proces";
        name += "-pid-" + pid;
        name += "-process_name-" + process_name;
        name += "-created-" + date;
        name += "-platform-" + platform;
        return name;
    }

    public String createIndividualCallbackName(String base, String type,String driverName, String detail, String platform){
        String name = base + "callback";
        name += "-type-" + type;
        name += "-driver_name-" + driverName;
        name += "-detail-" + detail;
        name += "-platform-" + platform;
        return name;
    }

    public String createIndividualDllName(String base, String base_address, String path, String platform){
        String name = base + "dll";
        name += "-base-" + base_address;
        name += "-path-" + path;
        name += "-platform-" + platform;
        return name;
    }

    public String createIndividualPropertiesName(String base, String address_i, String platform){
        String name = base + "properties";
        name += "-address-" + address_i;
        name += "-platform-" + platform;
        return name;
    }

    public String createIndividualFunctionName(String base, String function_name, String platform){
        String name = base + "function";
        name += "-name-" + function_name;
        name += "-platform-" + platform;
        return name;
    }

    public String createIndividualDriverFunctionName(String base, String driver_name, String function_name, String platform){
        String name = base + "function";
        name += "-driver-" + driver_name;
        name += "-function_name-" + function_name;
        name += "-platform-" + platform;
        return name;
    }

    public String createIndividualDriverName(String base, String driver_name, String address, String platform){
        String name = base + "driver";
        name += "-name-" + driver_name;
        name += "-address-" + address;
        name += "-platform-" + platform;
        return name;
    }


}
