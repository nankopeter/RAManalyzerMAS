/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sensors;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import myCommunication.Message;
import myHelpers.DashBoardHelper;
import myHelpers.TextHelper;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import myMemory.Memory;
import org.apache.log4j.Logger;


public class PslistSensor extends Agent {

    private static Logger log = Logger.getLogger(PslistSensor.class.getName());
    private OntModel model;

    private Memory mem;

    // Process properties
    private Property ProcessesProperty;
    private Property PidProperty;
    private Property process_nameProperty;
    private Property CreatedProperty;
    private Property memberOf;
    
    // Process properties
    private Property DLLsProperty;
    private Property BaseProperty;
    private Property PathProperty;
    private Property LoadProperty;
    private Property InitProperty;
    private Property MemProperty;
    private Property dllOf;
    
    OntClass ProcessesProperty_new;
    
    // helpers
    private DashBoardHelper dashboard;
    private TextHelper textHelper;
    
    Map<String, String> aMap;
    
    @Override
    protected void setup() {
        Object[] args = getArguments();
        aMap = new HashMap<String, String>();
        
        for(int i=0;i<args.length;i+=2){
            aMap.put(args[i].toString(), args[i+1].toString());
        }
        log.info("*************************THIS IS THE NAME: " + this.getLocalName());
        Message.register(this, this.getLocalName());
        
        dashboard = new DashBoardHelper();
        textHelper = new TextHelper();
        
        mem = new Memory(this.getLocalName(), aMap, false);
        model = mem.getModel();
        
        String module_name = this.getLocalName().replace("Sensor", "");

        ProcessesProperty_new = model.getOntClass(Memory.getBase() + "Processes");
        ProcessesProperty = mem.getModel().getProperty(Memory.getBase() + "Processes");      
        PidProperty = mem.getModel().getProperty(Memory.getBase() + "pid");
        process_nameProperty = mem.getModel().getProperty(Memory.getBase() + "process_name");
        CreatedProperty = mem.getModel().getProperty(Memory.getBase()  + "created");
        memberOf = mem.getModel().getProperty(Memory.getBase() + "memberOf");
        
        DLLsProperty = mem.getModel().getProperty(Memory.getBase() + "DLLs"); 
        BaseProperty = mem.getModel().getProperty(Memory.getBase() + "base"); 
        PathProperty = mem.getModel().getProperty(Memory.getBase() + "path"); 
        LoadProperty = mem.getModel().getProperty(Memory.getBase() + "load"); 
        InitProperty = mem.getModel().getProperty(Memory.getBase() + "init"); 
        MemProperty = mem.getModel().getProperty(Memory.getBase() + "mem"); 
        dllOf = mem.getModel().getProperty(Memory.getBase() + "dllOf"); 
       
        BehaviourSendToAgent sendToAgentBehaviour = new BehaviourSendToAgent(this);
        addBehaviour(sendToAgentBehaviour);
        

    }

    private void informParentAgents(Resource resource) {
        this.addBehaviour(new BehaviourSendNewData(this, resource));
    }

    /**
     * Behaviour is used to send data resource to parent agents
     */
    class BehaviourSendNewData extends OneShotBehaviour {

        private Resource resoruce = null;
        private Agent agent;
        private List<String> receivers;

        public BehaviourSendNewData(Agent _agent, Resource _resoruce) {
          
            super(_agent);
            agent = _agent;
            resoruce = _resoruce;
           
            receivers = new ArrayList<String>(Arrays.asList(mem.getParentAgents().split(",")));
           
        }

        @Override
        public void action() {
           
            if(!receivers.isEmpty()){
                receivers.forEach((receiverName) -> {
                    send(Message.createProposeMessage(agent, receiverName, resoruce, mem.config.IS_GUIDE, mem.config.PARENT_AGENTS_ADDRESS));
                    //dashboard.addMessage(agent.getLocalName(), ACLMessage.PROPOSE, receiverName); //commented by Kris
//                    log.info("-sending: " + resoruce.getProperty(PidProperty));
                });
            }
            else{
                log.error("Sensor: " + agent.getLocalName() + " havent parents agent");
            }
            
        }
    

    }

    /**
     * Behaviour is used to do sensor stuff
     */
    class BehaviourSendToAgent extends SimpleBehaviour {

        private boolean finished = false;
        private Agent Agent;

        private int test = 0;
        private int counter = 0;
        private boolean first = true;
        
        String date = "";
        String newDate = "";

        SimpleDateFormat fromUser = new SimpleDateFormat("MMM d HH:mm:ss yyyy");
        SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        List<String> pids = new ArrayList<String>();
        List<String> dates = new ArrayList<String>();

        public BehaviourSendToAgent(Agent _a) {
            super(_a);
            Agent = _a;
        }
        
        private Resource parse_process(String process_to_process) {
            
            String pid_i = process_to_process.substring(process_to_process.indexOf("pid-") + 4, process_to_process.indexOf("-process_name-"));
            String process_name_i = process_to_process.substring(process_to_process.indexOf("-process_name-") + 14, process_to_process.indexOf("-created"));
            String created_i;
            try{
                created_i = process_to_process.substring(process_to_process.indexOf("created-") + 8, process_to_process.indexOf("#"));
            }catch(Exception e ){
                created_i = process_to_process.substring(process_to_process.indexOf("created-") + 8);
            }

//            log.info(pid_i + " " + created_i);
            
            String module_name = this.Agent.getLocalName().replace("Sensor", "");
            
            
            Resource resource;
            resource = model.createResource(textHelper.createIndividualProcessName(Memory.getBase(), pid_i, process_name_i, created_i, mem.config.PLATFORM_ID), ProcessesProperty);
            resource.addProperty(PidProperty, model.createTypedLiteral(pid_i));
            resource.addProperty(process_nameProperty, model.createTypedLiteral(process_name_i));
            resource.addProperty(CreatedProperty, model.createTypedLiteral(created_i));
//            resource.addProperty(memberOf, model.getObjectProperty(Memory.getBase() + "Pslist"));
//            resource.addProperty(memberOf, model.getOntClass(Memory.getBase() + module_name));
            
            log.info("URI: " + resource);
//            informParentAgents(resource);

            return resource;
        }
        
        private Resource parse_dll(String dll_to_process) {

            String base_i = dll_to_process.substring(dll_to_process.indexOf("-base-") + 6, dll_to_process.indexOf("-L-"));
            String load_i = dll_to_process.substring(dll_to_process.indexOf("-L-") + 3, dll_to_process.indexOf("-I-"));
            String init_i = dll_to_process.substring(dll_to_process.indexOf("-I-") + 3, dll_to_process.indexOf("-M-"));
            String mem_i = dll_to_process.substring(dll_to_process.indexOf("-M-") + 3, dll_to_process.indexOf("-path-"));
            String path_i = dll_to_process.substring(dll_to_process.indexOf("-path-") + 6);
            String new_path_i = path_i.replace('\\', '/');
            
//            log.info(base_i + " " + load_i + " " + init_i + " " + mem_i + " " + new_path_i);
            
            String module_name = this.Agent.getLocalName().replace("Sensor", "");

            Resource resource;

            resource = model.createResource(textHelper.createIndividualDllName(Memory.getBase(), base_i, new_path_i, mem.config.PLATFORM_ID), DLLsProperty);
            resource.addProperty(BaseProperty, model.createTypedLiteral(base_i));
            resource.addProperty(LoadProperty, model.createTypedLiteral(load_i));
            resource.addProperty(InitProperty, model.createTypedLiteral(init_i));
            resource.addProperty(MemProperty, model.createTypedLiteral(mem_i));
            resource.addProperty(PathProperty, model.createTypedLiteral(new_path_i));
//            resource.addProperty(dllOf, model.getOntClass(Memory.getBase() + module_name));
//            resource.addOntClass(model.getResource(Memory.getBase() + "LdrModule" + "DLLs"));

            log.info("URI: " + resource);
            informParentAgents(resource);

            return resource;
        }
        
        private Resource connect_2_resources(Resource resource_source, Property property_action, Resource resource_target) {

            resource_source.addProperty(property_action, resource_target);
            
            return resource_source;
        }
        
        

        @Override
        public void action() {

            if(first){
                first = false;
                dashboard.changeStatus(this.getAgent().getLocalName(), "done");
                log.info(this.getAgent().getLocalName() + " is ready!");
//                doWait();

        
            }
            
            
            try {
                Thread.sleep(5000);
            } catch (InterruptedException ie) {
            }
            
            
            synchronized (this) {

                ACLMessage msg;
                while((msg = receive())!=null)
                {
//                    System.out.println("Received message: "+ msg.getContent());
                    String list = msg.getContent();
                    String[] list_item;
                    list_item = list.split("#end ");
                    
                    log.info("This is " + this.getAgent().getLocalName());
                    String module_name = this.getAgent().getLocalName().replace("Sensor", "");
                    
                    switch(module_name){

                        case "Pslist":
                        case "Pslist_PAPH":
                        case "Psscan":
                        case "Pstree":
                        case "ExitedProcesses":
                        case "HidingProcesses":
                        case "FakeNamedProcesses":
                            for (int i = 0; i < list_item.length; i++){
                                Resource resource_i_proc = parse_process(list_item[i]);
                                informParentAgents(resource_i_proc);
                            }
                            break;

                        case "LdrModule":
                            for (int i = 0; i < list_item.length; i++){
                                String process_item = list_item[i].substring(0, list_item[i].indexOf("#dll"));
                                String dll_item = list_item[i].substring(list_item[i].indexOf("#dll"));
                                Resource resource_i_proc = parse_process(process_item);
                                informParentAgents(resource_i_proc);
                                Resource resource_i_dll = parse_dll(dll_item);
                                resource_i_dll = connect_2_resources(resource_i_dll, dllOf, resource_i_proc);
                                informParentAgents(resource_i_dll);
                            }
                            break;

                        default:        log.info("NOT CORRECT module: " + module_name);
                    }
//                    for (int i = 0; i < processes.length; i++){
//
//                        String list_remaining = processes[i];
//                        
////                        log.info(list_remaining);
////                        while(true){
////                            try{
////                                String class_name = list_remaining.substring(0, list_remaining.indexOf("-"));
////                                list_remaining = list_remaining.substring(class_name.length()+1);
////                                switch(class_name){
////
////                                    case "proces":  list_remaining = parse_process(list_remaining);
////                                                    break;
////
////                                    case "dll":  
////                                                    break;
////
////                                    default:        log.info("NOT CORRECT/NO MORE class to create RDF: " + class_name);
////                                }
////                                log.info(list_remaining);
////                                break;
////                            }catch(Exception e ){
////                                break;
////                            }
////                            
////                        }
//                        
//                    }
                }
                block();

                // do senzor stuff 
                if (test < 5) {
                    test++;

//                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                    DateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
//                    Date date = new Date();
//                    String formatedDate = dateFormat.format(date);
//                    String formatedDate2 = dateFormat2.format(date);
//
//                    model = mem.getModel();
//                    Resource resource;
//                    
//                    for(int i = 0; i < 5; i++){
//                        date = new Date();
//                        formatedDate = dateFormat.format(date);
//                        formatedDate2 = dateFormat2.format(date);
//                        
//                        resource = model.createResource(textHelper.createIndividualName(Memory.getBase(), Integer.toString(i), formatedDate2, mem.config.PLATFORM_ID), ProcessesProperty);
////                        resource.addProperty(PidProperty, model.createTypedLiteral("p" + i));
////                        resource.addProperty(TitleProperty, model.createTypedLiteral("t" + i));
////                        resource.addProperty(CreatedProperty, model.createTypedLiteral(formatedDate));
////                        resource.addProperty(PlatformProperty, model.createTypedLiteral("Platform1"));
////                        resource.addProperty(PlatformProperty, model.createTypedLiteral(textHelper.senderToClass(this.Agent.getLocalName())));
//                        resource.addProperty(memberOf, model.getOntClass(Memory.getBase() + "Pslist"));
////                        resource.addProperty(memberOf, model.getObjectProperty(Memory.getBase() + "Pslist"));
//                        log.info("URI: " + resource.getURI());
//
//                        informParentAgents(resource);
//                        
//                        //log.info("Count: " + counter++);
//                    }
                    
                }
            }
        }

        @Override
        public boolean done() {
            return finished;
        }
       
    }
}
