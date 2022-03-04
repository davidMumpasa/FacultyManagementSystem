 
package za.ac.tut.user;

 
public class User {
   String userName;
   int password;
   
   public User( ){
    
   }
   
   public User(String userName, int password){
   this.userName = userName;
   this.password = password;
   }

    public int getPassword() {
        return password;
    }

    public void setPassword(int password) {
        this.password = password;
    }
   
   

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
   
   
}
