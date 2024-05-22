package com.textreader.comic;

public class ComicINFO {
    private String tittle = "*ERROR READ*";
    private String author = "*ERROR READ*";
    private String profile = "*ERROR READ*";
    private byte[] icon = null;

    public String getTittle() {
        return tittle;
    }

    public void setTittle(String tittle) {
        this.tittle = tittle;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public byte[] getIcon() {
        return icon;
    }

    public void setIcon(byte[] icon) {
        this.icon = icon;
    }
}
