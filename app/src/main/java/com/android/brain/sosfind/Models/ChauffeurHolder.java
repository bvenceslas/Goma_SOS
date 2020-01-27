package com.android.brain.sosfind.Models;

/**
 * Created by Brain on 13/04/2019.
 */

public class ChauffeurHolder {

    public String noms;
    public double latitude, longitude;

    public ChauffeurHolder() {

    }

    public ChauffeurHolder(String nom, double latitude, double longitude) {
        this.noms = nom;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
