package org.d3ifcool.alert.model;

/**
 * Created by Sholeh Hermawan on 10/31/2017.
 */

public class Akun {
    private String idAkun;
    private String statusAkun;
    private String namaAkun;
    double latitude, longitude;

    public Akun() {
    }

    public Akun(String idAkun, String statusAkun) {
        this.idAkun = idAkun;
        this.statusAkun = statusAkun;
    }

    public Akun(String namaAkun, double latitude, double longitude) {
        this.namaAkun = namaAkun;
        this.latitude = latitude;
        this.longitude = longitude;
    }
    public String getIdAkun() {
        return idAkun;
    }

    public String getStatusAkun() {
        return statusAkun;
    }

    public String getNamaAkun() {
        return namaAkun;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
