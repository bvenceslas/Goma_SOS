package com.android.brain.sosfind.Models;

/**
 * Created by Brain on 13/04/2019.
 */

public class Ccommande {

    private String idPassager;
    private String idChauffeur;
    private String idCmd;
    private String depart;
    private String destination;
    private String details;


    private String datecmd;
    private double montant;
    private boolean success;

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getIdPassager() {
        return idPassager;
    }

    public void setIdPassager(String idPassager) {
        this.idPassager = idPassager;
    }

    public String getIdChauffeur() {
        return idChauffeur;
    }

    public void setIdChauffeur(String idChauffeur) {
        this.idChauffeur = idChauffeur;
    }

    public String getIdCmd() {
        return idCmd;
    }

    public void setIdCmd(String idCmd) {
        this.idCmd = idCmd;
    }

    public String getDepart() {
        return depart;
    }

    public void setDepart(String depart) {
        this.depart = depart;
    }


    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public double getMontant() {
        return montant;
    }

    public void setMontant(double montant) {
        this.montant = montant;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getDatecmd() {
        return datecmd;
    }

    public void setDatecmd(String datecmd) {
        this.datecmd = datecmd;
    }

}
