
package za.ac.tut.requestprocessor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;
import za.ac.tut.authentificationthread.authentificationThread;
import za.ac.tut.user.User;
import za.ac.tut.userthread.UserThread;

public class RequestProcessor {
    BufferedReader in =null;
    PrintWriter out =null;
    List<UserThread> userThreads = new ArrayList<>();
    
    public RequestProcessor(){
    
    }
  
    
    public void execute(){
    ServerSocket s =null;
    Socket socket =null;
   
    
    try{
    s = new ServerSocket(9191);
    }catch(Exception ex){
    System.out.println(ex);
    }
    
    while(true){
        
    try{
    socket = s.accept();
    System.out.println("new client detected...");
    User user = new User();
    
   // new authentificationThread( socket, user).start();
     
    RequestProcessor processor = new RequestProcessor();
    UserThread userT = new UserThread( socket,processor,userThreads);
    userThreads.add(userT);
    userT.start();
    
      
    }catch(Exception ex){
    System.out.println(ex);
    }
    
   }
    }
    
    public void broadcact(String message,List<UserThread> userThreads ){
   
       String[] token = message.split("-");
        System.out.println("broadcasting... token: "+message);
       for(UserThread  user: userThreads){
          // search the receiver
         if(user.getUser().getUserName().compareTo(token[2])==0){
            user.sendMessage(message);
        System.out.println("broadcasting finish...");
         }
       }
      
   }
    
    
    
    public void broadcactNotification(String message,List<UserThread> userThreads ){
   
       String[] token = message.split("-");
        System.out.println("broadcasting... token: "+message);
       for(UserThread  user: userThreads){
           
            user.sendMessage(message);
            System.out.println("broadcasting finish...");
       
       }
      
   }
    
    public void disconnectUser(String userData,List<UserThread> userThreads ){
      String[] token = userData.split("-");
        //System.out.println("broadcasting... token: "+message);
       for(UserThread  user: userThreads){
            if(user.getUser().getUserName().compareTo(token[1])==0){
            userThreads.remove(user);
         
         }
       }
    
    }
    
        
    public String whoIsConnected(List<UserThread> userThreads ){
   
      String data="";
       for(int i=0;i<userThreads.size();i++){
         data = data+(userThreads.get(i).getUser().getUserName()).concat("-");
         }
       
       return data;
     }
   
    
     
    
   
}
