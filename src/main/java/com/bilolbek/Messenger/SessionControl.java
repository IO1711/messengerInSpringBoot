package com.bilolbek.Messenger;

public class SessionControl {

    private String currentUser;

    private String currentFriend;

    public SessionControl() {
        this.currentFriend="Default";
    }


    public String getCurrentFriend() {
        return currentFriend;
    }

    public void setCurrentFriend(String currentFriend) {
        this.currentFriend = currentFriend;
    }

    public String getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(String currentUser) {
        this.currentUser = currentUser;
    }

    @Override
    public String toString() {
        return "SessionControl{" +
                "currentUser='" + currentUser + '\'' +
                ", currentFriend='" + currentFriend + '\'' +
                '}';
    }
}
