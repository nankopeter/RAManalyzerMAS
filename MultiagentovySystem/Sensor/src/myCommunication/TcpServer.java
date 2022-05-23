/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package myCommunication;

/**
 *
 * @author kikes
 */

import java.io.*;
import java.net.*;

public class TcpServer implements Runnable{
    
    protected int serverPort;
    protected ServerSocket serverSocket = null;
    
    public TcpServer(ServerSocket serverSocket){
        this.serverSocket = serverSocket;
    }
    
    @Override
    public void run() {
        
        boolean running = true;
        while (running) {
            
            try{
                Socket clientSocket = serverSocket.accept();
                
                String fromClient;
                String toClient;
                
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(),true);

                fromClient = in.readLine();
                System.out.println("received: " + fromClient);

                if(fromClient.equals("Hello")) {
                    toClient = "olleH";
                    System.out.println("send olleH");
                    out.println(toClient);
                    
                    TcpClient m = new TcpClient(clientSocket);
                    new Thread(m).start();
                }
                
                else{
                    toClient = "eyB";
                    out.println(toClient);
                    clientSocket.close();
                    System.out.println("client closed");
                }
                
            } catch (IOException e){
                System.out.println(e.getMessage());
            }
        }
    }
}
