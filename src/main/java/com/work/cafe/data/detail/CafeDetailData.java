
package com.work.cafe.data.detail;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CafeDetailData {

    @SerializedName("result")
    @Expose
    private Result result;

    @SerializedName("status")
    @Expose
    private String status;


    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
