package com.omnihealthgroup.reshining.custom.object;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/8/17.
 */
public class GCMData implements Serializable {
    private long _id; //食品分類

    private String userUniqueId; //系統唯一識別碼
    private String rfid; //RFID卡號
    private String userIDNO; //身分證號碼
    private String name;//姓名
    private String receivetime; //接收時間
    private String title; //標題
    private String body; //內容
    private String status; //狀態

    //////////////////////////////////////////


    public long get_id() {
        return _id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    public String getUserUniqueId() {
        return userUniqueId;
    }

    public void setUserUniqueId(String userUniqueId) {
        this.userUniqueId = userUniqueId;
    }

    public String getRfid() {
        return rfid;
    }

    public void setRfid(String rfid) {
        this.rfid = rfid;
    }

    public String getUserIDNO() {
        return userIDNO;
    }

    public void setUserIDNO(String userIDNO) {
        this.userIDNO = userIDNO;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReceivetime() {
        return receivetime;
    }

    public void setReceivetime(String receivetime) {
        this.receivetime = receivetime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
