package org.d3ifcool.alert.model;

/**
 * Created by Novika Dian Renanda on 02/12/2017.
 */

public class Ortu {
    public String namaOrtu;
    public String noHPOrtu;

    public Ortu() {
    }

    public Ortu(String namaOrtu, String noHPOrtu) {
        this.namaOrtu = namaOrtu;
        this.noHPOrtu = noHPOrtu;
    }

    public String getNamaOrtu() {
        return namaOrtu;
    }

    public String getNoHPOrtu() {
        return noHPOrtu;
    }
}
