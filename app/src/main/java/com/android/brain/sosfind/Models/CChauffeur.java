package com.android.brain.sosfind.Models;

/**
 * Created by Brain on 13/04/2019.
 */

public class CChauffeur extends CBase
{
    private String mail;
    private String plaque;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLontitude() {
        return lontitude;
    }

    public void setLontitude(String lontitude) {
        this.lontitude = lontitude;
    }

    private String status;

    public String getLatiude() {
        return latiude;
    }

    public void setLatiude(String latiude) {
        this.latiude = latiude;
    }

    private String latiude;
    private String lontitude;

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPlaque() {
        return plaque;
    }

    public void setPlaque(String plaque) {
        this.plaque = plaque;
    }
}
