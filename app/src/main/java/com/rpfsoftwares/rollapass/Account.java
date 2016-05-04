package com.rpfsoftwares.rollapass;

public class Account {
    private int id;
    private String website;
    private String username;
    private String password;

    @Override
    public String toString() {
        return website +"    "+
                username +"    "+
                password +"    ";
    }

    public Account(int id, String website, String username, String password) {
        this.id = id;
        this.website = website;
        this.username = username;
        this.password = password;
    }

    public Account(String website, String username, String password) {
        this.website = website;
        this.username = username;
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
