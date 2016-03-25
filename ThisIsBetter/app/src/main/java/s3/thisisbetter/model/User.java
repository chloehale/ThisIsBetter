package s3.thisisbetter.model;

/**
 * Created by Chloe on 3/17/16.
 */
public class User {

    public static final String EMAIL_KEY = "email";

    private String email;

    public User() { }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
