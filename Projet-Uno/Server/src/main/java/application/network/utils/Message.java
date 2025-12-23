package application.network.utils;

import application.network.UserConnection;

public class Message {
    private UserConnection user;
    private String type;
    private String content;

    public Message(UserConnection user, String content) {
        this.user = user;

        String[] parts = content.split(" ", 2);
        this.type = parts[0];
        this.content = parts.length > 1 ? parts[1] : "";
    }

    public UserConnection getUser() {
        return user;
    }

    public String getType() {
        return type;
    }

    public String getContent() {
        return content;
    }

    public String message() {
        return user.getUsername() + " : [" + type + "] " + content;
    }
}

