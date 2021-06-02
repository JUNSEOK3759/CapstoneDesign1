package com.work.cafe.data;

import java.io.Serializable;

public class CafeStatusData implements Serializable {

    public int personnelStatus;
    public int personnelMax;

    public int getPersonnelStatus() {
        return personnelStatus;
    }

    public void setPersonnelStatus(int personnelStatus) {
        this.personnelStatus = personnelStatus;
    }

    public int getPersonnelMax() {
        return personnelMax;
    }

    public void setPersonnelMax(int personnelMax) {
        this.personnelMax = personnelMax;
    }
}
