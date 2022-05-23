/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package myMemory;

import com.hp.hpl.jena.db.DBConnection;
import com.hp.hpl.jena.db.IDBConnection;
import com.hp.hpl.jena.ontology.OntDocumentManager;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ModelMaker;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.reasoner.ReasonerRegistry;
import com.hp.hpl.jena.reasoner.rulesys.GenericRuleReasoner;
import com.hp.hpl.jena.reasoner.rulesys.Rule;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Map;

import myOntology.Ontology;
import org.apache.log4j.Logger;

/**
 *
 * @author patrik.matiasko
 */
public class Memory {
    public Config config = null;
    boolean loadIndi = false;
    
    public Memory(String _agentName, Map<String, String> properties, boolean loadInvidiual) {
    log = Logger.getLogger(_agentName+ " " + Memory.class.getName() );
        config = new Config(properties);
        loadIndi = loadInvidiual;
    this.getModel();
    } 
    
  /**
   * Default ontology model name
   */
  private static final String DEFAULT_MODEL_NAME = "urn:x-hp-jena:agent";

  /**
   * Log4j object
   */
  private Logger log = null;

  /**
   * Instance of database connection object
   */
  private IDBConnection conn = null;

  /**
   * Instance of ontological model used in agent
   */
  private OntModel model = null;
    
  private OntModel reasoningModel = null;

  /**
   * State variable identifies whether model is loaded
   */
  private boolean loaded = false;

  /**
   * Returns the agent ontology name space
   * @return name space of agent
   */
  public static String getBase() {
    return Ontology.BASE + "#";
  }

  public String getParentAgents(){
      return config.PARENT_AGENTS;
  }
  
  public String getAgentSensors(){
      return config.AGENT_SENSORS;
  }
  
  
  /**
   * This method returns db connection
   * @return instance of db connection object
   */

  public IDBConnection getConnection() {
    log.info("IDB Connection call");
    if (conn == null) {
      if (!loaded) {  
        // Load the Driver
        try {
          Class.forName(config.DBDRIVER_CLASS); // load driver
        }
        catch (Exception e) {
          log.error("failed loading driver:"+ e.toString());
        }
        loaded = true;
      }  
      // Create database connection
      log.info("DB:"+config.DB_URL+" user:"+ config.DB_USER+ " pass:"+config.DB_PASSWD);
      conn = new DBConnection(config.DB_URL, config.DB_USER, config.DB_PASSWD, config.DB_TYPE);
    }
    return conn;
  }
  
  /**
   * Creates OWL model out of defined SOURCE_FILE in OWL database and store this model
   * see: {@link agent.core.onto.Config#SOURCE_FILE SOURCE_FILE},  
   *   {@link agent.core.memory.Config#INDIVIDUALS_FILE INDIVIDUALS_FILE}  
   */
  public void createModel() {
    ModelMaker modelMaker = ModelFactory.createModelRDBMaker (getConnection());

    OntModelSpec ontModelSpec = new OntModelSpec(OntModelSpec.OWL_MEM_RDFS_INF);
    ontModelSpec.setBaseModelMaker(modelMaker);
    ontModelSpec.setDocumentManager(new OntDocumentManager());

    Model base = modelMaker.createModel(DEFAULT_MODEL_NAME);
    model = ModelFactory.createOntologyModel(ontModelSpec, base);

    model.read(myOntology.Config.SOURCE_FILE);
    model.read(config.INDIVIDUALS_FILE);
    model.close();
    model = null;
  }
  
  public OntModel createEmptyModel() {
    OntModel emptyModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_RDFS_INF, null);
    emptyModel.removeAll();
    return emptyModel;
  }
  
  
  /**
   * Remove model from DB
  */
  public void removeModel(){
    ModelMaker modelMaker = ModelFactory.createModelRDBMaker (getConnection());
    if (modelMaker.hasModel(DEFAULT_MODEL_NAME)) {
      modelMaker.removeModel(DEFAULT_MODEL_NAME);
      modelMaker.close();
      model = null;
    }
  }
  
  public void clearModel(){
      model = null;
  }
    
  /**
   * Returns OWL ontological model for manipulation
   * @return ontological model
   */  
  public OntModel getModel() {
    if (model == null) {
      if (config.MEM_MODEL) {
        model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_RDFS_INF, null);
        try {
            //log.info("Loading source file: " + myOntology.Config.SOURCE_FILE);
            model.read(new FileInputStream(myOntology.Config.SOURCE_FILE), Ontology.BASE);
            if(config.IS_AGENT && loadIndi){
//                log.info("Loading individual file: " + config.INDIVIDUALS_FILE);
                model.read(new FileInputStream(config.INDIVIDUALS_FILE), Ontology.BASE);
            }
            if(config.IS_AGENT && config.READ_METHOD){
                model.read(new FileInputStream(config.OUTPUT_FILE), Ontology.BASE);
            }
        } catch (Exception e) {
            log.error("Loading Model failed:" + e);
        }
      } else {
        log.info("Starting with MySQL model ...");
//        if (config.REMOVE_MODEL)
//            removeModel();
//        log.debug("Create model");
//        if (config.CREATE_MODEL)
//            createModel();
//
//        ModelMaker modelMaker = ModelFactory.createModelRDBMaker (getConnection());
//
//  //    if (modelMaker.hasModel(DEFAULT_MODEL_NAME)) modelMaker.removeModel(DEFAULT_MODEL_NAME);
//
//        OntModelSpec ontModelSpec = new OntModelSpec(OntModelSpec.OWL_MEM);
//        ontModelSpec.setModelMaker(modelMaker);
//        ontModelSpec.setDocumentManager(new OntDocumentManager());
//
//        Model base = modelMaker.openModel(DEFAULT_MODEL_NAME);
//        model = ModelFactory.createOntologyModel(ontModelSpec, base);
      }
    }
    
    if(model != null){
        return model;
    }
    else{
        return null;
    }
  }
  
  /**
   * Returns OWL ontological model for manipulation with memory data include reasoner
   * @return ontological model
   */  
  public OntModel getReasoningModel(){
      OntModel baseModel = getModel();
      
      if(reasoningModel == null){
        
//        log.info("creating reasoning model .. ");
        Reasoner reasoner = ReasonerRegistry.getOWLMicroReasoner();
        reasoner = reasoner.bindSchema(baseModel);

        OntModelSpec ontModelSpec = OntModelSpec.OWL_MEM_RDFS_INF;
        ontModelSpec.setReasoner(reasoner);

        reasoningModel = ModelFactory.createOntologyModel(ontModelSpec, baseModel);
//        reasoningModel = ModelFactory.createInfModel(reasoner, baseModel);
//        this.getModel().add(reasoningModel);
//        log.info("done");
      }
      
      if(reasoningModel != null){
        return reasoningModel;
      }
      else{
        return baseModel;
      }
  }
  
  
  /**
   * Infers some new statements about the agent ontological model accoding to given rules of inference
   * and adds inferenced model as submodel of agent
   * @param ruleFile path to file with line delimited list of rules
   * @param derivationLogging true/false value indicates whether to perform logging of inferenced statements
   * false value accelerate inferece processing
   * @see <a HREF="http://jena.sourceforge.net/javadoc/com/hp/hpl/jena/rdf/model/InfModel.html#setDerivationLogging(boolean)">setDerivationLogging</a>
   */
  public void performInference(String ruleFile, boolean derivationLogging) {
    log.info("Perform inference");
    // prepare rules
    String rule = null, rules = "";
    File file = new File(ruleFile);
    try {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        while ((rule = reader.readLine()) != null)
        rules += rule;
        log.error(rules);
    } catch (FileNotFoundException e) {
        log.error(e);
    } catch (IOException e) {
        log.error(e);
    }
    
    Reasoner reasoner = new GenericRuleReasoner(Rule.parseRules(rules));
    reasoner.setDerivationLogging(derivationLogging);
    InfModel infModel = ModelFactory.createInfModel(reasoner, this.getModel());
    infModel.prepare();
    this.getModel().add(infModel.getDeductionsModel(), true);
  }
  
  /**
   * Sometimes if operation is idle MySQL connection fails and we need to reset connection and model 
   */
  public void reset() {
    model = null;
    conn = null;
    ModelMaker modelMaker = ModelFactory.createModelRDBMaker (getConnection());

    OntModelSpec ontModelSpec = new OntModelSpec(OntModelSpec.OWL_MEM);
    ontModelSpec.setBaseModelMaker(modelMaker);
    ontModelSpec.setDocumentManager(new OntDocumentManager());

    Model base = modelMaker.createModel(DEFAULT_MODEL_NAME);
    model = ModelFactory.createOntologyModel(ontModelSpec, base);
  }
  
  /**
   * Prints objects from OWL model for testing purposes
   */
  public void test() {
    Iterator i = this.getModel().listObjects();
    while (i.hasNext()) {
      Object o = i.next();
      //if (o instanceof OntClass) {
      log.info(o.toString());
      //}
    }
  }

  /**
   * Saves current content of Memory into file defined in Ontology Property file
   * see: {@link agent.core.memory.Config#OUTPUT_FILE OUTPUT_FILE}  
   */
  public void memory2owl() {
    try {
        Model m = this.getModel();
        log.info("Creating file: "+config.OUTPUT_FILE);
        OutputStream file = new FileOutputStream(config.OUTPUT_FILE);
        m.write(file,"RDF/XML", Ontology.BASE);
        file.close();
      
    } catch (Exception e) {
        log.info("Exporting file problem : " + e.toString());
    }
  }
  
  /**
   * Close OWL Model and store Model in RDB if using database backend
   * This have to be redesigned and store everything on the crash
   */
  public void closeModel() {
    getModel().close();
    model = null;
  }

  /**
   * Creates ontology property out ofstring represenation
   * @param property represents local name of ontology property
   * @return Property
   */
  public Property createProperty(String property) {
    return getModel().createProperty(getBase() + property);
  }

  /**
   * Tests the status of connection. If it doesn't exist then make new connection to database and
   *  initialize model
   */
  public void testConnection() {
    log.info("Test Connection ...");
    if (conn != null)
      try {
        if (!conn.containsModel(DEFAULT_MODEL_NAME)) {
          conn.close();
          conn = null;
          model = getModel();
        }
      }
      catch (Exception e) {
        log.error(e);
      }
  }
    
}
