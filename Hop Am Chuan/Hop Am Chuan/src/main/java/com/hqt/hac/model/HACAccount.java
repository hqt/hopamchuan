package com.hqt.hac.model;

public class HACAccount {
    public String username;
    public String password;
    public String email;
    public byte[] image;

    public HACAccount(String username, String password, String email, byte[] image) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.image = image;
    }
}
