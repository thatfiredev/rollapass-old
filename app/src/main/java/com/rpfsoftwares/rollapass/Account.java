package com.rpfsoftwares.rollapass;

public class Account {
    private int id;
    private String Website;
    private String Username;
    private String Password;

    public Account() {
    }

    @Override
    public String toString() {
        return Website+"    "+
                Username+"    "+
                Password+"    ";
    }

    public Account(int id, String website, String username, String password) {
        this.id = id;
        Website = website;
        Username = username;
        Password = password;
    }

    public Account(String website, String username, String password) {
        Website = website;
        Username = username;
        Password = password;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWebsite() {
        return Website;
    }

    public void setWebsite(String website) {
        Website = website;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }
}
