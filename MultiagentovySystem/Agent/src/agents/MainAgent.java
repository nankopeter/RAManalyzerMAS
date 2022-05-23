/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package agents;

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
import jade.core.behaviours.SimpleBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
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
import java.util.HashSet;
import myMemory.Memory;
import myOntology.Ontology;
import org.apache.log4j.Logger;


public class MainAgent extends Agent {
    
    private static Logger log = Logger.getLogger(MainAgent.class.getName());
    private Memory mem, tempMem;
    HashSet<String> potencial_malware = new HashSet<String>();
    HashSet<String> hollowfind_suspicious = new HashSet<String>();
    HashSet<String> malfind_suspicious = new HashSet<String>();
    HashSet<String> fakename_suspicious = new HashSet<String>();
    HashSet<String> hiding_suspicious = new HashSet<String>();

    // Process properties
    
    
    private Property ProcessesProperty;
    private Property TitleProperty;
    private Property PidProperty;
    private Property process_nameProperty;
    private Property CreatedProperty;
    private Property PlatformProperty;
    private Property does;
    
    Map<String, String> aMap;
    private List<String> receivers,detects;
    
    // Helpers
    private TextHelper textHelper;
    private MemoryHelper memHelp;
    private DashBoardHelper dashboard;
    
    OntModel reasoningModel;
    
    // queues
    private LinkedList<SuspiciousM> suspiciousQueue = new LinkedList<SuspiciousM>();
    private LinkedList<HelperM> helperQueue = new LinkedList<HelperM>();
    private LinkedList<ACLMessage> messageQueue = new LinkedList<ACLMessage>();
    
    @Override
    protected void setup() {
        Object[] args = getArguments();
        aMap = new HashMap<String, String>();
        
        for(int i=0;i<args.length;i+=2){
            aMap.put(args[i].toString(), args[i+1].toString());
        }
        
        Message.register(this, "MainAgent");
        mem = new Memory("MainAgent", aMap, true);
        tempMem = new Memory("MainAgent", aMap, false);
        dashboard = new DashBoardHelper();
        textHelper = new TextHelper();
        memHelp = new MemoryHelper();
        
        ProcessesProperty = mem.getModel().getProperty(Memory.getBase() + "Processes"); 
        TitleProperty = mem.getModel().getProperty(Memory.getBase() + "title");
        PidProperty = mem.getModel().getProperty(Memory.getBase() + "pid");
        process_nameProperty = mem.getModel().getProperty(Memory.getBase() + "process_name");
        CreatedProperty = mem.getModel().getProperty(Memory.getBase()  + "created");
        PlatformProperty = mem.getModel().getProperty(Memory.getBase()  + "platform");
        does = mem.getModel().getProperty(Memory.getBase() + "does");
        
        // create reasoning model before behaviours
        reasoningModel = mem.getReasoningModel();
        
        receivers = new ArrayList<String>(Arrays.asList(mem.getAgentSensors().split(",")));
        detects = new ArrayList<String>(Arrays.asList(mem.getParentAgents().split(",")));
        
        BehaviourTimeQuerySending timequerysendingBehaviour = new BehaviourTimeQuerySending(this, mem.config.QUERY_INTERVAL);
        BehaviourHandleRecivedMessages hrmBehaviour = new BehaviourHandleRecivedMessages(this);
        MessageHandler msghandler = new MessageHandler(this, 500);
        ResultBehaviour resultBehaviour = new ResultBehaviour(this, mem.config.REASONING_INTERVAL);
        SuspiciousQueueHandler sqh = new SuspiciousQueueHandler(this, 500);
        
        addBehaviour(timequerysendingBehaviour);
        addBehaviour(resultBehaviour);
        addBehaviour(sqh);
        addBehaviour(msghandler);
        addBehaviour(hrmBehaviour);
    }
    
    @Override
    public void doDelete() {
        mem.memory2owl();  
        super.doDelete();
    }
    


    class SuspiciousQueueHandler extends TickerBehaviour{

        private SuspiciousM suspicious;
        public final OntModel m = mem.createEmptyModel();

        public SuspiciousQueueHandler(Agent a, long period) {
            super(a, period);
        }

        @Override
        protected void onTick() {

            for(int cycle_i = 0; cycle_i < 1000; cycle_i++){
                DateFormat dateFormat = new SimpleDateFormat("<yyyy-MM-dd HH:mm:ss> ");
                Date date = new Date();
                String formatedDate = dateFormat.format(date);

                if(suspiciousQueue.size() > 0){
                    m.removeAll();
                    suspicious = suspiciousQueue.getFirst();
                    suspiciousQueue.removeFirst();

                    if((malfind_suspicious.contains(suspicious.getR().toString().replace(mem.getBase(), "")) &&
                            fakename_suspicious.contains(suspicious.getR().toString().replace(mem.getBase(), "")) &&
                            hollowfind_suspicious.contains(suspicious.getR().toString().replace(mem.getBase(), "")) &&
                            hiding_suspicious.contains(suspicious.getR().toString().replace(mem.getBase(), ""))) ||
                            (malfind_suspicious.contains(suspicious.getR().toString().replace(mem.getBase(), "")) &&
                                    fakename_suspicious.contains(suspicious.getR().toString().replace(mem.getBase(), "")) &&
                                    hiding_suspicious.contains(suspicious.getR().toString().replace(mem.getBase(), ""))) ||
                            (malfind_suspicious.contains(suspicious.getR().toString().replace(mem.getBase(), "")) &&
                                    fakename_suspicious.contains(suspicious.getR().toString().replace(mem.getBase(), "")) &&
                                    hollowfind_suspicious.contains(suspicious.getR().toString().replace(mem.getBase(), ""))) ||
                            (malfind_suspicious.contains(suspicious.getR().toString().replace(mem.getBase(), "")) &&
                                    hollowfind_suspicious.contains(suspicious.getR().toString().replace(mem.getBase(), "")) &&
                                    hiding_suspicious.contains(suspicious.getR().toString().replace(mem.getBase(), ""))) ||
                            (fakename_suspicious.contains(suspicious.getR().toString().replace(mem.getBase(), "")) &&
                                    hollowfind_suspicious.contains(suspicious.getR().toString().replace(mem.getBase(), "")) &&
                                    hiding_suspicious.contains(suspicious.getR().toString().replace(mem.getBase(), ""))) ||
                            (fakename_suspicious.contains(suspicious.getR().toString().replace(mem.getBase(), "")) &&
                                    hollowfind_suspicious.contains(suspicious.getR().toString().replace(mem.getBase(), ""))) ||
                            (fakename_suspicious.contains(suspicious.getR().toString().replace(mem.getBase(), "")) &&
                                    hiding_suspicious.contains(suspicious.getR().toString().replace(mem.getBase(), ""))) ||
                            (hollowfind_suspicious.contains(suspicious.getR().toString().replace(mem.getBase(), "")) &&
                                    hiding_suspicious.contains(suspicious.getR().toString().replace(mem.getBase(), ""))) ||
                            (malfind_suspicious.contains(suspicious.getR().toString().replace(mem.getBase(), "")) &&
                                    fakename_suspicious.contains(suspicious.getR().toString().replace(mem.getBase(), ""))) ||
                            (malfind_suspicious.contains(suspicious.getR().toString().replace(mem.getBase(), "")) &&
                                    hiding_suspicious.contains(suspicious.getR().toString().replace(mem.getBase(), ""))) ||
                            (hiding_suspicious.contains(suspicious.getR().toString().replace(mem.getBase(), ""))) ||
                            (malfind_suspicious.contains(suspicious.getR().toString().replace(mem.getBase(), "")) &&
                                    hollowfind_suspicious.contains(suspicious.getR().toString().replace(mem.getBase(), "")))){


                        potencial_malware.add(suspicious.getR().toString().replace(mem.getBase(), ""));

                        try(FileWriter fw = new FileWriter("result.txt", true);
                            BufferedWriter bw = new BufferedWriter(fw);
                            PrintWriter out = new PrintWriter(bw))
                        {
                            out.println(formatedDate + "Possible: " + suspicious.getType() + " Id: " +
                                    suspicious.getR().toString().replace(mem.getBase(), ""));
                            dashboard.addSuspiciousMessage("MainAgent", suspicious.getType(),
                                    suspicious.getR().toString().replace(mem.getBase(), ""));
                        } catch (IOException e) {
                            log.info("Error while writing result to file: " + e.toString());
                        }

                    }

                    //log.info("Query processed ... remain: " + suspiciousQueue.size());



                }
                else break;
            }
        }
    }


    class ResultBehaviour extends TickerBehaviour{

        private Agent agent;
        private HelperM helper;
        int test = 0;

        public ResultBehaviour(Agent _agent, long period) {
            super(_agent, period);
            agent = _agent;
        }

        @Override
        protected void onTick() {

//            if(test < 1){
            if(true){

                test ++;
                log.info("Running main reasoning ...");

                //detects.forEach((detect) -> {
                for (String detect : detects){
                    if( (detect.equals("HollowfindSuspicious")) || (detect.equals("FakeNamedProcessesSuspicious")) || (detect.equals("MalfindSuspicious")) || (detect.equals("HidingProcessesSuspicious")) || (detect.equals("LdrModuleSuspicious")) ) {
                        String queryMSG = "PREFIX ont: <http://kristian.sranko/rekal_agents.owl#>" +
                                "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +
                                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
                                "SELECT ?x WHERE {?x rdf:type ont:" + detect + "}";
                        //                    log.info("DESTECT: --------------- " + detect);
                        Query query = QueryFactory.create(queryMSG);
                        QueryExecution qexec = QueryExecutionFactory.create(query, mem.getReasoningModel());


                        try {

                            ResultSet results = qexec.execSelect();

                            while (results.hasNext()) {
                                QuerySolution soln = results.nextSolution();
                                Resource x = soln.getResource("x");
                                helperQueue.add(new HelperM(x, detect));

                                if(helperQueue.size() > 0) {
                                    helper = helperQueue.getFirst();
                                    helperQueue.removeFirst();

                                    if (helper.getType().equals("HollowfindSuspicious")) {
                                        hollowfind_suspicious.add(helper.getR().toString().replace(mem.getBase(), ""));
                                    } else if (helper.getType().equals("FakeNamedProcessesSuspicious")) {
                                        fakename_suspicious.add(helper.getR().toString().replace(mem.getBase(), ""));
                                    } else if (helper.getType().equals("MalfindSuspicious")) {
                                        malfind_suspicious.add(helper.getR().toString().replace(mem.getBase(), ""));
                                    } else if (helper.getType().equals("HidingProcessesSuspicious")) {
                                        hiding_suspicious.add(helper.getR().toString().replace(mem.getBase(), ""));
                                    }

                                }

                                suspiciousQueue.add(new SuspiciousM(x, detect));

                            }
                        } finally {
                            qexec.close();
                        }


                    }

                }
            }
        }
    }

    
    class BehaviourTimeQuerySending extends TickerBehaviour{
        
        private Agent agent;
        int test = 0;

        public BehaviourTimeQuerySending(Agent _agent, long period) {
            super(_agent, period);
            agent = _agent;
        }

        @Override
        protected void onTick() {
            
//            if(test < 1){
            if(true){
                test ++;
                receivers.forEach((receiverName) -> {
                    send(Message.createQueryMessage(agent, receiverName, "SELECT ?x WHERE {?x rdf:type ont:" + textHelper.senderToEvent(receiverName) + "}"));
//                    dashboard.addMessage(agent.getLocalName(), ACLMessage.QUERY_REF, receiverName);
                });   
            }
        }
    }
    
    
     class MessageHandler extends TickerBehaviour{
        
        private ACLMessage msg;
        private String sender = "";
        private Boolean skip = false;
        
        public MessageHandler(Agent a, long period) {
            super(a, period);
        }

        @Override
        protected void onTick() {
            for(int cycle_i = 0; cycle_i < 1000; cycle_i++){
                if(messageQueue.size() > 0){
                    msg = messageQueue.getFirst();
                    messageQueue.removeFirst();
                    skip = false;
                    sender = msg.getSender().getLocalName();

                    try {
                        // if direct read to model
                        if(mem.config.DIRECT_SAVE){
                            mem.getModel().read(new StringReader(msg.getContent()), Ontology.BASE);
                        }
                        else{
                            tempMem.getModel().read(new StringReader(msg.getContent()), Ontology.BASE, "TURTLE");
                        }

                    } catch (Exception e) {
                        log.error("Read model failed: " + e.toString());
                        skip = true;
                    }

                    if(!mem.config.DIRECT_SAVE && !skip){

                        log.info("Inform processed ... remain: " + messageQueue.size());

                        Iterator iter = tempMem.getModel().listSubjectsWithProperty(does);

                        while (iter.hasNext()) {
                            Resource r = (Resource) iter.next();
                            
                            String item_type_helper;
                            String message_pid;
                            String resource_pid;
                            try{
                                item_type_helper = msg.getContent().substring(msg.getContent().indexOf("#proces-pid-") + 12);
                                message_pid = item_type_helper.substring(0, item_type_helper.indexOf("-process_name-"));
                                resource_pid = r.getURI().substring(r.getURI().indexOf("#proces-pid-") + 12, r.getURI().indexOf("-process_name-"));
                                if(!message_pid.equals(resource_pid))
                                    continue;
                            }catch(Exception e){
                                continue;
                            }
//                            
                            Resource r_proc = mem.getModel().getResource(Memory.getBase() + textHelper.senderToClass(sender) + "Processes");
                            memHelp.createProcessIndividual(mem.getModel(), r, r.getURI(), ProcessesProperty,  process_nameProperty, PidProperty, CreatedProperty, r_proc, does, sender);
                        }
                        mem.getReasoningModel().add(mem.getModel());

                        tempMem.getModel().removeAll();
                    }
                }
                else break;
            }
        }   
    }
    
    public static class BackUpBehaviour extends TickerBehaviour{
       
        private final Agent agent;
        private Memory mem = null;

        public BackUpBehaviour(Agent _a, long period, Memory _mem) {
            super(_a, period);
            agent = _a;
            mem = _mem;
        }

        @Override
        protected void onTick() {
            mem.memory2owl();
        }
    
    }
    
    class BehaviourHandleRecivedMessages extends SimpleBehaviour {
        
        private boolean finished = false;
        private Agent agent;    
        private boolean first = true;
        
        String sender;
        OntClass senderClass;
        
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
            
            for(int cycle_i = 0; cycle_i < 1000; cycle_i++){
                synchronized (this) {
                    // receive message
                    ACLMessage msg = receive();

                    if (msg != null) {
                        switch (msg.getPerformative()) {
                            // message from agents 
                            case ACLMessage.INFORM:
                                if (msg.getLanguage().equals(Ontology.RDF)) {
                                    messageQueue.add(msg);
//                                    if(msg.getSender().getLocalName() == "ExitedProcessesAgent")
//                                    log.info(msg.toString());
//                                    log.info("new inform msg from *****************" + msg.getSender().getLocalName());
//                                    dashboard.addNewMessage(agent.getLocalName(), ACLMessage.INFORM, msg.getSender().getLocalName());  
                                }
                            break;    
                        }
                    }
                    else break;
                }
            }
        }

        @Override
        public boolean done() {
            return finished;
        }
        
    }
    
    /**
     * Class helping storing query messages in memory
     */
    static class QueryM{
        private Agent sender;
        private String receiver;
        private Resource r;

        public QueryM(Agent sender, String receiver, Resource r) {
            this.sender = sender;
            this.receiver = receiver;
            this.r = r;
        }

        public Agent getSender() {
            return sender;
        }

        public String getReceiver() {
            return receiver;
        }

        public Resource getR() {
            return r;
        }

        public void setSender(Agent sender) {
            this.sender = sender;
        }

        public void setReceiver(String receiver) {
            this.receiver = receiver;
        }

        public void setR(Resource r) {
            this.r = r;
        }
       
    }
    
    static class SuspiciousM{
        private String type;
        private Resource r;

        public SuspiciousM(Resource r, String type) {
            this.r = r;
            this.type = type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public void setR(Resource r) {
            this.r = r;
        }

        public String getType() {
            return type;
        }

        public Resource getR() {
            return r;
        }
    }

    static class HelperM{
        private String type;
        private Resource r;

        public HelperM(Resource r, String type) {
            this.r = r;
            this.type = type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public void setR(Resource r) {
            this.r = r;
        }

        public String getType() {
            return type;
        }

        public Resource getR() {
            return r;
        }
    }

}
