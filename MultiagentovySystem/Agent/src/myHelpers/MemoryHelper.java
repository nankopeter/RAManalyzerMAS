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
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import myMemory.Memory;
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
    private TextHelper textHelper;
    
    public MemoryHelper() {
        textHelper = new TextHelper();
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
    
    public boolean individualHasResource(Property actionProp, Individual individual, RDFNode ontClass){
        StmtIterator iter = individual.listProperties(actionProp);

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
    
    public void createIndividual(OntModel model, Resource r, String name, Property individual_i, Property does, OntClass sender){
        Individual individual = model.createIndividual(name, individual_i);
        individual.addProperty(does, sender);
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
     * @param platform
     * @param does
     * @param sender 
     */
    public void createIndividual(OntModel model, Resource r, String name, Property processes, Property title, Property pid, Property created, Property platform, Property does, OntClass sender){

        Individual individual = model.createIndividual(name, processes);
        individual.addProperty(pid, model.createTypedLiteral(r.getProperty(pid).getObject().toString().replace(Ontology.XML_STRING, "")));
        individual.addProperty(title, model.createTypedLiteral(r.getProperty(title).getObject().toString().replace(Ontology.XML_STRING, "")));
        individual.addProperty(created, model.createTypedLiteral(r.getProperty(created).getObject().toString().replace(Ontology.XML_STRING, "")));
        individual.addProperty(platform, model.createTypedLiteral(r.getProperty(platform).getObject().toString().replace(Ontology.XML_STRING, "")));
        individual.addProperty(does, sender);

        StmtIterator existingProp = r.listProperties(does);

        while(existingProp.hasNext()) {

            Statement state = (Statement) existingProp.next();
            individual.addProperty(does, model.getOntClass(state.getObject().toString()));
            try{
                existingProp.hasNext();
            }catch(Exception e ){
                break;
            }
        }
    }

//    public void createIndividual(OntModel model, Resource r, String name, Property processes, Property title, Property pid, Property process_name, Property created, Property platform, Property does, OntClass sender){
//
//        Individual individual = model.createIndividual(name, processes);
//        individual.addProperty(pid, model.createTypedLiteral(r.getProperty(pid).getObject().toString().replace(Ontology.XML_STRING, "")));
//        individual.addProperty(title, model.createTypedLiteral(r.getProperty(title).getObject().toString().replace(Ontology.XML_STRING, "")));
//        individual.addProperty(created, model.createTypedLiteral(r.getProperty(created).getObject().toString().replace(Ontology.XML_STRING, "")));
//        individual.addProperty(platform, model.createTypedLiteral(r.getProperty(platform).getObject().toString().replace(Ontology.XML_STRING, "")));
//        individual.addProperty(does, sender);
//
//        StmtIterator existingProp = r.listProperties(does);
//
//        while(existingProp.hasNext()) {
//
//            Statement state = (Statement) existingProp.next();
//            individual.addProperty(does, model.getOntClass(state.getObject().toString()));
//            try{
//                existingProp.hasNext();
//            }catch(Exception e ){
//                break;
//            }
//        }
//    }


//    public void createProcessIndividual(OntModel model, Resource r, String name, Property individual_i, Property pid, Property created, Resource r_proc, Property does, String sender){
//        Individual individual = model.createIndividual(name, individual_i);
//        individual.addProperty(pid, model.createTypedLiteral(r.getProperty(pid).getObject().toString().replace(Ontology.XML_STRING, "")));
//        individual.addProperty(created, model.createTypedLiteral(r.getProperty(created).getObject().toString().replace(Ontology.XML_STRING, "")));
//        individual.addOntClass(r_proc);
//
//        OntClass senderClass = model.getOntClass(Memory.getBase() + textHelper.senderToClass(sender));
//        individual.addProperty(does, senderClass);
//
//    }


    public void createProcessIndividual(OntModel model, Resource r, String name, Property individual_i, Property process_name, Property pid, Property created, Resource r_proc, Property does, String sender){
        Individual individual = model.createIndividual(name, individual_i);
        individual.addProperty(pid, model.createTypedLiteral(r.getProperty(pid).getObject().toString().replace(Ontology.XML_STRING, "")));
        individual.addProperty(process_name, model.createTypedLiteral(r.getProperty(process_name).getObject().toString().replace(Ontology.XML_STRING, "")));
        individual.addProperty(created, model.createTypedLiteral(r.getProperty(created).getObject().toString().replace(Ontology.XML_STRING, "")));
        individual.addOntClass(r_proc);

        OntClass senderClass = model.getOntClass(Memory.getBase() + textHelper.senderToClass(sender));
        individual.addProperty(does, senderClass);

    }
    
    public void createDLLIndividual(OntModel model, Resource r, String name, Property individual_i, Property base, Property loadProperty, Property initProperty, Property memProperty, Property path, Property action, Resource r_dll){
        Individual individual = model.createIndividual(name, individual_i);
        
        StmtIterator iter_s = r.listProperties(action);
        while (true) {
            try{
                iter_s.hasNext();
                Statement state = (Statement) iter_s.next();
                individual.addProperty(action , state.getObject());
//                log.info("****dllOf: " + state.getObject().toString());
            }catch(Exception e){
                break;
            }
        }
        
        individual.addProperty(base, model.createTypedLiteral(r.getProperty(base).getObject().toString().replace(Ontology.XML_STRING, "")));
        individual.addProperty(loadProperty, model.createTypedLiteral(r.getProperty(loadProperty).getObject().toString().replace(Ontology.XML_STRING, "")));
        individual.addProperty(initProperty, model.createTypedLiteral(r.getProperty(initProperty).getObject().toString().replace(Ontology.XML_STRING, "")));
        individual.addProperty(memProperty, model.createTypedLiteral(r.getProperty(memProperty).getObject().toString().replace(Ontology.XML_STRING, "")));
        individual.addProperty(path, model.createTypedLiteral(r.getProperty(path).getObject().toString().replace(Ontology.XML_STRING, "")));
        
        individual.addOntClass(r_dll);
        
//        RDFNode object_i = r.getProperty(action).getObject();
//        individual.addProperty(action , object_i);  
    }

    public void createPropertyIndividual(OntModel model, Resource r, String name, Property individual_i, Property address, Property vadTag, Property protection, Property private_i, Property action, Resource r_prop){
        Individual individual = model.createIndividual(name, individual_i);
        
        StmtIterator iter_s = r.listProperties(action);
        while (true) {
            try{
                iter_s.hasNext();
                Statement state = (Statement) iter_s.next();
                individual.addProperty(action , state.getObject());
//                log.info("****dllOf: " + state.getObject().toString());
            }catch(Exception e){
                break;
            }
        }
        
        individual.addProperty(address, model.createTypedLiteral(r.getProperty(address).getObject().toString().replace(Ontology.XML_STRING, "")));
        individual.addProperty(vadTag, model.createTypedLiteral(r.getProperty(vadTag).getObject().toString().replace(Ontology.XML_STRING, "")));
        individual.addProperty(protection, model.createTypedLiteral(r.getProperty(protection).getObject().toString().replace(Ontology.XML_STRING, "")));
        individual.addProperty(private_i, model.createTypedLiteral(r.getProperty(private_i).getObject().toString().replace(Ontology.XML_STRING, "")));
        individual.addOntClass(r_prop);
    }


    public void createPropertyIndividual2(OntModel model, Resource r, String name, Property individual_i, Property address, Property vadTag, Property action, Resource r_prop){
        Individual individual = model.createIndividual(name, individual_i);

        StmtIterator iter_s = r.listProperties(action);
        while (true) {
            try{
                iter_s.hasNext();
                Statement state = (Statement) iter_s.next();
                individual.addProperty(action , state.getObject());
            }catch(Exception e){
                break;
            }
        }

        individual.addProperty(address, model.createTypedLiteral(r.getProperty(address).getObject().toString().replace(Ontology.XML_STRING, "")));
        individual.addProperty(vadTag, model.createTypedLiteral(r.getProperty(vadTag).getObject().toString().replace(Ontology.XML_STRING, "")));
        individual.addOntClass(r_prop);
    }


    
    public void createFunctionIndividual(OntModel model, Resource r, String name, Property individual_i, Property address, Property function_name,  Property action1, Property action2, Resource r_func){
        Individual individual = model.createIndividual(name, individual_i);
        
        StmtIterator iter_s = r.listProperties(action1);
        while (true) {
            try{
                iter_s.hasNext();
                Statement state = (Statement) iter_s.next();
                individual.addProperty(action1 , state.getObject());
//                log.info("****dllOf: " + state.getObject().toString());
            }catch(Exception e){
                break;
            }
        }
        
        iter_s = r.listProperties(action2);
        while (true) {
            try{
                iter_s.hasNext();
                Statement state = (Statement) iter_s.next();
                individual.addProperty(action2 , state.getObject());
//                log.info("****dllOf: " + state.getObject().toString());
            }catch(Exception e){
                break;
            }
        }
        
        try{
            individual.addProperty(address, model.createTypedLiteral(r.getProperty(address).getObject().toString().replace(Ontology.XML_STRING, "")));
        }catch(Exception e){
        }
        individual.addProperty(function_name, model.createTypedLiteral(r.getProperty(function_name).getObject().toString().replace(Ontology.XML_STRING, "")));
        individual.addOntClass(r_func);
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

        String queryMSG =  "PREFIX ont: <http://kristian.sranko/rekal_agents.owl>" +
                           "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +
                           "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
                           "SELECT ?x WHERE {?x rdf:type ont:" +  type + "}";
     
        Query query = QueryFactory.create(queryMSG);
        QueryExecution qexec = QueryExecutionFactory.create(query, model);

        try {
            ResultSet results = qexec.execSelect();

            for (; results.hasNext();) {
                QuerySolution soln = results.nextSolution();

                Resource x = soln.getResource("x");
                log.info("Answer: " + x.toString());

            }
        } finally {
            qexec.close();
        }

        log.info("------------ after query ------------");
    }
    
}
