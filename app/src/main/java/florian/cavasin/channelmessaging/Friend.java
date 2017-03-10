package florian.cavasin.channelmessaging;

import java.util.UUID;


public class Friend {
    private int userID;
    private String username;
    private String imageUrl;

    public Friend(int userID, String username, String imageUrl) {
        this.userID = userID;
        this.username = username;
        this.imageUrl = imageUrl;
    }

    public int getUserID() {

        return userID;
    }

    public Friend() {
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}