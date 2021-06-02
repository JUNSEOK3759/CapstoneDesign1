
package com.work.cafe.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.work.cafe.data.legacy.Location;
import com.work.cafe.data.legacy.Viewport;

public class Geometry {

    @SerializedName("location")
    @Expose
    private Location location;
    @SerializedName("viewport")
    @Expose
    private Viewport viewport;

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Viewport getViewport() {
        return viewport;
    }

    public void setViewport(Viewport viewport) {
        this.viewport = viewport;
    }

}
