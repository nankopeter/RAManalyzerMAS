
//import myCommunication.TcpServer;
//import helpers.TextHelper;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.ControllerException;
import jade.wrapper.StaleProxyException;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import javax.swing.JOptionPane;

import myHelpers.TextHelper;
import org.apache.log4j.Logger;
import org.apache.log4j.net.SocketServer;
import org.apache.xmlrpc.WebServer;



public class Gui extends javax.swing.JFrame {

    private static List<String> Agents = new ArrayList<String>();
    private static List<String> Sensors = new ArrayList<String>();
    private static Logger log = Logger.getLogger( Gui.class.getName() );
    
    // creating agents
    private static jade.core.Runtime rt;
    private static ContainerController cc;
    private static ContainerController sensorsContainer;
    
    // webserver for dashboard
    WebServer xmlrpcServer;
    private static final int port = 8001;
    
    // main config
    private static String HOST = null;
    private static String PORT = null;
    private static String MTP_PORT = null;
    private static String USE_GUI = null;
    private static String PLATFORM_ID = null;
    
    private static boolean READ_METHOD = false;
  
    static java.util.Properties config = null;
    
    TextHelper textHelper;
    
    private static double itemsSize = 0.0;
    
    private static int rm = -1;
    
    /**
     * Creates new form Gui
     */
    public Gui() {
        initComponents();
        
        try{
            xmlrpcServer = new WebServer(port);
            log.info("XMLRPC communication running ...");
        }   catch (IOException e)   {
            log.error("Port " + port + " is in use" + " or: " + e.toString() ); 
        }
        xmlrpcServer.addHandler("Gui", this);
        
        textHelper = new TextHelper();
        
    }
    
    public String addMessage(String message) {
        dashBoard.append(message);
        dashBoard.setCaretPosition(dashBoard.getDocument().getLength());
        return "ok";
    }
    
    public String changeStatus(String name, String action){
        
        itemsSize= 4;
        double calc = 0;
        
        switch(action){
            case "done": {
                calc = statusBar.getValue() + ((1/itemsSize)*100);
                statusBar.setValue((int) calc);
                break;
            }
            case "busy": {
                calc = statusBar.getValue() - ((1/itemsSize)*100);
                if(calc >= 0){
                    statusBar.setValue((int)calc);
                }
                else{
                    statusBar.setValue(0);
                }
                
                break;
            }
            default:
                break;
        }
        
        if(calc >= 95){
            statusBar.setValue(100);
            statusText.setText("System successfully loaded!");
            
            updateInfo();
        }

        return "ok";
    }
    
    private void finish(int status){
        
        addMessage(textHelper.formatExitMessage("System will shutting down in few seconds"));

        new Thread(new Runnable() {
            public void run(){
                try {  
                    TimeUnit.SECONDS.sleep(3);
                    System.exit(status);
                } catch (InterruptedException ie) {
                }
            }
        }).start();
    }
    
    private void activeSensor(javax.swing.JCheckBox sensor){
        if(sensor.isSelected()){
            try {
                sensorsContainer.getAgent(sensor.getText()).activate();
                itemsSize++;
            } catch (ControllerException ex) {
                log.error("Error while activating sensor: " + sensor.getText() + ", error: " + ex.toString());
            }
        }
        else{
            try {
                sensorsContainer.getAgent(sensor.getText()).suspend();
                itemsSize--;
            } catch (ControllerException ex) {
                log.error("Error while suspending sensor: " + sensor.getText() + ", error: " + ex.toString());
            }
        }
    }
    
    private void updateInfo(){
        sensorList.setText("");
        Sensors.forEach((sensor) -> {
            sensorList.append(sensor + "\n");
        });
       
        agentList.setText("");
        Agents.forEach((agent) -> {
            agentList.append(agent + "\n");
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jDialog1 = new javax.swing.JDialog();
        jDialog2 = new javax.swing.JDialog();
        jDialog3 = new javax.swing.JDialog();
        jScrollPane2 = new javax.swing.JScrollPane();
        dashBoard = new javax.swing.JTextArea();
        saveAndExit = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        DosSensor = new javax.swing.JCheckBox();
        EmailSendingSensor = new javax.swing.JCheckBox();
        ExploitSendingSensor = new javax.swing.JCheckBox();
        ScanningSensor = new javax.swing.JCheckBox();
        statusBar = new javax.swing.JProgressBar();
        statusText = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jScrollPane1 = new javax.swing.JScrollPane();
        agentList = new javax.swing.JTextArea();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        sensorList = new javax.swing.JTextArea();
        jLabel3 = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();

        javax.swing.GroupLayout jDialog1Layout = new javax.swing.GroupLayout(jDialog1.getContentPane());
        jDialog1.getContentPane().setLayout(jDialog1Layout);
        jDialog1Layout.setHorizontalGroup(
            jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        jDialog1Layout.setVerticalGroup(
            jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jDialog2Layout = new javax.swing.GroupLayout(jDialog2.getContentPane());
        jDialog2.getContentPane().setLayout(jDialog2Layout);
        jDialog2Layout.setHorizontalGroup(
            jDialog2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        jDialog2Layout.setVerticalGroup(
            jDialog2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jDialog3Layout = new javax.swing.GroupLayout(jDialog3.getContentPane());
        jDialog3.getContentPane().setLayout(jDialog3Layout);
        jDialog3Layout.setHorizontalGroup(
            jDialog3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        jDialog3Layout.setVerticalGroup(
            jDialog3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("DashBoard");

        dashBoard.setEditable(false);
        dashBoard.setColumns(20);
        dashBoard.setRows(5);
        jScrollPane2.setViewportView(dashBoard);

        saveAndExit.setText("Save and Exit");
        saveAndExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveAndExitActionPerformed(evt);
            }
        });

        jLabel1.setText("DashBoard");

        DosSensor.setText("DosSensor");
        DosSensor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DosSensorActionPerformed(evt);
            }
        });

        EmailSendingSensor.setText("EmailSendingSensor");
        EmailSendingSensor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EmailSendingSensorActionPerformed(evt);
            }
        });

        ExploitSendingSensor.setText("ExploitSendingSensor");
        ExploitSendingSensor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ExploitSendingSensorActionPerformed(evt);
            }
        });

        ScanningSensor.setText("ScanningSensor");
        ScanningSensor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ScanningSensorActionPerformed(evt);
            }
        });

        statusText.setText("System loading ...");

        agentList.setEditable(false);
        agentList.setColumns(20);
        agentList.setRows(5);
        jScrollPane1.setViewportView(agentList);

        jLabel2.setText("Active Agents");

        sensorList.setColumns(20);
        sensorList.setRows(5);
        jScrollPane3.setViewportView(sensorList);

        jLabel3.setText("Active Sensors");

        jMenu1.setText("System");

        jMenuItem2.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_TAB, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem2.setText("Info");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem2);

        jMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0));
        jMenuItem1.setText("Exit");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuBar1.add(jMenu1);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator2)
                    .addComponent(jScrollPane2)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(DosSensor)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(EmailSendingSensor)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(saveAndExit))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(ExploitSendingSensor)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ScanningSensor)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 257, Short.MAX_VALUE)
                        .addComponent(statusBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jSeparator1)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(statusText))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(saveAndExit)
                            .addComponent(DosSensor)
                            .addComponent(EmailSendingSensor))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(statusBar, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(ExploitSendingSensor)
                        .addComponent(ScanningSensor)))
                .addGap(13, 13, 13)
                .addComponent(statusText)
                .addGap(15, 15, 15)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3))
                .addGap(3, 3, 3)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 116, Short.MAX_VALUE)
                    .addComponent(jScrollPane1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 26, Short.MAX_VALUE)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 262, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void saveAndExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveAndExitActionPerformed

        Sensors.forEach((sensor) -> {
            try {
                sensorsContainer.getAgent(sensor).kill();
            } catch (ControllerException ex) {
                log.error("Error while killing sensor: " + sensor + ", error: " + ex.toString());
            }
        });
        
        addMessage(textHelper.formatExitMessage("Sensors successfully closed"));
        
        Agents.forEach((agent) -> {
            try {
                cc.getAgent(agent).kill();
            } catch (ControllerException ex) {
                log.error("Error while killing agent: " + agent + ", error: " + ex.toString());
            }
        });
        
        addMessage(textHelper.formatExitMessage("Angets successfully closed"));
        
        finish(NORMAL);
    }//GEN-LAST:event_saveAndExitActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        saveAndExitActionPerformed(evt);
    }//GEN-LAST:event_jMenuItem1ActionPerformed
    
    private void DosSensorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DosSensorActionPerformed
        activeSensor(DosSensor);
    }//GEN-LAST:event_DosSensorActionPerformed

    private void EmailSendingSensorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EmailSendingSensorActionPerformed
        activeSensor(EmailSendingSensor);
    }//GEN-LAST:event_EmailSendingSensorActionPerformed

    private void ExploitSendingSensorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ExploitSendingSensorActionPerformed
        activeSensor(ExploitSendingSensor);
    }//GEN-LAST:event_ExploitSendingSensorActionPerformed

    private void ScanningSensorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ScanningSensorActionPerformed
        activeSensor(ScanningSensor);
    }//GEN-LAST:event_ScanningSensorActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        addMessage("\t --- MultiAgentSystem created by Patrik Matiasko <patrik.matiasko@gmail.com> --- ");
    }//GEN-LAST:event_jMenuItem2ActionPerformed
 
    private static void readMainProperty(){
        String propertyFile = "D:\\Diplomovka_program\\bakalarka-master\\program\\MultiagentovySystem\\Sensor\\config\\System.properties";
        try {
            config = new java.util.Properties();
            config.load(new FileInputStream(propertyFile));
            
            HOST = config.getProperty("HOST");
            PORT = config.getProperty("PORT");
            USE_GUI = config.getProperty("USE_GUI");
            PLATFORM_ID = config.getProperty("PLATFORM_ID");
//            MTP_PORT = config.getProperty("MTP_PORT");
        } catch (Exception e) {
            log.error("Cannot load properties from " + propertyFile, e);
        }
        
    }
    
    private static List<String> readProperty(String propertyFile){
        List<String> arguments = new ArrayList<String>();
        String tmp = "";
        boolean is_agent = false;
        boolean is_sensor = false;
        
        log.info("reading:" + propertyFile);
        try {
            config = new java.util.Properties();
            config.load(new FileInputStream(propertyFile));

            arguments.add("IS_AGENT");
            tmp = config.getProperty("IS_AGENT");
            is_agent = Boolean.parseBoolean(tmp);
            arguments.add(tmp);
            
            arguments.add("IS_SENSOR");
            tmp = config.getProperty("IS_SENSOR");
            is_sensor = Boolean.parseBoolean(tmp);
            arguments.add(tmp);

            arguments.add("CREATE_MODEL");
            arguments.add(config.getProperty("CREATE_MODEL"));
            
            arguments.add("REMOVE_MODEL");
            arguments.add(config.getProperty("REMOVE_MODEL"));
            
            arguments.add("MEM_MODEL");
            arguments.add(config.getProperty("MEM_MODEL"));

            // if is agent
            if(is_agent){
                arguments.add("INDIVIDUALS_FILE");
                arguments.add(config.getProperty("INDIVIDUALS_FILE"));
                
                arguments.add("OUTPUT_FILE");
                arguments.add(config.getProperty("OUTPUT_FILE"));
                
                arguments.add("AGENT_SENSORS");
                arguments.add(config.getProperty("AGENT_SENSORS"));
                
                arguments.add("READ_METHOD"); 
                arguments.add(String.valueOf(READ_METHOD));
            }

            //if is sensor
            if(is_sensor){
                arguments.add("PARENT_AGENTS");
                arguments.add(config.getProperty("PARENT_AGENTS"));
                
                arguments.add("PARENT_AGENTS_ADDRESS");
                arguments.add(config.getProperty("PARENT_AGENTS_ADDRESS"));
                
                arguments.add("REASONING_TIME");
                arguments.add(config.getProperty("REASONING_TIME"));
                
                arguments.add("SENDING_TIME");
                arguments.add(config.getProperty("SENDING_TIME"));
            }
            
            arguments.add("HOST"); 
            arguments.add(HOST);

            arguments.add("PORT"); 
            arguments.add(PORT);

            arguments.add("PLATFORM_ID"); 
            arguments.add(PLATFORM_ID);
            
            arguments.add("IS_GUIDE"); 
            arguments.add(config.getProperty("IS_GUIDE"));
          
        } catch (Exception e) {
            log.error("Cannot load properties from " + propertyFile, e);
        }
        
        return arguments;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Gui.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        // read main system properties
        readMainProperty();

        java.awt.EventQueue.invokeLater(() -> {
            new Gui().setVisible(true);

            // 1 - no
            // 0 - yes
            //rm = JOptionPane.showConfirmDialog(null, " Read from last save ?", "Read Method", JOptionPane.YES_NO_OPTION);
        });

        rm = 1;

        // start up checker
        while( rm == -1){
            try {
                Thread.sleep(200);
            } catch (InterruptedException ie) {

            }
        }

        //set read method
        if(rm == 0){
            READ_METHOD = true;
        }
        else{
            READ_METHOD = false;
        }

//        log.info(READ_METHOD);


        rt = jade.core.Runtime.instance();
        Profile p = new ProfileImpl();
        p.setParameter(Profile.MAIN_HOST, HOST);
        p.setParameter(Profile.MAIN_PORT, PORT);
        p.setParameter(Profile.GUI, USE_GUI);
        p.setParameter(Profile.NO_MTP, "false");
//        p.setParameter(Profile.MTPS, "jade.mtp.http.MessageTransportProtocol" +
//                "(http://" + HOST + ":" + MTP_PORT + "/acc);");
        p.setParameter(Profile.PLATFORM_ID, PLATFORM_ID);
        cc = rt.createMainContainer(p);

        Profile sensorsProfile = new ProfileImpl();
        sensorsProfile.setParameter(Profile.CONTAINER_NAME, "SensorsContainer");
        sensorsContainer = rt.createAgentContainer(sensorsProfile);

        AgentController ac;


        try{
            // SENSORS

//            ac = sensorsContainer.createNewAgent("DosSensor", "sensors.DosSensor", readProperty("C:\\Users\\mizom\\Downloads\\DVD\\MultiagentovySystem\\Sensor\\config\\DosSensor.properties").toArray());
//            ac.start();
//            Sensors.add("DosSensor");


            ac = sensorsContainer.createNewAgent("PsscanSensor", "sensors.UniversalSensor",
                    readProperty("D:\\Diplomovka_program\\bakalarka-master\\program\\MultiagentovySystem\\Sensor\\config\\PsscanSensor.properties").toArray());
            ac.start();
            Sensors.add("PsscanSensor");

            ac = sensorsContainer.createNewAgent("PstreeSensor", "sensors.UniversalSensor",
                    readProperty("D:\\Diplomovka_program\\bakalarka-master\\program\\MultiagentovySystem\\Sensor\\config\\PstreeSensor.properties").toArray());
            ac.start();
            Sensors.add("PstreeSensor");

            ac = sensorsContainer.createNewAgent("ExitedProcessesSensor", "sensors.UniversalSensor",
                    readProperty("D:\\Diplomovka_program\\bakalarka-master\\program\\MultiagentovySystem\\Sensor\\config\\ExitedProcessesSensor.properties").toArray());
            ac.start();
            Sensors.add("ExitedProcessesSensor");

            ac = sensorsContainer.createNewAgent("HidingProcessesSensor", "sensors.UniversalSensor",
                    readProperty("D:\\Diplomovka_program\\bakalarka-master\\program\\MultiagentovySystem\\Sensor\\config\\HidingProcessesSensor.properties").toArray());
            ac.start();
            Sensors.add("HidingProcessesSensor");

            ac = sensorsContainer.createNewAgent("FakeNamedProcessesSensor", "sensors.UniversalSensor",
                    readProperty("D:\\Diplomovka_program\\bakalarka-master\\program\\MultiagentovySystem\\Sensor\\config\\FakeNamedProcessesSensor.properties").toArray());
            ac.start();
            Sensors.add("FakeNamedProcessesSensor");

            ac = sensorsContainer.createNewAgent("PslistSensor", "sensors.UniversalSensor",
                    readProperty("D:\\Diplomovka_program\\bakalarka-master\\program\\MultiagentovySystem\\Sensor\\config\\PslistSensor.properties").toArray());
            ac.start();
            Sensors.add("PslistSensor");

            ac = sensorsContainer.createNewAgent("LdrModuleSensor", "sensors.UniversalSensor",
                    readProperty("D:\\Diplomovka_program\\bakalarka-master\\program\\MultiagentovySystem\\Sensor\\config\\LdrModuleSensor.properties").toArray());
            ac.start();
            Sensors.add("LdrModuleSensor");

            ac = sensorsContainer.createNewAgent("MalfindSensor", "sensors.UniversalSensor",
                    readProperty("D:\\Diplomovka_program\\bakalarka-master\\program\\MultiagentovySystem\\Sensor\\config\\MalfindSensor.properties").toArray());
            ac.start();
            Sensors.add("MalfindSensor");

            ac = sensorsContainer.createNewAgent("HollowfindSensor", "sensors.UniversalSensor",
                    readProperty("D:\\Diplomovka_program\\bakalarka-master\\program\\MultiagentovySystem\\Sensor\\config\\HollowfindSensor.properties").toArray());
            ac.start();
            Sensors.add("HollowfindSensor");

            ac = sensorsContainer.createNewAgent("SSDTSensor", "sensors.UniversalSensor",
                    readProperty("D:\\Diplomovka_program\\bakalarka-master\\program\\MultiagentovySystem\\Sensor\\config\\SSDTSensor.properties").toArray());
            ac.start();
            Sensors.add("SSDTSensor");

            ac = sensorsContainer.createNewAgent("InlineKernelHookSensor", "sensors.UniversalSensor",
                    readProperty("D:\\Diplomovka_program\\bakalarka-master\\program\\MultiagentovySystem\\Sensor\\config\\InlineKernelHookSensor.properties").toArray());
            ac.start();
            Sensors.add("InlineKernelHookSensor");

            ac = sensorsContainer.createNewAgent("IdtHookSensor", "sensors.UniversalSensor",
                    readProperty("D:\\Diplomovka_program\\bakalarka-master\\program\\MultiagentovySystem\\Sensor\\config\\IdtHookSensor.properties").toArray());
            ac.start();
            Sensors.add("IdtHookSensor");

            ac = sensorsContainer.createNewAgent("IrpFunctionHookSensor", "sensors.UniversalSensor",
                    readProperty("D:\\Diplomovka_program\\bakalarka-master\\program\\MultiagentovySystem\\Sensor\\config\\IrpFunctionHookSensor.properties").toArray());
            ac.start();
            Sensors.add("IrpFunctionHookSensor");

            ac = sensorsContainer.createNewAgent("ModuleListSensor", "sensors.UniversalSensor",
                    readProperty("D:\\Diplomovka_program\\bakalarka-master\\program\\MultiagentovySystem\\Sensor\\config\\ModuleListSensor.properties").toArray());
            ac.start();
            Sensors.add("ModuleListSensor");

            ac = sensorsContainer.createNewAgent("CallbacksSensor", "sensors.UniversalSensor",
                    readProperty("D:\\Diplomovka_program\\bakalarka-master\\program\\MultiagentovySystem\\Sensor\\config\\CallbacksSensor.properties").toArray());
            ac.start();
            Sensors.add("CallbacksSensor");

            ac = sensorsContainer.createNewAgent("TimersSensor", "sensors.UniversalSensor",
                    readProperty("D:\\Diplomovka_program\\bakalarka-master\\program\\MultiagentovySystem\\Sensor\\config\\TimersSensor.properties").toArray());
            ac.start();
            Sensors.add("TimersSensor");

            ac = sensorsContainer.createNewAgent("TCPpassingAgent", "sensors.TCPpassingAgent",
                    readProperty("D:\\Diplomovka_program\\bakalarka-master\\program\\MultiagentovySystem\\Sensor\\config\\TCPpassingAgent.properties").toArray());
            ac.start();
            Sensors.add("TCPpassingAgent");

        } catch(StaleProxyException e){
            e.printStackTrace();
        }

    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox DosSensor;
    private javax.swing.JCheckBox EmailSendingSensor;
    private javax.swing.JCheckBox ExploitSendingSensor;
    private javax.swing.JCheckBox ScanningSensor;
    private javax.swing.JTextArea agentList;
    private javax.swing.ButtonGroup buttonGroup1;
    public javax.swing.JTextArea dashBoard;
    private javax.swing.JDialog jDialog1;
    private javax.swing.JDialog jDialog2;
    private javax.swing.JDialog jDialog3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JButton saveAndExit;
    private javax.swing.JTextArea sensorList;
    private javax.swing.JProgressBar statusBar;
    private javax.swing.JLabel statusText;
    // End of variables declaration//GEN-END:variables
}
