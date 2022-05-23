/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package myHelpers;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import myOntology.Ontology;
import org.apache.log4j.Logger;


/**
 *
 * Memory helper helps manage agent memory
 * 
 * @author patrik.matiasko
 */
public class MemoryHelper {
    
    private static Logger log = Logger.getLogger(MemoryHelper.class.getName());
    
    public MemoryHelper() {
        
    }
    
    /**
     * Function check individual in agent memory individual list
     * @param model
     * @param procesProperty
     * @param pidProperty
     * @param pid
     * @return 
     */
    public boolean checkIndividual(OntModel model, Property procesProperty, Property pidProperty, String pid){
        ExtendedIterator ei = model.listIndividuals(procesProperty);

        while (ei.hasNext()) {
            Individual i = (Individual) ei.next();

            if(pid.equals(i.getPropertyValue(pidProperty).toString())){
                return true;
            }
        }

        return false;
    }
    
    /**
     * Function check resoruce in agent memory resources list 
     * 
     * @param model
     * @param procesProperty
     * @param name
     * @return 
     */
    public boolean checkResources(OntModel model, Property procesProperty, String name){
        ExtendedIterator ei = model.listIndividuals(procesProperty);

        while (ei.hasNext()) {
            Individual i = (Individual) ei.next();

            if(name.equals(i.toString())){
                return true;
            }
        }

        return false;
    }
    
    /**
     * Function check individual date if exist or not
     * 
     * @param indi
     * @param exist
     * @param createProp
     * @return 
     */
    public boolean checkIndividualDate(Individual indi, Individual exist, Property createProp){
        if(exist.getPropertyValue(createProp).equals(indi.getPropertyValue(createProp))){
            return true;
        }
        return false;
    }
    
    /**
     * Function check property date if exist or not
     * 
     * @param indi
     * @param exist
     * @param createProp
     * @return 
     */
    public boolean chcekPropertyDate(Resource r, Individual exist, Property createProp){
        if(exist.getPropertyValue(createProp).toString().equals(r.getProperty(createProp).getObject().toString())){
            return true;
        }
        return false;
    }
    
    /**
     * Function find agent individual in agent memory individuals list
     * 
     * @param model
     * @param procesProperty
     * @param pidProperty
     * @param pid
     * @return 
     */
    public Individual findIndividualWithPid(OntModel model, Property procesProperty, Property pidProperty, String pid){
        Individual individual = null;

        ExtendedIterator ei = model.listIndividuals(procesProperty);

        while (ei.hasNext()) {
            Individual indi = (Individual) ei.next();

            if(pid.equals(indi.getPropertyValue(pidProperty).toString())){
                individual = indi;
                break;
            }
        }

        return individual;
    }
    
    /**
     * Function return individual by name from agent memory 
     * 
     * @param model
     * @param procesProperty
     * @param name
     * @return 
     */
    public Individual getIndividual(OntModel model, Property procesProperty, String name){
        Individual individual = null;

        ExtendedIterator ei = model.listIndividuals(procesProperty);

        while (ei.hasNext()) {
            Individual indi = (Individual) ei.next();

            if(name.equals(indi.toString())){
                individual = indi;
                break;
            }
        }

        return individual;
    }
    
    /**
     * Return true/false if current agent individual has current property
     * 
     * @param doesProp
     * @param individual
     * @param ontClass
     * @return 
     */
    public boolean individualHasProperty(Property doesProp, Individual individual, OntClass ontClass){
        StmtIterator iter = individual.listProperties(doesProp);

        while (iter.hasNext()) {
            Statement state = (Statement) iter.next();
            if(ontClass.equals(state.getObject())){
                return true;
            }
        }

        return false;
    }
    
    /**
     * Function create new individual in agent memory model
     * 
     * @param model
     * @param indi
     * @param name
     * @param processes
     * @param title
     * @param pid
     * @param created
     * @param does
     * @param sender
     * @param receiver 
     */
    public void createIndividual(OntModel model, Individual indi, String name, Property processes, Property title, Property pid, Property created, Property does, OntClass sender, OntClass receiver){
        Individual individual = model.createIndividual(name, processes);
        individual.addProperty(title, indi.getPropertyValue(title));
        individual.addProperty(pid, indi.getPropertyValue(pid));
        individual.addProperty(created, indi.getPropertyValue(created));
        individual.addProperty(does, sender);
        individual.addProperty(does, receiver);
    }
    
    /**
     * Function create new individual in agent memory model
     * 
     * @param model
     * @param r
     * @param name
     * @param processes
     * @param title
     * @param pid
     * @param created
     * @param does
     * @param sender
     * @param receiver 
     */
    public void createIndividual(OntModel model, Resource r, String name, Property processes, Property title, Property pid, Property created, Property does, OntClass sender, OntClass receiver){
        Individual individual = model.createIndividual(name, processes);
        individual.addProperty(pid, model.createTypedLiteral(r.getProperty(pid).getObject().toString().replace(Ontology.XML_STRING, "")));
        individual.addProperty(title, model.createTypedLiteral(r.getProperty(title).getObject().toString().replace(Ontology.XML_STRING, "")));
        individual.addProperty(created, model.createTypedLiteral(r.getProperty(created).getObject().toString().replace(Ontology.XML_STRING, "")));
        individual.addProperty(does, sender);
        individual.addProperty(does, receiver);
    }
    
    /**
     * Function create new individual in agent memory model
     * 
     * @param model
     * @param r
     * @param name
     * @param processes
     * @param title
     * @param pid
     * @param created
     * @param does
     * @param sender 
     */
    public void createIndividual(OntModel model, Resource r, String name, Property processes, Property title, Property pid, Property created, Property does, OntClass sender){
        Individual individual = model.createIndividual(name, processes);
        individual.addProperty(pid, model.createTypedLiteral(r.getProperty(pid).getObject().toString().replace(Ontology.XML_STRING, "")));
        individual.addProperty(title, model.createTypedLiteral(r.getProperty(title).getObject().toString().replace(Ontology.XML_STRING, "")));
        individual.addProperty(created, model.createTypedLiteral(r.getProperty(created).getObject().toString().replace(Ontology.XML_STRING, "")));
        individual.addProperty(does, sender);
    }
  
    /**
     * Test function to query message to model
     * 
     * @param log
     * @param model
     * @param titleProp
     * @param pidProp
     * @param createdP
     * @param type 
     */
    public void readOnt(Logger log, OntModel model, Property titleProp, Property pidProp, Property createdP, String type){
        log.info("------------ test query ------------");
        
        log.info("--- Quering: " + type + " ---");

        String queryMSG =  "PREFIX ont: <http://patrik.matiasko/agents.owl#>" +
                           "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +
                           "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
                           "SELECT ?x WHERE {?x rdf:type ont:" +  type + "}";
        log.info("1");
        Query query = QueryFactory.create(queryMSG);
        log.info("2");
        QueryExecution qexec = QueryExecutionFactory.create(query, model);
        log.info("3");    
        try {
            log.info("4");
            ResultSet results = qexec.execSelect();
            log.info("5");    
            for (; results.hasNext();) {
                log.info("6");
                QuerySolution soln = results.nextSolution();
                log.info("7");    
                Resource x = soln.getResource("x");
                log.info("ANNNN: " + x.toString());
                log.info("8");    
            }
            log.info("9");
        } finally {
            log.info("10");
            qexec.close();
            log.info("11");
        }
        log.info("12");
        log.info("------------ after query ------------");
    }
    
    
    
     public void readOnt(Logger log, InfModel model, Property titleProp, Property pidProp, Property createdP, String type){
        log.info("------------ test query ------------");
        
        log.info("--- Quering: " + type + " ---");

        String queryMSG =  "PREFIX ont: <http://patrik.matiasko/agents.owl#>" +
                           "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +
                           "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
                           "SELECT ?x WHERE {?x rdf:type ont:" +  type + "}";
        log.info("1");
        Query query = QueryFactory.create(queryMSG);
        log.info("2");
        QueryExecution qexec = QueryExecutionFactory.create(query, model);
        log.info("3");    
        try {
            log.info("4");
            ResultSet results = qexec.execSelect();
            log.info("5");    
            for (; results.hasNext();) {
                log.info("6");
                QuerySolution soln = results.nextSolution();
                log.info("7");    
                Resource x = soln.getResource("x");
                log.info("ANNNN: " + x.toString());
                log.info("8");    
            }
            log.info("9");
        } finally {
            log.info("10");
            qexec.close();
            log.info("11");
        }
        log.info("12");
        log.info("------------ after query ------------");
    }
    
}
