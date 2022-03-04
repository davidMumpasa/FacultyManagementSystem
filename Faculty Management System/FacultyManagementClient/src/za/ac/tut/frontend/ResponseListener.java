
package za.ac.tut.frontend;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;


public class ResponseListener extends Thread {
    Socket socket;
    JTextArea chatField;
    JTextArea connectedUser;
    public ResponseListener(Socket socket, JTextArea chatField, JTextArea connectedUser){
      this.socket=socket;
      this.chatField= chatField;
      this.connectedUser =connectedUser;
    }
    
    public void receiverClassList(String[] responseToken){
     
       String data="";
       for(int i=1;i<responseToken.length;i++){
       data= data +(responseToken[i]).concat("\n");
       }
         
        JOptionPane.showMessageDialog(null,"NAME : ID"+"\n"+"------------------"+"\n"+ data, "CLASS LIST", JOptionPane.INFORMATION_MESSAGE);
       
        //System.out.println("response: "+ response);
    }
    
      public void receiveSetMarkResponse(String[] responseToken){
        JOptionPane.showMessageDialog(null,responseToken[1], "COMFIRMATION", JOptionPane.INFORMATION_MESSAGE);
       
       }
    
    public void receiverStudentData(String[] responseToken){
     String studentData= "NAME: "+responseToken[1]+"\n"+"STUDENT NUM: "+responseToken[2]+"\n"+"AGE: "+responseToken[3]+"\n"+
                            "EMAIL ADDS: "+responseToken[4]+"\n"+"CELLPHONE: "+responseToken[5]+"\n"+"PHYSICAL ADDRESS: "+responseToken[6];
         
    JOptionPane.showMessageDialog(null, studentData, "STUDENT DATA", JOptionPane.INFORMATION_MESSAGE);
        
    
    }
    //option: 5
    public void receiveMessage(String[] responseToken){
     String sender =responseToken[1];
      String msg = responseToken[3];
      String liveMessage="";
      liveMessage="SENDER: "+sender+"\n"+"MESSAGE: "+msg;
      
      chatField.setText(chatField.getText()+"\n\n"+"NEW MESSAGE: "+"\n"+liveMessage);
      System.out.println("SENDER: "+sender+"\n"+"MESSAGE: "+msg);
    }
    
    public void receiveModuleData(String[] responseToken){
    String response ="";
       if(responseToken[1].compareTo("FOUND")==0){
       response="NAME: "+responseToken[2]+"\n"+"MODULE CODE: "+responseToken[3]+"\n"+"TEST 1 : "+responseToken[4]+"\n"+
               "TEST 2 : "+responseToken[5]+"\n"+"TEST 3 : "+responseToken[6]+"\n"+"FINAL MARK : "+responseToken[7];
       
       }else{
       response= responseToken[1];
       }
      
       JOptionPane.showMessageDialog(null,response, "COMFIRMATION", JOptionPane.INFORMATION_MESSAGE);
    }
    
    public void receiveConnectedUser(String[] responseToken){
     String data="";
       for(int i=1;i<responseToken.length;i++){
       data= data +(responseToken[i]).concat("\n");
       }
         
      connectedUser.setText(data);
      System.out.println(data);
    }
    
    public void receiveNotification(String[] responseToken){
      String sender =responseToken[1];
      String msg = responseToken[2];
      String liveMessage="";
        liveMessage="SENDER: "+sender+"\n"+"NOTIFICATION: "+msg;
      
      chatField.setText(chatField.getText()+"\n\n"+"NEW NOTIFICATION:"+"\n"+liveMessage);
      System.out.println("SENDER: "+sender+"\n"+"MESSAGE: "+msg);
    }
    
    public void run(){
    int option=0;
    BufferedReader in =null;
    try{
   in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
   }catch(Exception ex){
 System.out.println(ex);
   }    
    while(!socket.isClosed()){
        
        
    try{
    String[] responseToken=in.readLine().split("-");
    option= Integer.parseInt(responseToken[0]);
    
    if(option==3){
     receiverClassList(responseToken);
    }else if(option==4){
    receiveSetMarkResponse(responseToken);
     
    }else if(option==5){
    receiveMessage( responseToken);
     
    }else if(option==6){
    receiveModuleData(responseToken);
    }else if(option==7){
    receiverStudentData( responseToken);
    }else if(option==8){
    receiveConnectedUser( responseToken);
    }else if(option==9){
    receiveNotification( responseToken);
    }
    
   }catch(Exception ex){
       System.out.println(ex);
   }      
   
    
    }
    }
}
