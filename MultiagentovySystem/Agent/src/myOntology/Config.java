/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package myOntology;

import org.apache.log4j.Logger;

/**
 *
 * @author patrik.matiasko
 */
public class Config {
    
    private static Logger log = Logger.getLogger( Config.class.getName() );
    /**
     *  Properties file
     */
    static java.util.Properties config = null;
    

    /**
     * BASE returns agent ontology url without "#" at the end
     * same string with # can be returned by  {@link agent.core.memory.Memory#getBase() Memory.getBase()} method
     */    
    public static String BASE = "http://kristian.sranko/rekal_agents.owl";
    
    /**
     * Source file defines path to OWL file containing of agent ontology definitions
     * This can be loaded when OM config {@link agent.core.memory.Config#CREATE_MODEL CREATE_MODEL} is set True 
     */
    public static final String SOURCE_FILE = System.getProperty("user.dir") + "\\MultiagentovySystem\\Agent\\memory_init\\rekal_agents.owl";
}
