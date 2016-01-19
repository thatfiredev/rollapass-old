package com.rpfsoftwares.rollapass;

public class PreferencesList {
    private String title;
    private String subtitle;

    public PreferencesList(String title,String subtitle)
    {
        this.title=title;
        this.subtitle=subtitle;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }
}
