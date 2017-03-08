package florian.cavasin.channelmessaging;

import java.util.List;

public class MessageContainer {
    private List<Message> messages;

    public MessageContainer(List<Message> messages) {
        this.messages = messages;
    }

    @Override
    public String toString() {
        return "MessageContainer{" +
                "messages=" + messages +
                '}';
    }

    public List<Message> getMessages() {

        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }
}