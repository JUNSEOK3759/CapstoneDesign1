package com.work.cafe.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.work.cafe.data.detail.Photo;

import java.util.List;

public class CafePreviewData {

    @SerializedName("adr_address")
    @Expose
    private String adrAddress;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("formatted_address")
    @Expose
    private String formattedAddress;

    @SerializedName("user_ratings_total")
    @Expose
    private Integer userRatingsTotal;

    @SerializedName("photo")
    @Expose
    private Photo photo = null;

    @SerializedName("place_id")
    @Expose
    private String placeId;

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getAdrAddress() {
        return adrAddress;
    }

    public void setAdrAddress(String adrAddress) {
        this.adrAddress = adrAddress;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFormattedAddress() {
        return formattedAddress;
    }

    public void setFormattedAddress(String formattedAddress) {
        this.formattedAddress = formattedAddress;
    }

    public Integer getUserRatingsTotal() {
        return userRatingsTotal;
    }

    public void setUserRatingsTotal(Integer userRatingsTotal) {
        this.userRatingsTotal = userRatingsTotal;
    }

    public Photo getPhoto() {
        return photo;
    }

    public void setPhoto(Photo photo) {
        this.photo = photo;
    }
}
