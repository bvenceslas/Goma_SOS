package com.android.brain.sosfind.Models;

/**
 * Created by Brain on 13/04/2019.
 */

public class Cmvt {

    public String datecmd;
    public String depart;
    public String succes;
    public String destination;
    public String details;
    public String montant;


    public Cmvt() {

    }

    public Cmvt(String datecmd, String depart, String succes, String destination, String details, String montant) {
        this.datecmd = datecmd;
        this.depart = depart;
        this.succes = succes;
        this.destination = destination;
        this.details = details;
        this.montant = montant;
    }

    public String getDatecmd() {
        return datecmd;
    }

    public void setDatecmd(String datecmd) {
        this.datecmd = datecmd;
    }

    public String getDepart() {
        return depart;
    }

    public void setDepart(String depart) {
        this.depart = depart;
    }

    public String getSucces() {
        return succes;
    }

    public void setSucces(String succes) {
        this.succes = succes;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getMontant() {
        return montant;
    }

    public void setMontant(String montant) {
        this.montant = montant;
    }
}
