package com.omnihealthgroup.reshining.custom.object;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/8/14.
 */
public class MeasureStandard implements Serializable {
    private long _id; //食品分類

    private String itemKind; //收縮壓
    private String warningMax; //警告值上限
    private String warningMin; //警告值下限
    private String dangerMax;//危險值上限
    private String dangerMin;//危險值下限

    public long get_id() {
        return _id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    public String getItemKind() {
        return itemKind;
    }

    public void setItemKind(String itemKind) {
        this.itemKind = itemKind;
    }

    public String getWarningMax() {
        return warningMax;
    }

    public void setWarningMax(String warningMax) {
        this.warningMax = warningMax;
    }

    public String getWarningMin() {
        return warningMin;
    }

    public void setWarningMin(String warningMin) {
        this.warningMin = warningMin;
    }

    public String getDangerMax() {
        return dangerMax;
    }

    public void setDangerMax(String dangerMax) {
        this.dangerMax = dangerMax;
    }

    public String getDangerMin() {
        return dangerMin;
    }

    public void setDangerMin(String dangerMin) {
        this.dangerMin = dangerMin;
    }
}
