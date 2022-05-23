/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package myCommunication;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import java.io.StringWriter;
import java.util.List;

import myOntology.Ontology;
import org.apache.log4j.Logger;

/**
 *
 * @author patrik.matiasko
 */
public class Message {

    private static Logger log = Logger.getLogger(Message.class.getName());
    
    private static void logger(String sender, String receiver, String message) {
        log.info("### Message from: " + sender + ", for: " + receiver + ", is : " + message);
    }

    /**
     * This method creates ACL Inform message containing of RDF of some String
     * content
     *
     * @param sender Agent which sends message
     * @param receiverName string name of Agent which receive message
     * @param content of message is string
     * @return ACLMessage
     */
    public static ACLMessage createInformMessage(Agent sender, String receiverName, String content) {
        ACLMessage message = new ACLMessage(ACLMessage.INFORM);
        AID receiver = new AID(receiverName, false);

        message.setSender(sender.getAID());
        message.addReceiver(receiver);
        message.setContent(content);

        return message;
    }

    /**
     * This method creates ACL Inform message containing of RDF of some string
     * content
     *
     * @param sender Agent which sends message
     * @param receiverNames string names of Agents which receive one message
     * @param content of message is string
     * @return ACLMessage
     */
    public static ACLMessage createInformMessage(Agent sender, List<String> receiverNames, String content) {
        ACLMessage message = new ACLMessage(ACLMessage.INFORM);
        message.setSender(sender.getAID());
        message.setContent(content);

        receiverNames.stream().forEach((receiverName) -> {
            AID receiver = new AID(receiverName, false);
            message.addReceiver(receiver);
        });

        return message;
    }

    /**
     * This method creates ACL Inform message containing of RDF of some Jena RDF
     * Resource
     *
     * @param sender Agent which sends message
     * @param receiverName string name of Agent which receive message
     * @param resource Jena resource
     * @return ACLMessage message
     *
     */
    public static ACLMessage createInformMessage(Agent sender, String receiverName, Resource resource) {
        String content = resource2RDF(resource);
        ACLMessage m = new ACLMessage(ACLMessage.INFORM);
        m.setSender(sender.getAID());
        m.addReceiver(new AID(receiverName, false));
        m.setLanguage(Ontology.RDF);
        m.setOntology(Ontology.BASE);
        m.setContent(content);
        
        return m;
    }
    

    /**
     * This method create ACL query message from one agent to another
     *
     * @param sender - Agent which sends message
     * @param receiverName - agent name which receive message
     * @param query - sparql query
     * @return ACLMessage message
     */
    public static ACLMessage createQueryMessage(Agent sender, String receiverName, String query) {
        ACLMessage m = new ACLMessage(ACLMessage.QUERY_REF);
        AID receiver = new AID(receiverName, false);
        m.setSender(sender.getAID());
        m.addReceiver(receiver);
        m.setLanguage(Ontology.SPARQL);
        m.setOntology(Ontology.BASE);

        String content = Ontology.SPARQL_PREFIX + query;

        m.setContent(content);

        return m;
    }

    /**
     * This method creates ACL Propose message containing of RDF of some Jena
     * RDF Resource
     *
     * @param sender Agent which sends message
     * @param receiverName string name of Agent which receive message
     * @param resource Jena resource
     * @param is_guide Boolean if is guide(internal or external)
     * @param address String address of receiver only if is_guide = true
     * @return ACLMessage message
     *
     */
    public static ACLMessage createProposeMessage(Agent sender, String receiverName, Resource resource, Boolean is_guide, String address) {
        String content = resource2RDF(resource);
        ACLMessage m = new ACLMessage(ACLMessage.PROPOSE);
        AID aid = new AID(receiverName, is_guide);
        
        if(is_guide){
            aid.addAddresses(address);
        }
        
        m.setSender(sender.getAID());
        m.addReceiver(aid);
    
        m.setLanguage(Ontology.RDF);
        m.setOntology(Ontology.BASE);
        m.setContent(content);
        
        return m;
    }

    /**
     * This method creates ACL message containing of RDF of some Jena RDF
     * Resource
     *
     * @param sender Agent which sends message
     * @param receiverName string name of Agent which receive message
     * @param ACLtype type of ACL message
            * @param resource Jena resource
     * @return ACLMessage message
     *
     */
    public static ACLMessage createMessage(Agent sender, String receiverName, Integer ACLtype, Resource resource) {
        String content = resource2RDF(resource);
        ACLMessage m = new ACLMessage(ACLtype);
        m.setSender(sender.getAID());
        m.addReceiver(new AID(receiverName, false));
        m.setLanguage(Ontology.RDF);
        m.setOntology(Ontology.BASE);
        m.setContent(content);
        
        return m;
    }
    
    
    public static ACLMessage createMessage(Agent sender, List<String> receiverNames, Integer ACLtype, Resource resource) {
        String content = resource2RDF(resource);
        ACLMessage m = new ACLMessage(ACLtype);
        
        m.setSender(sender.getAID());
        m.setContent(content);
        m.setLanguage(Ontology.RDF);
        m.setOntology(Ontology.BASE);

        receiverNames.stream().forEach((receiverName) -> {
            AID receiver = new AID(receiverName, false);
            m.addReceiver(receiver);
        });

        return m;
    }
    

    /**
     * This method returns XML/RDF text representation of RDF Resource
     *
     * @param resource Jena Resource
     * @return String RDF
     */
    public static String resource2RDF(Resource resource) {
        return resource2RDF(resource, ModelFactory.createOntologyModel());
    }

    /**
     * This method returns XML/RDF text representation of RDF Model
     *
     * @param model Jena Model
     * @return String RDF
     */
        public static String model2RDF(Model model) {
        String rdf = "";
        try {
            StringWriter writer = new StringWriter();
            //model.write(writer, "RDF/XML-ABBREV");
            model.write(writer, "TURTLE");
            rdf = writer.getBuffer().toString();
        } catch (Exception e) {
            log.error("error adding instance to model");
        }
        return rdf;
    }

    /**
     * This method returns XML/RDF text representation of specified model
     *
     * @param resource Jena resource
     * @param model Jena model
     * @return String
     */
    public static String resource2RDF(Resource resource, Model model) {
        return model2RDF(resource2Model(resource, model));
    }

    /**
     * This method returns XML/RDF text representation of specified model
     *
     * @param resource Jena resource
     * @param model Jena model
     * @return Model
     */
    public static Model resource2Model(Resource resource, Model model) {
        try {

            model.add(resource.listProperties());

            StmtIterator i = resource.listProperties();
            while (i.hasNext()) {
                Statement s = i.nextStatement();
                if (s.getObject() instanceof Resource) {
//                    if(s.getObject().toString().contains("#dll-base-"))continue;
//                    log.info("res2mod " + s.getObject().toString());
                    model.add(((Resource) resource.getProperty(s.getPredicate()).getObject()).listProperties());
                }
            }

        } catch (Exception e) {
            log.error("Error adding instance to model");
        }
        
        return model;
    }

    /**
     * Recursive method which move resource with all properties recursively into
     * new model
     *
     * @param model Jena model
     * @param resource Jena resource
     */
    public static void addProperties(Model model, Resource resource) {
        model.add(resource.listProperties());
        StmtIterator properties = resource.listProperties();
        while (properties.hasNext()) {
            Statement statement = properties.nextStatement();
            try {
                model.add(statement.getResource().listProperties());
                addProperties(model, statement.getResource());
            } catch (Exception e) {
                log.info("Error adding properties");
            }
        }
    }

    /**
     * This method register agent with directory facilitator
     *
     * @param agent Agent which registers
     * @param agentType string description of agent type
     *
     */
    public static void register(Agent agent, String agentType) {
        DFAgentDescription dfd = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();

        sd.setType(agentType);
        sd.setName(agent.getName());

        sd.setOwnership(Ontology.AGENT_OWNERSHIP);
        sd.addOntologies(Ontology.BASE);
        dfd.setName(agent.getAID());
        dfd.addServices(sd);
        try {
            DFService.register(agent, dfd);
        } catch (FIPAException e) {
            log.error(agent.getLocalName() + " registration with DF unsucceeded. Reason: " + e.getMessage());
            agent.doDelete();
        }
    }

}
