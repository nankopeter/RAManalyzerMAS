/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package myHelpers;

import jade.lang.acl.ACLMessage;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;
import org.apache.log4j.Logger;
import org.apache.xmlrpc.XmlRpcClientLite;
import org.apache.xmlrpc.XmlRpcException;

/**
 *
 * Helper can send data via XmlRpcClientLite to XmlRpcServer
 * 
 * @author patrik.matiasko
 */
public class DashBoardHelper {
    
    private static Logger log = Logger.getLogger(DashBoardHelper.class.getName());
   
    public static final String SERVER = "http://localhost:8001";
    XmlRpcClientLite myClient;

    public DashBoardHelper() {
      try {
        myClient = new XmlRpcClientLite (SERVER);
      }
      catch(Exception e) {
        log.error("error: " + e.toString());
        e.printStackTrace();
      }  
    }
    
    
    /** 
     * Function send data message via XmlRpcClientLite to XmlRpcSercer
     * 
     * @param sender
     * @param messageType
     * @param receiver 
     */
    public void addMessage(String sender, int messageType, String receiver){
        String result = "";
        Vector<String> v = new Vector<String> ();
        String date = new SimpleDateFormat("<YYYY-MM-dd HH:mm:ss>").format(new Date());
        String message = date + " Sender: " + sender + ",\t type: " + typeToString(messageType);
        message += ",\t Receiver: " + receiver;
        message += "\n";
        
        v.addElement (message);

        try {
            result = (String) myClient.execute("Gui." + "addMessage", v);
        } catch (XmlRpcException e ){
            log.error("exception while transmitting message " + e.toString());
        } catch (java.io.IOException e) {
            log.error("exception while transmitting message " + e.toString());
        }
        
    }
    
    /**
     * Function send data message via XmlRpcClientLite to XmlRpcSercer
     * 
     * @param sender
     * @param messageType
     * @param receiver 
     */
    public void addNewMessage(String sender, int messageType, String receiver){
        String result = "";
        Vector<String> v = new Vector<String> ();
        String date = new SimpleDateFormat("<YYYY-MM-dd HH:mm:ss>").format(new Date());  
        String message = date + " " + sender +  ",\t new " + typeToString(messageType) + " message";
        message += ",\t from: " + receiver;
        message += "\n";
        
        v.addElement (message);

        try {
            result = (String) myClient.execute("Gui." + "addMessage", v);
        } catch (XmlRpcException e ){
            log.error("exception while transmitting message " + e.toString());
        } catch (java.io.IOException e) {
            log.error("exception while transmitting message " + e.toString());
        }
        
    }
    
    
    /**
     * Convert ACLMessage type to string
     * 
     * @param type
     * @return 
     */
    private String typeToString(int type){
        String string = "";
        
        switch(type){
            case ACLMessage.PROPOSE:
                string = "PROPOSE";
            break;
            case ACLMessage.INFORM:
                string = "INFORM";
            break;
            case ACLMessage.REQUEST:
                string = "REQUEST";
            break;
            case ACLMessage.QUERY_REF:
                string = "QUERY_REF";
            break;
        }
        return string;
    }

    /**
     * Function send data message on chage status via XmlRpcClientLite to XmlRpcSercer
     * 
     * @param name
     * @param action 
     */
    public void changeStatus(String name, String action){
        String result = "";
        Vector<String> v = new Vector<String> ();
        v.addElement (name);
        v.addElement (action);
        
        try {
            result = (String) myClient.execute("Gui." + "changeStatus", v);
        } catch (XmlRpcException e ){
            log.error("exception while transmitting message " + e.toString());
        } catch (java.io.IOException e) {
            log.error("exception while transmitting message " + e.toString());
        }
    }
    
    public void log(Logger loger, String msg){
        DateFormat dateFormat = new SimpleDateFormat("<HH:mm:ss>");
        Date date = new Date();
        String formatedDate = dateFormat.format(date);
        loger.info(formatedDate + " " + msg);
    }
   
}
