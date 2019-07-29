package org.d3ifcool.alert.model;

/**
 * Created by Novika Dian Renanda on 25/09/2017.
 */

public class Profile {
    private String idProfile;
    private String profileName;
    private String profileJK;
    private String profileTempat;
    private String profileTglLahir;
    private String profileNoHP;
    private String profileStatus;
    private String companyUrlPhoto;

    public Profile() {
    }

    public Profile(String idProfile) {
        this.idProfile = idProfile;
    }

    public Profile(String profileName, String profileJK, String profileTempat, String profileTglLahir, String profileNoHP, String profileStatus, String companyUrlPhoto) {
        this.profileName = profileName;
        this.profileJK = profileJK;
        this.profileTempat = profileTempat;
        this.profileTglLahir = profileTglLahir;
        this.profileNoHP = profileNoHP;
        this.profileStatus = profileStatus;
        this.companyUrlPhoto = companyUrlPhoto;
    }

    public String getIdProfile() {
        return idProfile;
    }

    public String getProfileName() {
        return profileName;
    }

    public String getProfileJK() {
        return profileJK;
    }

    public String getProfileTempat() {
        return profileTempat;
    }

    public String getProfileTglLahir() {
        return profileTglLahir;
    }

    public String getProfileNoHP() {
        return profileNoHP;
    }

    public String getProfileStatus() {
        return profileStatus;
    }

    public String getCompanyUrlPhoto() {
        return companyUrlPhoto;
    }
}
