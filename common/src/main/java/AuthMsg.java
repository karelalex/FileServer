import java.io.Serializable;

public class AuthMsg implements Serializable {
    public AuthAction getAct() {
        return act;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    private AuthAction act;
     private String username;
     private String password;
     public AuthMsg(AuthAction act){
         this.act=act;
     }
     public AuthMsg (AuthAction act, String username){
         this(act);
         this.username=username;
     }
     public AuthMsg(AuthAction act, String username, String password){
         this(act, username);
         this.password=password;
     }

}
