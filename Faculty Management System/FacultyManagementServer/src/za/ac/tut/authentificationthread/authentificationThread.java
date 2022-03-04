/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package za.ac.tut.authentificationthread;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import za.ac.tut.user.User;
import za.ac.tut.userthread.UserThread;

/**
 *
 * @author Rentex
 */
public class authentificationThread extends Thread{
    Socket socket;
  
    BufferedReader in =null;
    PrintWriter out =null;
    User user;
    
    
    public authentificationThread(Socket socket,User user){
      
      this.socket=socket;
      this.user = user;
    }
    
     public Connection getConnection(){
    Connection connect=null;
    String URL="jdbc:mysql://localhost:3306/facultydatabase";
    String USER ="root";
    String PASSWD="87654321";
    
    try{
     DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());
    connect = DriverManager.getConnection(URL, USER, PASSWD);
    }catch(Exception ex){
    System.out.println(ex);
    }
    return connect;
    }
    
 
    public int login(Socket socket,User user){
    
    int result=0;
        try{
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),true);
        
          
    
        Connection connect = getConnection();
        
        Statement statement = connect.createStatement();
    String  dataToSend ="";    
    String sql ="select * from users_id where user_name ='"+user.getUserName()+"' AND password ='"+user.getPassword()+"'";
    ResultSet rs = statement.executeQuery(sql);
    
    if(rs.next()==true){
     // user found   
     result=1;
    }
    out.println(result);
    
    }catch(Exception ex){
        System.out.println(ex);
        }
     return result;
    }
    
     
    
    public void recoverPassword(Socket socket, User user){
          try{
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),true);
        
       // String userData = in.readLine();
        
       // String userName =  userData;
        
        
        Connection connect = getConnection();
        Statement statement = connect.createStatement();
        String  dataToSend ="";  
        
        String sql ="select * from users_id where user_name ='"+user.getUserName()+"' ";
        ResultSet rs = statement.executeQuery(sql);
        System.out.println("search: "+user.getUserName());
    
    if(rs.next()!=false){
         
    dataToSend=  rs.getString("password");
    }else{
     
    dataToSend=  "invalid username";
    }
    
    out.println(dataToSend);
    System.out.println("recored password: "+dataToSend);
    
        }catch(Exception ex){
        System.out.println("recover passwd: "+ex);
        }
    }
    
    public void run(){
      int option;
  
      try{
        
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
       }catch(Exception ex){
    System.out.println(ex);
    }
      
      while(true){
    try{
        
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),true);
        
        String dataReceived = in.readLine();
        System.out.println("data: "+dataReceived);
        String[] userData=dataReceived.split("-");
        option = Integer.parseInt(userData[0]);
        
     // if option = 1 : login
   while(option!=3){
    if(option==1){
         
    String userName = userData[1];
    int password =  Integer.parseInt(userData[2]);
    User user= new User(userName,password);
     this.user = user;
     int result=0;
     
     result = login( socket, user);
     
     if(result==1){
     option=3;
     }
    
      // if option = 2 : recovert password
    }else{
      String userName = userData[1];
      User user= new User();
      user.setUserName(userName);
      this.user = user;
     System.out.println("user name: r"+user.getUserName());
      recoverPassword( socket,user);
      
    }
    
     dataReceived = in.readLine();
     System.out.println("data: "+dataReceived);
      userData=dataReceived.split("-");
     option = Integer.parseInt(userData[0]);
   }
        
    }catch(Exception ex){
    System.out.println("run part: "+ex);
    }
          
      
    }
    }
}
