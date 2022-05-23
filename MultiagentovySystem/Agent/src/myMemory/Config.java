/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package myMemory;

import java.util.Map;
import org.apache.log4j.Logger;

/**
 *
 * @author patrik.matiasko
 */
public class Config {
    
    private static Logger log = Logger.getLogger( Config.class.getName() );
    
    static java.util.Properties config = null;
    
    public Config(Map<String, String> properties) {
             
        IS_AGENT = Boolean.parseBoolean(properties.get("IS_AGENT"));
        IS_SENSOR = Boolean.parseBoolean(properties.get("IS_SENSOR"));

        CREATE_MODEL = Boolean.parseBoolean(properties.get("CREATE_MODEL"));
        REMOVE_MODEL = Boolean.parseBoolean(properties.get("REMOVE_MODEL"));
        MEM_MODEL = Boolean.parseBoolean(properties.get("MEM_MODEL"));

        if(IS_AGENT && !IS_SENSOR){
            INDIVIDUALS_FILE = properties.get("INDIVIDUALS_FILE");
            OUTPUT_FILE = properties.get("OUTPUT_FILE");
            AGENT_SENSORS = properties.get("AGENT_SENSORS");
            DIRECT_SAVE = Boolean.parseBoolean(properties.get("DIRECT_SAVE"));
            READ_METHOD = Boolean.parseBoolean(properties.get("READ_METHOD"));
            QUERY_INTERVAL = Integer.valueOf(properties.get("QUERY_INTERVAL"));
        }
        
        if(IS_SENSOR && !IS_AGENT){
            PARENT_AGENTS_ADDRESS = properties.get("PARENT_AGENTS_ADDRESS");
            SENDING_INTERVAL = Integer.valueOf(properties.get("SENDING_INTERVAL"));
            REASONING_INTERVAL = Integer.valueOf(properties.get("REASONING_INTERVAL"));
        }
        
        //main agent
        if(IS_AGENT && IS_SENSOR){
            INDIVIDUALS_FILE = properties.get("INDIVIDUALS_FILE");
            OUTPUT_FILE = properties.get("OUTPUT_FILE");
            AGENT_SENSORS = properties.get("AGENT_SENSORS");
            DIRECT_SAVE = Boolean.parseBoolean(properties.get("DIRECT_SAVE"));
            READ_METHOD = Boolean.parseBoolean(properties.get("READ_METHOD"));
            QUERY_INTERVAL = Integer.valueOf(properties.get("QUERY_INTERVAL"));
            PARENT_AGENTS_ADDRESS = properties.get("PARENT_AGENTS_ADDRESS");
            REASONING_INTERVAL = Integer.valueOf(properties.get("REASONING_INTERVAL"));
        }
        
        
        PARENT_AGENTS = properties.get("PARENT_AGENTS");
        HOST = properties.get("HOST");
        PORT = properties.get("PORT");
        PLATFORM_ID = properties.get("PLATFORM_ID");
        IS_GUIDE = Boolean.parseBoolean(properties.get("IS_GUIDE"));
    }
    
    public String DB_URL = "";

    public String DB_USER = "";

    public String DB_PASSWD = "";
    
    public String DB_TYPE = "";
    
    public String BASE = "";
    
    public String SOURCE_FILE = "";

    public String DBDRIVER_CLASS = "";
    
    public boolean IS_AGENT = true;
    
    public boolean IS_SENSOR = false;
    
    public String PARENT_AGENTS = "";
    
    public String AGENT_SENSORS = "";
    
    public boolean DIRECT_SAVE = false;
    
    public boolean READ_METHOD = false;
    
    public String HOST = "";
    
    public String PORT = "";
    
    public String PLATFORM_ID = "";
    
    public boolean IS_GUIDE = false;
    
    public String PARENT_AGENTS_ADDRESS = "";
    
    public int QUERY_INTERVAL = 5000;
    
    public int REASONING_INTERVAL = 5000;
    
    public int SENDING_INTERVAL = 15000;
   

    /**
     * If True it creates model in RDB (MySQL) when agent is started
     * We need to set this True only first time when running agent or if we want to recreate model from OWL files
     * in case of recreating we need to set REMOVE_MODEL True as well in order to remove old model first
     * OWl files which contains of Ontology definition and Ontology individuals (Instances) are defined in Ontology Config
     */
    public boolean CREATE_MODEL = false;
    
    
    /**
     * If True it removes model from RDB (MySQL) when agent is started
     */
    public boolean REMOVE_MODEL = false;
    
    
    /**
     * If True it reads model from FILE defined in ontology config properties and holds model in memory instead of MySQL database
     * it is faster but not very good solution.
     * When set to true REMOVE_MODEL and CREATE_MODEL is not valid 
     */
    public boolean MEM_MODEL = false;

    
    /**
     * Individuals file defines path to OWL file containing of agent ontology Individuals (instances)
     * This can be loaded when Memory Config {@link agent.core.memory.Config#CREATE_MODEL CREATE_MODEL} is set True 
     */    
    public String INDIVIDUALS_FILE = "";
    
    
    /**
     * Output file defines path to OWL file where content of OM can we write when calling method
     */    
    public String OUTPUT_FILE = "";
    
    
}
