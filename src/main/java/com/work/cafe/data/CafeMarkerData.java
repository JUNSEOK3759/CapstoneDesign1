package com.work.cafe.data;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CafeMarkerData {

    @SerializedName("cafe_id")
    @Expose
    public String cafeId;

    @SerializedName("cafe_name")
    @Expose
    public String cafeName;

    @SerializedName("cafe_status")
    @Expose
    public CafeStatusData cafeStatusData;

    @SerializedName("cafe_latlng")
    @Expose
    public LatLng cafeLatLng;

    public String getCafeId() {
        return cafeId;
    }

    public void setCafeId(String cafeId) {
        this.cafeId = cafeId;
    }

    public String getCafeName() {
        return cafeName;
    }

    public void setCafeName(String cafeName) {
        this.cafeName = cafeName;
    }

    public CafeStatusData getCafeStatusData() {
        return cafeStatusData;
    }

    public void setCafeStatusData(CafeStatusData cafeStatusData) {
        this.cafeStatusData = cafeStatusData;
    }

    public LatLng getCafeLatLng() {
        return cafeLatLng;
    }

    public void setCafeLatLng(LatLng cafeLatLng) {
        this.cafeLatLng = cafeLatLng;
    }
}
