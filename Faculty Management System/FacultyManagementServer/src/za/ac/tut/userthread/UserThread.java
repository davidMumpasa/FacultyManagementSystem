
package za.ac.tut.userthread;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import za.ac.tut.requestprocessor.RequestProcessor;
import za.ac.tut.user.User;


public class UserThread extends Thread{
    Socket socket;
    BufferedReader in =null;
    PrintWriter out =null;
    User user;
    List<UserThread>userThreads;
    RequestProcessor processor;
    public UserThread(Socket socket,RequestProcessor processor, List<UserThread>userThreads){
      this.socket=socket;
      //this.user = user;
      this.processor = processor;
      this.userThreads = userThreads;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
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
 // GET CLASS LIST OPTION: 3
    
    public void getClassList(Socket socket){
    String name;
     int id=0;
     String classList ="";
        try {
            
            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),true);
        
             Connection connect =getConnection();
             Statement stmt = connect.createStatement();
             String sql="select * from student";
             ResultSet rs = stmt.executeQuery(sql);
             
             while(rs.next()!=false){
             name = rs.getString("name");
             id = rs.getInt("id");
             classList= classList+(name+": "+id).concat("-");
             name="";
             id=0;
             }
              //send the class list
             out.println(3+"-"+classList);
             
             
         } catch (Exception ex) {
           System.out.println(ex);
         }
    
    }
    
    // SET MARKS OPTION:2
    public void setStudentMark(Socket socket, String userData){
        try{
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),true);
        
      
        
        // studentNum-module-mark1-mark2-mark3 #finalMark
        String[] token= userData.split("-");
        int studentNum = Integer.parseInt(token[1]);
        String moduleCode =  token[2];
        int mark1 = Integer.parseInt(token[3]);
        int mark2 = Integer.parseInt(token[4]);
        int mark3= Integer.parseInt(token[5]);
        
        int finalMark = ((mark1+mark2+mark3)*100)/300;
        
        
        Connection connect = getConnection();
        String sql="insert into module_data values(?,?,?,?,?,?)";
        PreparedStatement insert = connect.prepareStatement(sql);
        String  dataToSend ="";  
        
        insert.setInt(1, studentNum);
        insert.setString(2, moduleCode);
        insert.setInt(3, mark1);
        insert.setInt(4, mark2);
        insert.setInt(5, mark3);
        insert.setInt(6, finalMark);
        
        insert.executeUpdate();
        dataToSend="MARKS SUCCESSFULLY INSERTED";
        
        out.println(4+"-"+dataToSend);
        }catch(Exception ex){
        System.out.println(ex);
        }
    }
    
    //SERVE MESSAGE OPTION 3
    
    public void serveMessages(Socket socket,RequestProcessor processor, String message){
     try{
  // while(true){
     in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    // out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),true);
     
      
      System.out.println(user.getUserName()+" data: "+message);
      // method from processor class
      processor.broadcact(message,userThreads);
      // store messages to the database
      storeMessage( message);
     // }
     }catch(Exception ex){
     System.out.println(ex);
     }
    }
    
    // send message OPTION: 3.1
     public void sendMessage(String message){
    try{
     
     out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),true);
     out.println(message);
     System.out.println("message response sent");
      
     }catch(Exception ex){
     System.out.println(ex);
     }
           
 }
     // OPTION 3.2
     public void storeMessage(String message){
    try{
      Connection connect = getConnection();
      String[] messageToken= message.split("-");
      String sender=messageToken[1];
      String receiver =messageToken[2];
      String messageDesc = messageToken[3];
      
      String sql = "insert into messages_tbl values(?,?,?)";
      
      PreparedStatement insert = connect.prepareStatement(sql);
      
      insert.setString(1, sender);
      insert.setString(2, receiver);
      insert.setString(3, messageDesc);
      
      insert.executeUpdate();
       System.out.println("message successfully saved...");
     }catch(Exception ex){
     System.out.println(ex);
     }
     
     }
     
     
     public void serveNotification(Socket socket,RequestProcessor processor, String message){
      try{
    
      System.out.println(user.getUserName()+" data: "+message);
       
      processor.broadcactNotification(message, userThreads);
       
       Connection connect = getConnection();
      String[] messageToken= message.split("-");
      String sender=messageToken[1];
      String receiver ="public";
      String messageDesc = messageToken[2];
      
      String sql = "insert into messages_tbl values(?,?,?)";
      
      PreparedStatement insert = connect.prepareStatement(sql);
      
      insert.setString(1, sender);
      insert.setString(2, receiver);
      insert.setString(3, messageDesc);
      
      insert.executeUpdate();
       System.out.println("message successfully saved...");
      
     }catch(Exception ex){
     System.out.println(ex);
     }
     }
 
     
  public void getModuleData(Socket socket, String userData){
      
        int mark1=0;
        int mark2=0;
        int mark3=0;
        Double finalMark =0.0;
      
  try{
      in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),true);
     
      String[] data = userData.split("-");
      String studentName =  data[1];
      String moduleName= data[2];
      String dataToSend = "";
      Connection connect = getConnection();
      String sql ="select * from module_data where name= '"+studentName+"' and module_name=  '"+moduleName+"'";
      
      Statement stmt = connect.createStatement();
      
      ResultSet rs = stmt.executeQuery(sql);
      
      if(rs.next()!=false){
      mark1 = rs.getInt("test_1");
      mark2 = rs.getInt("test_2");
      mark3= rs.getInt("test_3");
      finalMark = rs.getDouble("final_mark");
      
       dataToSend = 6+"-"+"FOUND"+"-"+studentName+"-"+moduleName+"-"+mark1+"-"+mark2+"-"+mark3+"-"+finalMark;
      
      }else{
       dataToSend= 6+"-"+"YOUR MARKS ARE NOT AVAILABLE YET";
      }
      
      out.println(dataToSend);
      System.out.println("module data successfully sent ...");
     }catch(Exception ex){
     System.out.println(ex);
     }
  }   
     
   // get specific student data OPTION 5
  
  public void getStudent(Socket socket,int id){
  
        String name="";
        int age=0;
        String email="";
        String cellphone ="";
        String address ="";
      
  try{
      in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),true);
     
      
      
      Connection connect = getConnection();
      String sql ="select * from student where id = '"+id+"'";
      
      Statement stmt = connect.createStatement();
      
      ResultSet rs = stmt.executeQuery(sql);
      
      while(rs.next()!=false){
      name = rs.getString("name");
      age = rs.getInt("age");
      email = rs.getString("email");
      cellphone = rs.getString("cellphone");
      address = rs.getString("address");
      }
      
      String dataToSend = 7+"-"+name+"-"+id+"-"+age+"-"+email+"-"+cellphone+"-"+address;
      out.println(dataToSend);
      System.out.println("student data successfully sent ...");
     }catch(Exception ex){
     System.out.println(ex);
     }
      
      
      
  }
  
  public void seeConnectedUser(Socket socket,RequestProcessor processor){
  try{
      in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),true);
      String dataToSend = processor.whoIsConnected(userThreads);
      
      out.println(8+"-"+dataToSend);
       System.out.println("connected user resoinse sent..");
     }catch(Exception ex){
     System.out.println(ex);
     }
      
     }
   
public void logoutUser(Socket socket,RequestProcessor processor,String userData){
       processor.disconnectUser( userData,userThreads );
}  
     
     public void run(){
     int option=0; 
     String dataReceived="";
     String[] userData=new String[15];
     int id =0;
       /*try{
     
         
        }catch(Exception ex){
        System.out.println(ex);
        }*/
       
       while(true){
              
       try{
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
         dataReceived = in.readLine();
        System.out.println("all data received...: "+dataReceived);
        userData=dataReceived.split("-");
        option = Integer.parseInt(userData[0]);
    
   
    int result=0;
   
   if(option==1){
         
    String userName = userData[1];
    int password =  Integer.parseInt(userData[2]);
    User user= new User(userName,password);
    this.user= user ;
  
     
     result = login( socket, user);
     
    
      // if option = 2 : recovert password
    }else if(option==2){
      String userName = userData[1];
      User user= new User();
      user.setUserName(userName);
      
     System.out.println("user name: recov"+user.getUserName());
      recoverPassword( socket,user);
    
        }else if(option==3){
         getClassList(socket);
         
        }else if(option==4){
            
        setStudentMark(socket,dataReceived);
        }else if(option==5){
          
         serveMessages( socket, processor,dataReceived);  
         
        }else if(option==6){
         getModuleData( socket,dataReceived);
       
        }else if(option==7){
         System.out.println("OPTION: 7");
         id =Integer.parseInt(userData[1]);
         getStudent( socket,id);
       
        }else if (option==8){
        seeConnectedUser( socket, processor);
        }else if (option==9){
        serveNotification( socket, processor,  dataReceived);
        }else{
        logoutUser( socket, processor, dataReceived);
        }  
           
          
       
       }catch(Exception ex){
        System.out.println(ex);
        }
       
          
       }
     }
     
    
    
}
