package com.android.brain.sosfind.Models;

/**
 * Created by Brain on 13/04/2019.
 */

public class CBase {

    private String id; private String nom; private String password;
    private String phoneNumber;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    private String image;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber()
    {
        return phoneNumber;
    }

    public void setPhoneNumber(String phone)
    {
        this.phoneNumber = phone;
    }
}
