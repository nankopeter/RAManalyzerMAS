/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package agents;

import agents.MainAgent.BackUpBehaviour;
import agents.MainAgent.QueryM;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import myMemory.Memory;
import myOntology.Ontology;
import org.apache.log4j.Logger;


public class UniversalAgent extends Agent {
    
    private static Logger log = Logger.getLogger(UniversalAgent.class.getName());
    private Memory mem, tempMem;
    private OntModel temp_ontmodel, mem_helper;
    
    String module_name;
   
    
    // Properties
    private Property TitleProperty;
    private Property PlatformProperty;
    
    private Property thisProperty;
//    private Property thisObjectProperty;
    
    // Process properties
    private Property ProcessesProperty;
    private Property PidProperty;
    private Property process_nameProperty;
    private Property CreatedProperty;
    private Property does;
    
    // Process properties
    private Property DLLsProperty;
    private Property BaseProperty;
    private Property PathProperty;
    private Property LoadProperty;
    private Property InitProperty;
    private Property MemProperty;
    private Property dllOf;
    
    // Property properties
    private Property PropertiesProperty;
    private Property AddressProperty;
    private Property VadTagProperty;
    private Property ProtectionProperty;
    private Property PrivateProperty;
    private Property process_path_pebPropoerty;
    private Property addressOf;

        
    // Function properties
    private Property FunctionProperty;
    //private Property NameProperty;
    private Property functionOf;
    private Property callsFunction;
        
    // Driver properties
    private Property DriverProperty;
    private Property DriverName;
    private Property CallbackDetail;
    private Property SizeProperty;
    private Property FunctionNameProperty;
    private Property SyscallNameProperty;
    private Property HookMode;
    private Property HookType;
    private Property VictimModule;
    private Property UsedFunction;
    private Property CPU;
    private Property IdtIndex;
    private Property IrpFunction;
    private Property DriverPath;
    private Property CallbackType;
    private Property DueTime;
    private Property Period;
    private Property Signaled;


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
    
    String local_name;
    
    @Override
    protected void setup() {
        Object[] args = getArguments();
        aMap = new HashMap<String, String>();
        
        for(int i=0;i<args.length;i+=2){
            aMap.put(args[i].toString(), args[i+1].toString());
        }
        
        dashboard = new DashBoardHelper();
        
        local_name = this.getLocalName();
        
        
        Message.register(this, local_name);
        mem = new Memory(local_name, aMap, true);
        tempMem = new Memory(local_name, aMap, false);
        textHelper = new TextHelper();
        memHelp = new MemoryHelper();
        
        mem_helper = mem.getModel();
        
        module_name = this.getLocalName().replace("Agent", "");
        
        // Properties init
        //        TitleProperty = mem.getModel().getProperty(Memory.getBase() + "title");
        //        PlatformProperty = mem.getModel().getProperty(Memory.getBase()  + "platform");
        ProcessesProperty = mem.getModel().getProperty(Memory.getBase() + "Processes");      
        PidProperty = mem.getModel().getProperty(Memory.getBase() + "pid");
        process_nameProperty = mem.getModel().getProperty(Memory.getBase() + "process_name");
        CreatedProperty = mem.getModel().getProperty(Memory.getBase()  + "created");
        does = mem.getModel().getProperty(Memory.getBase() + "does");
        
        DLLsProperty = mem.getModel().getProperty(Memory.getBase() + "DLLs"); 
        BaseProperty = mem.getModel().getProperty(Memory.getBase() + "base"); 
        PathProperty = mem.getModel().getProperty(Memory.getBase() + "path"); 
        LoadProperty = mem.getModel().getProperty(Memory.getBase() + "load"); 
        InitProperty = mem.getModel().getProperty(Memory.getBase() + "init"); 
        MemProperty = mem.getModel().getProperty(Memory.getBase() + "mem"); 
        dllOf = mem.getModel().getProperty(Memory.getBase() + "dllOf");
        
        PropertiesProperty = mem.getModel().getProperty(Memory.getBase() + "Properties");
        AddressProperty = mem.getModel().getProperty(Memory.getBase() + "address");
        VadTagProperty = mem.getModel().getProperty(Memory.getBase() + "vadTag");
        ProtectionProperty = mem.getModel().getProperty(Memory.getBase() + "protection");
        PrivateProperty = mem.getModel().getProperty(Memory.getBase() + "private");
        process_path_pebPropoerty = mem.getModel().getProperty(Memory.getBase() + "process_path_peb");
        addressOf = mem.getModel().getProperty(Memory.getBase() + "addressOf");
        
        FunctionProperty = mem.getModel().getProperty(Memory.getBase() + "Function");
        //NameProperty = mem.getModel().getProperty(Memory.getBase() + "name");
        functionOf = mem.getModel().getProperty(Memory.getBase() + "functionOf");
        callsFunction = mem.getModel().getProperty(Memory.getBase() + "callsFunction");

        DriverProperty = mem.getModel().getProperty(Memory.getBase() + "Drivers");
        DriverName = mem.getModel().getProperty(Memory.getBase() + "DriverName");
        SyscallNameProperty = mem.getModel().getProperty(Memory.getBase() + "SyscallName");
        HookMode = mem.getModel().getProperty(Memory.getBase() + "HookMode");
        HookType = mem.getModel().getProperty(Memory.getBase() + "HookType");
        VictimModule = mem.getModel().getProperty(Memory.getBase() + "VictimModule");
        UsedFunction = mem.getModel().getProperty(Memory.getBase() + "InlineKernelHookFunction");
        CPU = mem.getModel().getProperty(Memory.getBase() + "CPU");
        IdtIndex = mem.getModel().getProperty(Memory.getBase() + "IdtIndex");
        IrpFunction = mem.getModel().getProperty(Memory.getBase() + "IrpFunction");
        DriverPath = mem.getModel().getProperty(Memory.getBase() + "DriverPath");
        CallbackType = mem.getModel().getProperty(Memory.getBase() + "CallbackType");
        CallbackDetail = mem.getModel().getProperty(Memory.getBase() + "CallbackDetail");
        DueTime = mem.getModel().getProperty(Memory.getBase() + "DueTime");
        Period = mem.getModel().getProperty(Memory.getBase() + "Period");
        Signaled = mem.getModel().getProperty(Memory.getBase() + "Signaled");

        SizeProperty = mem.getModel().getProperty(Memory.getBase() + "size");
        FunctionNameProperty = mem.getModel().getProperty(Memory.getBase() + "functionName");
        
        // create reasoning model before behaviours
        reasoningModel = mem.getReasoningModel();
    
        sensors = new ArrayList<String>(Arrays.asList(mem.getAgentSensors().split(",")));
        
        agentClass = mem.getModel().getOntClass(Memory.getBase() + textHelper.senderToClass(this.getLocalName()));
       
        BehaviourHandleRecivedMessages hrmBehaviour = new BehaviourHandleRecivedMessages(this);
        BackUpBehaviour backupbehaviour = new BackUpBehaviour(this, 300000, mem); //5min
        MessageHandler messageHandler = new MessageHandler(this, 5000);
        QueryMessageHandler queryMessageHandler = new QueryMessageHandler(this, 2000);
//        Test testt = new Test(this, 40000);
        
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

            for(int cycle_i = 0; cycle_i < 1000; cycle_i++){
                if(queryMessageQueue.size() > 0){
                    m.removeAll();
                    queryM = queryMessageQueue.getFirst();
                    queryMessageQueue.removeFirst();

//                    log.info("Query processed ... remain: " + queryMessageQueue.size());
                    log.info("Query processed ...: " + queryM.getR().toString());
                    send(Message.createInformMessage(queryM.getSender(), queryM.getReceiver(), queryM.getR()));

    //                dashboard.addMessage(queryM.getSender().getLocalName(), ACLMessage.QUERY_REF, queryM.getReceiver());
                }
                else break;
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
            temp_ontmodel = mem_helper;
            for(int cycle_i = 0; cycle_i < 100; cycle_i++){
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
                            //tempMem.getModel().read(new StringReader(msg.getContent()), Ontology.BASE, "TURTLE");
                            temp_ontmodel.read(new StringReader(msg.getContent()), Ontology.BASE, "TURTLE");
                        }

                    } catch (Exception e) {
                        log.error("Read model failed: " + e.toString());
                    }

                    if(!mem.config.DIRECT_SAVE){

//                        log.info("Inform processed ... remain: " + messageQueue.size());
                        Iterator iter;
                        String item_type_helper;
                        String item_type;
                        try{
                            //problem
                            //item_type_helper = msg.getContent().substring(msg.getContent().indexOf("<j.0:") + 5);
                            item_type_helper = msg.getContent().substring(msg.getContent().indexOf("<#P"));
                            item_type = item_type_helper.substring(2, item_type_helper.indexOf('>'));


                        }catch(Exception e){
                            continue;
                        }

                        switch(module_name){
                            case "Callbacks":

                                break;
                            case "Pslist":
                            case "Psscan":
                            case "Pstree":
                            case "ExitedProcesses":
                            case "HidingProcesses":
                            case "FakeNamedProcesses":
                                if(item_type.equals("Processes")){
//                                    iter = tempMem.getModel().listSubjectsWithProperty(PidProperty);
                                    iter = temp_ontmodel.listSubjectsWithProperty(PidProperty);
                                    while(iter.hasNext()) {
                                        Resource r = (Resource) iter.next();
                                        
                                        Resource r_proc = temp_ontmodel.getResource(Memory.getBase() + textHelper.senderToClass(sender) + "Processes");
                                        memHelp.createProcessIndividual(temp_ontmodel, r, r.getURI(), ProcessesProperty, process_nameProperty, PidProperty, CreatedProperty, r_proc, does, sender);
//                                        log.info("*2* Creating: " + r.getURI());
                                    }
                                }
                                break;

                            case "LdrModule":

                                String dll_helper = msg.getContent();
                                if( dll_helper.contains("#DLLs") ){
                                    item_type_helper = msg.getContent().substring(msg.getContent().indexOf("<#DLLs"));
                                    item_type = item_type_helper.substring(2, item_type_helper.indexOf('>'));
                                }

                                if(item_type.equals("Processes")){
                                    iter = temp_ontmodel.listSubjectsWithProperty(PidProperty);
                                    while(iter.hasNext()) {
                                        Resource r = (Resource) iter.next();
//                                        log.info("********** Creating Resource URI: " + r.getURI());
                                        Resource r_proc = temp_ontmodel.getResource(Memory.getBase() + textHelper.senderToClass(sender) + "Processes");
                                        memHelp.createProcessIndividual(temp_ontmodel, r, r.getURI(), ProcessesProperty, process_nameProperty, PidProperty, CreatedProperty, r_proc, does, sender);
                                    }
                                }
                                if(item_type.equals("DLLs")){
                                    iter = temp_ontmodel.listSubjectsWithProperty(dllOf);
//                                    while (iter.hasNext()) {
//                                        Resource r = (Resource) iter.next();
////                                        log.info("*Resource URI: " + r.getURI());
////                                        StmtIterator iter_s = r.listProperties(dllOf);
////                                        while (iter_s.hasNext()) {
////                                            Statement state = (Statement) iter_s.next();
////                                            log.info("**dllOf: " + state.getObject().toString());
////                                        }
//                                        
//                                        Resource r_dll = mem.getModel().getResource(Memory.getBase() + textHelper.senderToClass(sender) + "DLLs");
//                                        memHelp.createDLLIndividual(mem.getModel(), r, r.getURI(), DLLsProperty, BaseProperty, LoadProperty, InitProperty, MemProperty, PathProperty, dllOf, r_dll);
//                                    }
                                    while (iter.hasNext()) {
                                        Resource r = (Resource) iter.next();
//                                        log.info("*Resource URI: " + r.getURI());
//                                        StmtIterator iter_s = r.listProperties(dllOf);
//                                        while (iter_s.hasNext()) {
//                                            Statement state = (Statement) iter_s.next();
//                                            log.info("**dllOf: " + state.getObject().toString());
//                                        }
                                        
                                        Resource r_dll = temp_ontmodel.getResource(Memory.getBase() + textHelper.senderToClass(sender) + "DLLs");
                                        memHelp.createDLLIndividual(temp_ontmodel, r, r.getURI(), DLLsProperty, BaseProperty, LoadProperty, InitProperty, MemProperty, PathProperty, dllOf, r_dll);
                                    }
                                    
                                }
                                
                                break;

                            case "Malfind":
//                                String helper = msg.getContent();
//                                if( helper.contains("#Properties") ){
//                                    item_type_helper = msg.getContent().substring(msg.getContent().indexOf("<#Properties"));
//                                    item_type = item_type_helper.substring(2, item_type_helper.indexOf('>'));
//                                }

                                if(item_type.equals("Processes")){
                                    iter = temp_ontmodel.listSubjectsWithProperty(PidProperty);
                                    while(iter.hasNext()) {
                                        Resource r = (Resource) iter.next();
                                        
                                        Resource r_proc = temp_ontmodel.getResource(Memory.getBase() + textHelper.senderToClass(sender) + "Processes");
                                        memHelp.createProcessIndividual(temp_ontmodel, r, r.getURI(), ProcessesProperty, process_nameProperty, PidProperty, CreatedProperty, r_proc, does, sender);
                                    }
                                }
                                if(item_type.equals("Properties")){
                                    iter = temp_ontmodel.listSubjectsWithProperty(addressOf);
                                    while (iter.hasNext()) {
                                        Resource r = (Resource) iter.next();
                                        
                                        Resource r_prop = temp_ontmodel.getResource(Memory.getBase() + textHelper.senderToClass(sender) + "Properties");
                                        memHelp.createPropertyIndividual(temp_ontmodel, r, r.getURI(), PropertiesProperty, AddressProperty, VadTagProperty, ProtectionProperty, PrivateProperty, addressOf, r_prop);
                                    }
                                }
                                break;


                            case "Hollowfind":

                                String properties_helper = msg.getContent();
                                if( properties_helper.contains("#Properties") ){
                                    item_type_helper = msg.getContent().substring(msg.getContent().indexOf("<#Properties"));
                                    item_type = item_type_helper.substring(2, item_type_helper.indexOf('>'));
                                }

                                if(item_type.equals("Processes")){
                                    iter = temp_ontmodel.listSubjectsWithProperty(PidProperty);
                                    while(iter.hasNext()) {
                                        Resource r = (Resource) iter.next();

                                        Resource r_proc = temp_ontmodel.getResource(Memory.getBase() + textHelper.senderToClass(sender) + "Processes");
                                        memHelp.createProcessIndividual(temp_ontmodel, r, r.getURI(), ProcessesProperty, process_nameProperty, PidProperty, CreatedProperty, r_proc, does, sender);
                                    }
                                }
                                if(item_type.equals("Properties")){
                                    iter = temp_ontmodel.listSubjectsWithProperty(addressOf);
                                    while (iter.hasNext()) {
                                        Resource r = (Resource) iter.next();

                                        Resource r_prop = temp_ontmodel.getResource(Memory.getBase() + textHelper.senderToClass(sender) + "Properties");
                                        //memHelp.createPropertyIndividual(temp_ontmodel, r, r.getURI(), PropertiesProperty, AddressProperty, NameProperty, VadTagProperty, ProtectionProperty, addressOf, r_prop);
                                        memHelp.createPropertyIndividual2(temp_ontmodel, r, r.getURI(), PropertiesProperty, AddressProperty, VadTagProperty, addressOf, r_prop);
                                    }
                                }
                                break;




                            default:        log.info("NOT CORRECT module: " + module_name);
                        }
//                        Date d1 = new Date();
//                        mem.getReasoningModel().add(mem.getModel());
//                        Date d2 = new Date();
//                        System.out.println("Time: " + (d2.getTime() - d1.getTime()));

//                        tempMem.getModel().removeAll();
                    }
                }
                else break;
            }
            mem.getReasoningModel().add(temp_ontmodel);

        }
    }
    
    class BehaviourHandleRecivedMessages extends SimpleBehaviour {
        
        private boolean finished = false;
        private Agent agent;
        
//        private int testCounter = 0;
        
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
            int testCounter = 0;
            for(int cycle_i = 0; cycle_i < 10000; cycle_i++){
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
                                        
                                        while (results.hasNext()) {
                                            
                                            QuerySolution soln = results.nextSolution();
                                            Resource x = soln.getResource("x");
                                            log.info("resource to send*****************" + x.toString());

                                            queryMessageQueue.add(new QueryM(agent, msg.getSender().getLocalName(), x));
                                        }

                                        log.info("done iteration");

                                    } finally {
                                        qexec.close();
                                    }
                                }

                            //dashboard.addNewMessage(agent.getLocalName(), ACLMessage.QUERY_REF, msg.getSender().getLocalName());
                                break;

                            // message from sensors
                            case ACLMessage.PROPOSE:
                                if (msg.getLanguage().equals(Ontology.RDF)) {
                                    String sender = msg.getSender().getName();

                                    if(sensors.contains(sender)){
                                        messageQueue.add(msg);
//                                        log.info("********** Recieved Resource URI:" + msg.getContent());
//                                        log.info("TC:" + testCounter);
                                        testCounter++;

                                    }
                                    else{
                                        log.info(" - I didn't accept this sensor: " + sender);
                                        log.info(" - I accept only : " + sensors.toString());
                                    }
                                }

    //                            dashboard.addNewMessage(agent.getLocalName(), ACLMessage.PROPOSE, msg.getSender().getLocalName());    
                                break;
                        }
                    }
                    else break;

                    // if we want to use others behaviours
                    block();
                }
            }
        }

        @Override
        public boolean done() {
            return finished;
        }
        
      
    }
    
} 