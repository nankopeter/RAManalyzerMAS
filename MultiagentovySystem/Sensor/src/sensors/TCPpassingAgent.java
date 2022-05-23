/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sensors;

import myCommunication.Message;
import myHelpers.DashBoardHelper;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;

import org.apache.log4j.Logger;

public class TCPpassingAgent extends Agent{
    
    private BlockingQueue<String> process_queue = new ArrayBlockingQueue<String>(10000);
    ServerSocket serverSocket = null;
    
    private static Logger log = Logger.getLogger(PslistSensor.class.getName());
    private DashBoardHelper dashboard;
    int counter = 0;
    
    
    boolean server_running = true;
    
    
    @Override
    protected void setup()
    {
        Message.register(this, "TCPpassingAgent");
        
        dashboard = new DashBoardHelper();
        
        BehaviourSendToAgent sendToAgentBehaviour = new BehaviourSendToAgent(this);
        addBehaviour(sendToAgentBehaviour);
        
        connectingClient();
    }
    
    private Boolean connectingClient(){
        
        int portNumber = 11250;
    
        try{
            try{
                serverSocket = new ServerSocket(portNumber);
                System.out.println("Server TCPlowestAgent operating on port: " + portNumber);
            } catch (IOException e){
                System.out.println(e.getMessage());
            }
            Socket clientSocket = serverSocket.accept();
            
            TcpClient m = new TcpClient(clientSocket, process_queue);
            new Thread(m).start();
            return true;

        } catch (IOException e){
            System.out.println(e.getMessage());
            return false;
        }

    }
    
    public class TcpClient implements Runnable{

        protected Socket clientSocket = null;
        protected BlockingQueue queue = null;

        public TcpClient(Socket clientSocket, BlockingQueue queue){
            this.clientSocket = clientSocket;
            this.queue = queue;
        }

        @Override
        public void run() {

            while(true){
                try{
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    String arg1;
                    arg1 = in.readLine();
                    System.out.println("Client_rekal says: " + arg1);
                    String received_module = arg1.substring(0, arg1.indexOf(" "));
                    System.out.println("Client_rekal says: " + arg1.substring(0, arg1.indexOf(" ")));
                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                    out.println(received_module);
                    queue.put((String)arg1);
                    
                } catch(IOException e){
                    e.printStackTrace();
                } catch (InterruptedException ex) {
                    java.util.logging.Logger.getLogger(TCPpassingAgent.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        }

    }
    
    class BehaviourSendToAgent extends SimpleBehaviour {

        private boolean finished = false;
        private Agent Agent;
        
        private int test = 0;
        private int counter = 0;
        private boolean first = true;
        
        String date = "";
        String newDate = "";
        
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
                Thread.sleep(2000);
            } catch (InterruptedException ie) {
            }    
            
            synchronized (this) {
                
                while(true)
                {
                    try{
                        String processMessage = process_queue.take();
//                        log.info(processMessage);
                        String module_name = processMessage.substring(0, processMessage.indexOf(" "));
                        String list = processMessage.substring(module_name.length()+1);
//                        String[] processes;
//                        processes = list.split("#end ");
//                        for (int i = 0; i < processes.length; i++){
//                                                log.info(processes[i]);
//                                            }

                        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
                        msg.setContent(list);
                        
                        try{
                            String sensor_name = module_name + "Sensor";
                            log.info("sending to " + sensor_name);
                            msg.addReceiver(new AID(sensor_name, AID.ISLOCALNAME));
                            send(msg);
                        }catch(Exception e){
                            log.info("Could not send to " + module_name + "Sensor");
                        }                 
           
                    } catch (InterruptedException ex) {
                        break;
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
