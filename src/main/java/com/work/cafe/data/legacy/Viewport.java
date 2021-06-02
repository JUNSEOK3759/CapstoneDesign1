
package com.work.cafe.data.legacy;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.work.cafe.data.legacy.Northeast;
import com.work.cafe.data.legacy.Southwest;

public class Viewport {

    @SerializedName("northeast")
    @Expose
    private Northeast northeast;
    @SerializedName("southwest")
    @Expose
    private Southwest southwest;

    public Northeast getNortheast() {
        return northeast;
    }

    public void setNortheast(Northeast northeast) {
        this.northeast = northeast;
    }

    public Southwest getSouthwest() {
        return southwest;
    }

    public void setSouthwest(Southwest southwest) {
        this.southwest = southwest;
    }

}
