package com.bluebead38.opencvtesseractocr;

import android.media.projection.MediaProjection;

import java.io.Serializable;

public class CustomData  implements Serializable {

    private transient MediaProjection sMediaProjection;

    public MediaProjection getsMediaProjection() {
        return this.sMediaProjection;
    }

    public void setsMediaProjection(MediaProjection sMediaProjection) {
        this.sMediaProjection = sMediaProjection;
    }
}
