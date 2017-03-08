package florian.cavasin.channelmessaging;

public class Message {
    private int userID;
    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    private String messageImageUrl;

    public String getMessageImageUrl() {
        return messageImageUrl;
    }

    public void setMessageImageUrl(String messageImageUrl) {
        this.messageImageUrl = messageImageUrl;
    }

    @Override
    public String toString() {
        return "Message{" +
                "userID=" + userID +
                ", message='" + message + '\'' +
                ", date='" + date + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", messageImageUrl='" + messageImageUrl + '\'' +
                '}';
    }

    public Message(int userID, String message, String date, String imageUrl, String messageImageUrl) {
        this.userID = userID;
        this.message = message;
        this.date = date;
        this.imageUrl = imageUrl;
        this.messageImageUrl = messageImageUrl;
    }

    public int getUserID() {

        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    private String message;
    private String date;
    private String imageUrl;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Message message1 = (Message) o;

        if (userID != message1.userID) return false;
        if (username != null ? !username.equals(message1.username) : message1.username != null)
            return false;
        if (message != null ? !message.equals(message1.message) : message1.message != null)
            return false;
        if (date != null ? !date.equals(message1.date) : message1.date != null) return false;
        return imageUrl != null ? imageUrl.equals(message1.imageUrl) : message1.imageUrl == null;

    }

}