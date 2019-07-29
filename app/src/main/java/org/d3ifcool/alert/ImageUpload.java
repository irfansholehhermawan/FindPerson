package org.d3ifcool.alert;

import android.graphics.Bitmap;

/**
 * Created by SALADKING on 28/09/2017.
 */

public class ImageUpload {
    public String nameimage;
    public String url;

    public String getName() {
        return nameimage;
    }

    public String getUrl() {
        return url;
    }

    public ImageUpload(String name, String url) {
        this.nameimage = name;
        this.url = url;
    }

    public ImageUpload(){}
}