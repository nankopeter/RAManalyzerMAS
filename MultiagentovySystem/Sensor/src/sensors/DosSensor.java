/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sensors;

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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import myMemory.Memory;
import org.apache.log4j.Logger;


public class DosSensor extends Agent {

    private static Logger log = Logger.getLogger(DosSensor.class.getName());
    private OntModel model;

    private Memory mem;

    // Properties
    private Property ProcessesProperty;
    private Property TitleProperty;
    private Property PidProperty;
    private Property CreatedProperty;
    private Property SensorProperty;
    private Property does;
    
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
        
        Message.register(this, "DosSensor");
        
        dashboard = new DashBoardHelper();
        textHelper = new TextHelper();
        
        mem = new Memory("DosSensor", aMap, false);
        model = mem.getModel();

        ProcessesProperty = mem.getModel().getProperty(Memory.getBase() + "Processes");      
        TitleProperty = mem.getModel().getProperty(Memory.getBase() + "title");
        PidProperty = mem.getModel().getProperty(Memory.getBase() + "pid");
        CreatedProperty = mem.getModel().getProperty(Memory.getBase()  + "created");
        does = mem.getModel().getProperty(Memory.getBase() + "does");
       
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
                    dashboard.addMessage(agent.getLocalName(), ACLMessage.PROPOSE, receiverName);
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

                // do senzor stuff 
                if (test < 5) {
                    test++;
    //                log.info("sensor stuff");
    //                String s = null;
    //                Process p = null;
    //                int numb = 0;
    //               
    //                try {
    //                    p = Runtime.getRuntime().exec("ps -eo pid,lstart");
    //                    
    //                    BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
    //                    BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
    //                    
    //                    boolean first = true;
    //                    
    //                    while ((s = stdInput.readLine()) != null) {
    //                       
    //                        if(!first){
    //                            Scanner scan = new Scanner(s);
    //                            while (scan.hasNext()) {
    //                                String word = scan.next();
    //
    //                                if(numb == 0 && (!word.isEmpty())){
    //                                    pids.add(word);
    //                                }
    //                                else if(numb > 1){ //skip day in week
    //                                   date += word + " ";
    //                                }
    //                                numb++;
    //                            }  
    //                          
    //                            newDate = "";
    //                            try {
    //                                newDate = myFormat.format(fromUser.parse(date));
    //                                dates.add(newDate);
    //                            } catch (ParseException ex) {
    //                                log.info("convert date error: " + ex.toString());
    //                            }
    //                        
    //                        }
    //                        first = false;
    //                        date = "";
    //                        numb = 0;
    //                    }
    //                   
    //                } catch (IOException ex) {
    //                    log.error("error in sensor runtime: " + ex.toString());
    //                }
    // 
    //                model = mem.getModel();
    //
    ////                for (int i = 0; i < pids.size(); i++) {
    //                for (int i = 0; i < 5; i++) {
    //                
    //                    Resource resource = model.createResource(Memory.getBase() + "openPorter-P-" + i, ProcessesProperty);
    //                    resource.addProperty(TitleProperty, model.createTypedLiteral("openPorterTtitle-PPP-" + i));
    //                    resource.addProperty(PidProperty, model.createTypedLiteral(pids.get(i)));
    //                    
    ////                    log.info(pids.get(i) + "  date:  " + dates.get(i));
    //                    informParentAgents(resource);
    //                    
    //                }

                    // test 123
                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    DateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
                    Date date = new Date();
                    String formatedDate = dateFormat.format(date);
                    String formatedDate2 = dateFormat2.format(date);

                    model = mem.getModel();
                    Resource resource;
                    
                    for(int i = 0; i < 5; i++){
                        date = new Date();
                        formatedDate = dateFormat.format(date);
                        formatedDate2 = dateFormat2.format(date);
                        resource = model.createResource(textHelper.createIndividualName(Memory.getBase(), Integer.toString(i), formatedDate2, mem.config.PLATFORM_ID), ProcessesProperty);
                        resource.addProperty(PidProperty, model.createTypedLiteral("p" + i));
                        resource.addProperty(TitleProperty, model.createTypedLiteral("t" + i));
//                        resource.addProperty(CreatedProperty, model.createTypedLiteral("c" + i));
                        resource.addProperty(CreatedProperty, model.createTypedLiteral(formatedDate));
                        resource.addProperty(does, model.getOntClass(Memory.getBase() + "Dos"));
                        informParentAgents(resource);
                        
//                        log.info("Count: " + counter++);
                    }
                    
                }
            }
        }

        @Override
        public boolean done() {
            return finished;
        }
       
    }
}
