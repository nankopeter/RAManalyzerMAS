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


public class UniversalSensor extends Agent {

    private static Logger log = Logger.getLogger(UniversalSensor.class.getName());
    private OntModel model;

    private Memory mem;

    // Process properties
    private Property ProcessesProperty;
    private Property PidProperty;
    private Property process_nameProperty;
    private Property CreatedProperty;
    
    // DLL properties
    private Property DLLsProperty;
    private Property BaseProperty;
    private Property PathProperty;
    private Property LoadProperty;
    private Property InitProperty;
    private Property MemProperty;
    private Property dllOf;
    
    // Address properties
    private Property PropertiesProperty;
    private Property AddressProperty;
    private Property VadTagProperty;
    private Property ProtectionProperty;
    private Property process_path_pebProperty;
    private Property PrivateProperty;
    private Property addressOf;
    
    // Function properties
    private Property FunctionProperty;
    private Property NameProperty;
//    private Property AddressProperty;
    private Property functionOf;
    private Property callsFunction;
    
    // Driver properties
    private Property DriverProperty;

    private Property DriverName;
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
    private Property CallbackDetail;
    private Property DueTime;
    private Property Period;
    private Property Signaled;

    //    private Property NameProperty;
//    private Property AddressProperty;
    private Property SizeProperty;
    private Property FunctionNameProperty;
//    private Property callsFunction;
    
    private Property doesProperty;
    
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

        // initialization of properties from Model
        
        ProcessesProperty_new = model.getOntClass(Memory.getBase() + "Processes");
        ProcessesProperty = mem.getModel().getProperty(Memory.getBase() + "Processes");      
        PidProperty = mem.getModel().getProperty(Memory.getBase() + "pid");
        process_nameProperty = mem.getModel().getProperty(Memory.getBase() + "process_name");
        CreatedProperty = mem.getModel().getProperty(Memory.getBase()  + "created");
        
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
        process_path_pebProperty = mem.getModel().getProperty(Memory.getBase() + "process_path_peb");
        ProtectionProperty = mem.getModel().getProperty(Memory.getBase() + "protection");
        PrivateProperty = mem.getModel().getProperty(Memory.getBase() + "private");
        addressOf = mem.getModel().getProperty(Memory.getBase() + "addressOf");
        
        FunctionProperty = mem.getModel().getProperty(Memory.getBase() + "Functions");
        NameProperty = mem.getModel().getProperty(Memory.getBase() + "name");
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
       
        doesProperty = mem.getModel().getProperty(Memory.getBase() + "does");
        
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
                    //log.info("-sending: " + resoruce.getProperty(PidProperty));
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


        private Resource parse_callbacks(String callbacks_to_callbacks) {

            String type = callbacks_to_callbacks.substring(callbacks_to_callbacks.indexOf("type-") + 5, callbacks_to_callbacks.indexOf("-module_name-"));
            String driverName = callbacks_to_callbacks.substring(callbacks_to_callbacks.indexOf("-module_name-") + 13, callbacks_to_callbacks.indexOf("-path"));
            String detail = callbacks_to_callbacks.substring(callbacks_to_callbacks.indexOf("path-") + 7);


            Resource resource = model.createResource(textHelper.createIndividualCallbackName(Memory.getBase(), type, driverName, detail, mem.config.PLATFORM_ID), DriverProperty);
            resource.addProperty(CallbackType, model.createTypedLiteral(type));
            resource.addProperty(DriverName, model.createTypedLiteral(driverName));
            resource.addProperty(CallbackDetail, model.createTypedLiteral(detail));

            return resource;
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
            
            Resource resource = model.createResource(textHelper.createIndividualProcessName(Memory.getBase(), pid_i, process_name_i, created_i, mem.config.PLATFORM_ID), ProcessesProperty);
            resource.addProperty(PidProperty, model.createTypedLiteral(pid_i));
            resource.addProperty(process_nameProperty, model.createTypedLiteral(process_name_i));
            resource.addProperty(CreatedProperty, model.createTypedLiteral(created_i));

            return resource;
        }
        
        private Resource parse_dll(String dll_to_process) {

            String base_i = dll_to_process.substring(dll_to_process.indexOf("-base-") + 6, dll_to_process.indexOf("-L-"));
            String load_i = dll_to_process.substring(dll_to_process.indexOf("-L-") + 3, dll_to_process.indexOf("-I-"));
            String init_i = dll_to_process.substring(dll_to_process.indexOf("-I-") + 3, dll_to_process.indexOf("-M-"));
            String mem_i = dll_to_process.substring(dll_to_process.indexOf("-M-") + 3, dll_to_process.indexOf("-path-"));
            String path_i = dll_to_process.substring(dll_to_process.indexOf("-path-") + 6);
            String new_path_i = path_i.replace('\\', '/');

            Resource resource = model.createResource(textHelper.createIndividualDllName(Memory.getBase(), base_i, new_path_i, mem.config.PLATFORM_ID), DLLsProperty);
            resource.addProperty(BaseProperty, model.createTypedLiteral(base_i));
            resource.addProperty(LoadProperty, model.createTypedLiteral(load_i));
            resource.addProperty(InitProperty, model.createTypedLiteral(init_i));        
            resource.addProperty(MemProperty, model.createTypedLiteral(mem_i));
            resource.addProperty(PathProperty, model.createTypedLiteral(new_path_i));

//            log.info("URI: " + resource);

            return resource;
        }
        
        private Resource parse_properties(String properties_to_process) {

            String address_i = properties_to_process.substring(properties_to_process.indexOf("-address-") + 9, properties_to_process.indexOf("-vad_tag-"));
            String vad_tag_i = properties_to_process.substring(properties_to_process.indexOf("-vad_tag-") + 9, properties_to_process.indexOf("-protection-"));
            String protection_i = properties_to_process.substring(properties_to_process.indexOf("-protection-") + 12, properties_to_process.indexOf("-private-"));
            String private_i = properties_to_process.substring(properties_to_process.indexOf ("-private-") + 9);

            Resource resource = model.createResource(textHelper.createIndividualPropertiesName(Memory.getBase(), address_i, mem.config.PLATFORM_ID), PropertiesProperty);
            resource.addProperty(AddressProperty, model.createTypedLiteral(address_i));
            resource.addProperty(VadTagProperty, model.createTypedLiteral(vad_tag_i));
            resource.addProperty(ProtectionProperty, model.createTypedLiteral(protection_i));
            resource.addProperty(PrivateProperty, model.createTypedLiteral(private_i));

//            log.info("URI: " + resource);

            return resource;
        }

        private Resource parse_hollowfind_properties(String properties_to_process) {

//            String address_i = properties_to_process.substring(properties_to_process.indexOf("-address-") + 9, properties_to_process.indexOf("-vad_tag-"));
//            String vad_tag_i = properties_to_process.substring(properties_to_process.indexOf("-vad_tag-") + 9, properties_to_process.indexOf("-process_path_peb-"));
//            String process_path_peb_i = properties_to_process.substring(properties_to_process.indexOf("-process_path_peb-") + 18);

            String address_i = properties_to_process.substring(properties_to_process.indexOf("-address-") + 9, properties_to_process.indexOf("-vad_tag-"));
            String vad_tag_i = properties_to_process.substring(properties_to_process.indexOf("-vad_tag-") + 9);

            Resource resource = model.createResource(textHelper.createIndividualPropertiesName(Memory.getBase(), address_i, mem.config.PLATFORM_ID), PropertiesProperty);
            resource.addProperty(AddressProperty, model.createTypedLiteral(address_i));
            resource.addProperty(VadTagProperty, model.createTypedLiteral(vad_tag_i));
            //resource.addProperty(process_path_pebProperty, model.createTypedLiteral(process_path_peb_i));


//            log.info("URI: " + resource);

            return resource;
        }

        
//        private Resource parse_function(String function_to_process) {
//
//            String name_i;
//            try{
//                name_i = function_to_process.substring(function_to_process.indexOf("-name-") + 6, function_to_process.indexOf("#target"));
//            }catch(Exception e ){
//                name_i = function_to_process.substring(function_to_process.indexOf("-name-") + 6);
//            }
//
//            Resource resource;
//            resource = model.createResource(textHelper.createIndividualFunctionName(Memory.getBase(), name_i, mem.config.PLATFORM_ID), FunctionProperty);
//            resource.addProperty(NameProperty, model.createTypedLiteral(name_i));
//
//            try{
//                String address_i = function_to_process.substring(function_to_process.indexOf("-address-") + 9, function_to_process.indexOf("-name-"));
//                resource.addProperty(AddressProperty, model.createTypedLiteral(address_i));
//            }catch(Exception e ){
//            }
//
////            log.info("URI: " + resource);
//
//            return resource;
//        }
        
//        private Resource parse_driver_function(String function_to_process, String driver_name) {
//
//            String name_i = function_to_process.substring(function_to_process.indexOf("-name-") + 6, function_to_process.indexOf("#target"));
//
//
//            Resource resource;
//            resource = model.createResource(textHelper.createIndividualDriverFunctionName(Memory.getBase(), driver_name, name_i, mem.config.PLATFORM_ID), FunctionProperty);
//
////            log.info("URI: " + resource);
//
//            return resource;
//        }
        
//        private Resource parse_driver(String driver_to_process) {
//
//            String name_i = driver_to_process.substring(driver_to_process.indexOf("-name-") + 6, driver_to_process.indexOf("-address-"));
//            String address_i = driver_to_process.substring(driver_to_process.indexOf("-address-") + 9, driver_to_process.indexOf("-size-"));
//            String size_i;
//            try{
//                size_i = driver_to_process.substring(driver_to_process.indexOf("-size-") + 6, driver_to_process.indexOf("#target"));
//            }catch(Exception e ){
//                size_i = driver_to_process.substring(driver_to_process.indexOf("-size-") + 6);
//            }
//
//            Resource resource;
//            resource = model.createResource(textHelper.createIndividualDriverName(Memory.getBase(), name_i, address_i, mem.config.PLATFORM_ID), DriverProperty);
//            resource.addProperty(NameProperty, model.createTypedLiteral(name_i));
//            resource.addProperty(AddressProperty, model.createTypedLiteral(address_i));
//            resource.addProperty(SizeProperty, model.createTypedLiteral(size_i));
//
////            log.info("URI: " + resource);
//
//            return resource;
//        }
        
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
                    String module_name = this.getAgent().getLocalName().replace("Sensor", "");
                    Resource resource_i_proc = model.createResource();
                    
                    switch(module_name){

                        case "Pslist":
                        case "Psscan":
                        case "Pstree":
                        case "ExitedProcesses":
                        case "HidingProcesses":
                        case "FakeNamedProcesses":
                            for (int i = 0; i < list_item.length; i++){
                                try{
                                    resource_i_proc = parse_process(list_item[i]);
                                    informParentAgents(resource_i_proc);
                                }catch(Exception e){
                                    log.info("Something wrong with " + module_name);
                                }
                            }
                            break;

                        case "LdrModule":
                            String resource_i_proc_existing = "";
                            for (int i = 0; i < list_item.length; i++){
                                
                                try{
                                    String process_item = list_item[i].substring(0, list_item[i].indexOf("#dll"));
                                    String dll_item = list_item[i].substring(list_item[i].indexOf("#dll"));
                                    if(!process_item.equals(resource_i_proc_existing)){
                                        resource_i_proc = parse_process(process_item);
                                        resource_i_proc_existing = process_item;
                                        informParentAgents(resource_i_proc);
                                    }
                                    
                                    Resource resource_i_dll = parse_dll(dll_item);
                                    resource_i_dll = connect_2_resources(resource_i_dll, dllOf, resource_i_proc);
                                    informParentAgents(resource_i_dll);
                                }catch(Exception e){
                                    log.info("Something wrong with " + module_name);
                                }
                            }
                            break;
                            
                        case "Malfind":
                            for (int i = 0; i < list_item.length; i++){
                                try{
                                    String process_item = list_item[i].substring(0, list_item[i].indexOf("#properties"));
                                    String properties_item = list_item[i].substring(list_item[i].indexOf("#properties"));
                                    resource_i_proc = parse_process(process_item);
                                    informParentAgents(resource_i_proc);
                                    Resource resource_i_properties = parse_properties(properties_item);
                                    resource_i_properties = connect_2_resources(resource_i_properties, addressOf, resource_i_proc);
                                    informParentAgents(resource_i_properties);
                                }catch(Exception e){
                                    log.info("Something wrong with " + module_name);
                                }
                            }
                            break;

                        case "Hollowfind":
                            for (int i = 0; i < list_item.length; i++){
                                try{
                                    String process_item = list_item[i].substring(0, list_item[i].indexOf("#properties"));
                                    String properties_item = list_item[i].substring(list_item[i].indexOf("#properties"));
                                    resource_i_proc = parse_process(process_item);
                                    informParentAgents(resource_i_proc);
                                    Resource resource_i_properties = parse_hollowfind_properties(properties_item);
                                    resource_i_properties = connect_2_resources(resource_i_properties, addressOf, resource_i_proc);
                                    informParentAgents(resource_i_properties);
                                }catch(Exception e){
                                    log.info("Something wrong with " + module_name);
                                }
                            }
                            break;
                        case "SSDT":
                            break;
                        case "InlineKernelHook":
                            break;
                        case "IdtHook":
                            break;
                        case "IrpFunctionHook":
                            break;
                        case "ModuleList":
                            break;
                        case "Callbacks":
                            for (int i = 0; i < list_item.length; i++){
                                try{
                                    Resource resourceCallbacks = parse_callbacks(list_item[i]);
                                    informParentAgents(resourceCallbacks);
                                }catch(Exception e){
                                    log.info("Something wrong with " + module_name);
                                }
                            }
                            break;
                        case "Timers":
                            break;

                            
//                        case "IATHooks":
//                        case "EATHooks":
//                        case "InlineHooks":
//                            for (int i = 0; i < list_item.length; i++){
//                                try{
//                                    String process_item = list_item[i].substring(0, list_item[i].indexOf("#source"));
//                                    String source_item = list_item[i].substring(list_item[i].indexOf("#source"),list_item[i].indexOf("#target"));
//                                    String target_item = list_item[i].substring(list_item[i].indexOf("#target"));
//                                    resource_i_proc = parse_process(process_item);
//                                    informParentAgents(resource_i_proc);
//                                    Resource resource_i_source = parse_function(source_item);
//                                    Resource resource_i_target = parse_function(target_item);
//                                    informParentAgents(resource_i_target);
//                                    resource_i_source = connect_2_resources(resource_i_source, functionOf, resource_i_proc);
//                                    resource_i_source = connect_2_resources(resource_i_source, callsFunction, resource_i_target);
//                                    informParentAgents(resource_i_source);
//                                }catch(Exception e){
//                                    log.info("Something wrong with " + module_name);
//                                }
//                            }
//                            break;
//                            //    private Property FunctionNameProperty;
//                            ////    private Property callsFunction;
//                        case "IRPHooks":
//                            for (int i = 0; i < list_item.length; i++){
//                                try{
//                                    String driver_item = list_item[i].substring(0, list_item[i].indexOf("#source"));
//                                    String source_item = list_item[i].substring(list_item[i].indexOf("#source"),list_item[i].indexOf("#target"));
//                                    String target_item = list_item[i].substring(list_item[i].indexOf("#target"));
//                                    Resource resource_i_driver = parse_driver(driver_item);
//                                    informParentAgents(resource_i_driver);
//
//                                    String driver_name = resource_i_driver.getProperty(NameProperty).getString();
//                                    Resource resource_i_source = parse_driver_function(source_item, driver_name);
//                                    Resource resource_i_target = parse_function(target_item);
//                                    informParentAgents(resource_i_target);
//
//                                    resource_i_source = connect_2_resources(resource_i_source, functionOf, resource_i_driver);
//                                    resource_i_source = connect_2_resources(resource_i_source, callsFunction, resource_i_target);
//                                    informParentAgents(resource_i_source);
//                                }catch(Exception e){
//                                    log.info("Something wrong with " + module_name);
//                                }
//                            }
//                            break;

                        default:        log.info("NOT CORRECT module: " + module_name);
                    }

                }
                block();

            }
        }

        @Override
        public boolean done() {
            return finished;
        }
       
    }
}
