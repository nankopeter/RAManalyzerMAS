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

import jade.core.AID;
import jade.lang.acl.ACLMessage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class TcpClient implements Runnable{

    protected Socket clientSocket = null;
    
    public TcpClient(Socket clientSocket){
        this.clientSocket = clientSocket;
    }
    
    @Override
    public void run() {
        
        while(true){
            try{
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                String arg1;
                arg1 = in.readLine();
                System.out.println("Client Says: " + arg1);
                
            } catch(IOException e){
                e.printStackTrace();
            }
        }
        
    }
    
}
