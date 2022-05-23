/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package agents;

import agents.MainAgent.BackUpBehaviour;
import agents.MainAgent.QueryM;
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
import com.hp.hpl.jena.rdf.model.Resource;
import myCommunication.Message;
import myHelpers.DashBoardHelper;
import myHelpers.MemoryHelper;
import myHelpers.TextHelper;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import java.io.StringReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import myMemory.Memory;
import myOntology.Ontology;
import org.apache.log4j.Logger;

/**
 *
 * @author patrik.matiasko
 */
public class DosAgent extends Agent {
    
    private static Logger log = Logger.getLogger(DosAgent.class.getName());
    private Memory mem, tempMem;
   
    
    // Properties
    private Property ProcessesProperty;
    private Property TitleProperty;
    private Property PidProperty;
    private Property CreatedProperty;
    private Property PlatformProperty;
    private Property does;
    
    private List<String> sensors;
    
    // Helpers
    private TextHelper textHelper;
    private MemoryHelper memHelp;
    private DashBoardHelper dashboard;
    
    public Integer individualNumber = 0;
    
    private LinkedList<ACLMessage> messageQueue = new LinkedList<ACLMessage>();
    private LinkedList<QueryM> queryMessageQueue = new LinkedList<QueryM>();
    
    private OntClass agentClass;
    
    OntModel reasoningModel;
    
    Map<String, String> aMap;
    
    @Override
    protected void setup() {
        Object[] args = getArguments();
        aMap = new HashMap<String, String>();
        
        for(int i=0;i<args.length;i+=2){
            aMap.put(args[i].toString(), args[i+1].toString());
        }
        
        dashboard = new DashBoardHelper();
        
        Message.register(this, "DosAgent");
        mem = new Memory("DosAgent", aMap, true);
        tempMem = new Memory("DosAgent", aMap, false);
        textHelper = new TextHelper();
        memHelp = new MemoryHelper();
        
        // Properties init
        ProcessesProperty = mem.getModel().getProperty(Memory.getBase() + "Processes");      
        TitleProperty = mem.getModel().getProperty(Memory.getBase() + "title");
        PidProperty = mem.getModel().getProperty(Memory.getBase() + "pid");
        CreatedProperty = mem.getModel().getProperty(Memory.getBase()  + "created");
        PlatformProperty = mem.getModel().getProperty(Memory.getBase()  + "platform");
        does = mem.getModel().getProperty(Memory.getBase() + "does");
        
        // create reasoning model before behaviours
        reasoningModel = mem.getReasoningModel();
    
        sensors = new ArrayList<String>(Arrays.asList(mem.getAgentSensors().split(",")));
        
        agentClass = mem.getModel().getOntClass(Memory.getBase() + textHelper.senderToClass(this.getLocalName()));
       
        BehaviourHandleRecivedMessages hrmBehaviour = new BehaviourHandleRecivedMessages(this);
        BackUpBehaviour backupbehaviour = new BackUpBehaviour(this, 300000, mem); //5min
        MessageHandler messageHandler = new MessageHandler(this, 1000);
        QueryMessageHandler queryMessageHandler = new QueryMessageHandler(this, 1600);
        Test testt = new Test(this, 40000);
        
        addBehaviour(hrmBehaviour);
        addBehaviour(messageHandler);
        addBehaviour(queryMessageHandler);
//        addBehaviour(backupbehaviour);
//        addBehaviour(testt);
    }

    @Override
    public void doDelete() {
        mem.memory2owl();  
        super.doDelete();
    }
    
    
    private void informAskAgent(String receiver, Resource resource) {
        this.addBehaviour(new BehaviourSendResult(this, receiver, resource));
    }
    
    class BehaviourSendResult extends OneShotBehaviour {

        private String receiver = null;
        private Resource resource = null;
        private Agent agent;

        public BehaviourSendResult(Agent _agent, String _receiver, Resource _resource) {
            super(_agent);
            agent = _agent;
            receiver = _receiver;
            resource = _resource;
        }

        @Override
        public void action() {
            send(Message.createInformMessage(agent, receiver, resource));
            dashboard.addMessage(agent.getLocalName(), ACLMessage.QUERY_REF, receiver);
        }

    }
    
    class Test extends TickerBehaviour{
        
        int temp = 0;
        Resource resource;

        public Test(Agent a, long period) {
            super(a, period);
        }

        @Override
        protected void onTick() {
          
        }
            
    }

    
    class QueryMessageHandler extends TickerBehaviour{
        
        private QueryM queryM;
        public final OntModel m = mem.createEmptyModel();
        
        public QueryMessageHandler(Agent a, long period) {
            super(a, period);
        }

        @Override
        protected void onTick() {
            if(queryMessageQueue.size() > 0){
                m.removeAll();
                queryM = queryMessageQueue.getFirst();
                queryMessageQueue.removeFirst();
                
                log.info("Query processed ... remain: " + queryMessageQueue.size());
               
                send(Message.createInformMessage(queryM.getSender(), queryM.getReceiver(), queryM.getR()));
                
                dashboard.addMessage(queryM.getSender().getLocalName(), ACLMessage.QUERY_REF, queryM.getReceiver());
            }
        }
    }
    
    class MessageHandler extends TickerBehaviour{
        
        private ACLMessage msg;
        private String sender = "";
        private Boolean tmptest = false;
        
        public MessageHandler(Agent a, long period) {
            super(a, period);
        }

        @Override
        protected void onTick() {
            
            if(messageQueue.size() > 0){
                msg = messageQueue.getFirst();
                messageQueue.removeFirst();
                
                sender = msg.getSender().getLocalName();
                
                try {
                    // if direct read to model
                    if(mem.config.DIRECT_SAVE){
                        mem.getModel().read(new StringReader(msg.getContent()), Ontology.BASE);
                    }
                    else{
                        tempMem.getModel().read(new StringReader(msg.getContent()), Ontology.BASE);
                    }
                    
                } catch (Exception e) {
                    log.error("Read model failed: " + e.toString());
                }
                
                if(!mem.config.DIRECT_SAVE){
                    
                    log.info("Inform processed ... remain: " + messageQueue.size());
                
                    Iterator iter = tempMem.getModel().listSubjectsWithProperty(does);

                    while (iter.hasNext()) {
                        Resource r = (Resource) iter.next();
                        OntClass senderClass = mem.getModel().getOntClass(Memory.getBase() + textHelper.senderToClass(sender));
                        
                        Property SaveProperty;
                        try{
                            SaveProperty = mem.getModel().createProperty(Memory.getBase() + textHelper.clearProperty(r.getProperty(PlatformProperty).getObject().toString()) + "Processes");
                        }catch(Exception e ){
                            break;
                        }
                        
                        if(memHelp.checkResources(mem.getModel(), SaveProperty, r.getURI())){
                            Individual exist = mem.getModel().getIndividual(r.getURI());

                            if(!memHelp.individualHasProperty(does, exist, senderClass)){
                                exist.addProperty(does , senderClass);
                            }

                        }
                        else{
                            memHelp.createIndividual(mem.getModel(), r, r.getURI(), SaveProperty, TitleProperty, PidProperty, CreatedProperty, PlatformProperty, does, senderClass);
                            // add by Kris
                            dashboard.addSuspiciousMessage(sender, ProcessesProperty.toString(), senderClass.toString());
                        }
                        
//                        try{
//                            iter.hasNext();
//                        }catch(Exception e ){
//                            break;
//                        }
                    }
                    mem.getReasoningModel().add(mem.getModel());

                    tempMem.getModel().removeAll();
                }
            }
        }
    }
    
    class BehaviourHandleRecivedMessages extends SimpleBehaviour {
        
        private boolean finished = false;
        private Agent agent;
        
        private int testCounter = 0;
        
        private List<Resource> res = null;
        
        private boolean first = true;
        
        private QueryM queryM;
        
        public BehaviourHandleRecivedMessages(Agent _a) {
            super(_a);
            agent = _a;
        }

        @Override
        public void action() {
            
            if(first){
                first = false;
                dashboard.changeStatus(this.getAgent().getLocalName(), "done");
                log.info(this.getAgent().getLocalName() + " is ready!");
            }
             
            synchronized (this) {
                // receive message
                ACLMessage msg = receive();
                
                if (msg != null) {
                    
                    //log.info("new msg");
                    
                    switch (msg.getPerformative()) {
                        case ACLMessage.QUERY_REF:
                            
                            // if message is sparql query
                            if (msg.getLanguage().equals(Ontology.SPARQL)) {
                                    
                                Query query = QueryFactory.create(msg.getContent());
                                QueryExecution qexec = QueryExecutionFactory.create(query, mem.getReasoningModel());
                                    
                                try {
                                    ResultSet results = qexec.execSelect();

//                                    for (; results.hasNext();) {
                                    while (results.hasNext()) {    
                                        
                                        QuerySolution soln = results.nextSolution();
                                        Resource x = soln.getResource("x");
                                        
                                        queryMessageQueue.add(new QueryM(agent, msg.getSender().getLocalName(), x));
                                    }
                                    
                                    log.info("done iteration");

                                } finally {
                                    qexec.close();
                                }
                            }
                            
                        dashboard.addNewMessage(agent.getLocalName(), ACLMessage.QUERY_REF, msg.getSender().getLocalName());
                        break;
                        
                        // message from sensors
                        case ACLMessage.PROPOSE:
                            if (msg.getLanguage().equals(Ontology.RDF)) {
                                String sender = msg.getSender().getName();

                                if(sensors.contains(sender)){
                                    messageQueue.add(msg);
                                    log.info("TC:" + testCounter);
                                    testCounter++;
                                    
                                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    DateFormat dateSendFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
                                    Date date = new Date();
                                    String formatedDate = dateFormat.format(date);
                                    String formatedSendDate = dateSendFormat.format(date);
                                    
                                    int i = 5;
                                    Individual individual;
                
                                    individual = mem.getModel().createIndividual(textHelper.createIndividualName(Memory.getBase(), Integer.toString(i), formatedSendDate, mem.config.PLATFORM_ID), ProcessesProperty);
                                    individual.addProperty(PidProperty, mem.getModel().createTypedLiteral("proces-" + i));
                                    individual.addProperty(TitleProperty, mem.getModel().createTypedLiteral("title-" + i));
                                    individual.addProperty(CreatedProperty, mem.getModel().createTypedLiteral(formatedDate));
                                    individual.addProperty(PlatformProperty, mem.getModel().createTypedLiteral(this.agent.getLocalName()));
                                    individual.addProperty(does, mem.getModel().getOntClass(Memory.getBase() + textHelper.senderToClass(this.agent.getLocalName())));
                                    mem.getReasoningModel().add(mem.getModel());
                                }
                                else{
                                    log.info(" - I didn't accept this sensor: " + sender);
                                    log.info(" - I accenpt only : " + sensors.toString());
                                }
                            }
                            
                        dashboard.addNewMessage(agent.getLocalName(), ACLMessage.PROPOSE, msg.getSender().getLocalName());    
                        break;
                    }
                }
                
                // if we want to use others behaviours
                //block();
            }
        }

        @Override
        public boolean done() {
            return finished;
        }
        
      
    }
    
} 