package com.amazonaws.app.socialnews;

public class Comment {
    String id, message, commenter;

    public Comment(String id, String message, String commenter) {
        this.id = id;
        this.message = message;
        this.commenter = commenter;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCommenter() {
        return commenter;
    }

    public void setCommenter(String commenter) {
        this.commenter = commenter;
    }
}
