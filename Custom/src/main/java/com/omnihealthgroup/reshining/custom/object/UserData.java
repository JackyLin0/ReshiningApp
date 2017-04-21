package com.omnihealthgroup.reshining.custom.object;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/8/2.
 */
public class UserData implements Serializable {
    private long _id; //食品分類

    private String userUniqueId; //系統唯一識別碼
    private String rfid; //RFID卡號
    private String userIDNO; //身分證號碼
    private String UID;
    private String password;
    private String name_sur;//姓氏
    private String name_first;//名字
    private String name;//姓名
    private String nickname;//英文姓名
    private String gender;//性別，說明：[Unknow=未知(預設)]，[Male=男]，[Female=女]
    private String birthday; //出生年月日，格式:yyyy-MM-dd HH:mm:ss.fff
    private String userNationality; //國籍，說明：[0=未知(預設)]，[Taiwan=台灣]
    private String phone;//家裡電話號碼
    private String mobile;//手機號碼
    private String email; //電子郵件信箱
    private String userBlood; //血型，說明：[0=未知(預設)]，[A]，[B]，[O]，[AB]
    private String userMarried; //婚姻狀況，說明：[0=未知(預設)]，[Unmarried=未婚]，[Married=已婚]
    private String userRhType; //RH血型，說明：[0=未知(預設)]，[Rh+]，[Rh-]
    private String height;//身高(cm)，說明：可為空，數值1~250
    private String weight; //體重(kg)，說明：可為空，數值1~250
    private String physician; //主治醫師
    private String nurse; //護理師
    private String dietitian; //營養師

    //使用者專案照護服務設定
    private String bloodpressureenabled; //啟用血壓照護
    private String bloodglucoseenabled; //啟用血糖照護
    private String bodyweightenabled; //啟用體重照護
    private String dietenabled; //啟用飲食照護
    private String exerciseenabled; //啟用運動照護
    private String slumberenabled; //啟用睡眠照護
    private String lesionsenabled; //啟用病灶照護
    private String dietsuggestcalories; //飲食照護建議卡路里(kcal)，說明：可為空
    private String dietidealbodyweight; //飲食照護理想體重(kg)，說明：可為空
    private String dietintentbodyweight; //飲食照護目標體重(kg)，說明：可為空


    //    public PatientIndexItem()
    //    {}
    //
    //    public PatientIndexItem(long id, String area, String dept_manager, String station_manager, String vs
    //            , String np, String ns, String emerg)
    //    {
    //        this.setId(id);
    //        this.setArea(area);
    //        this.setDept_manager(dept_manager);
    //        this.setStation_manager(station_manager);
    //        this.setVs(vs);
    //        this.setNp(np);
    //        this.setNs(ns);
    //        this.setEmerg(emerg);
    //    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////

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

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName_sur() {
        return name_sur;
    }

    public void setName_sur(String name_sur) {
        this.name_sur = name_sur;
    }

    public String getName_first() {
        return name_first;
    }

    public void setName_first(String name_first) {
        this.name_first = name_first;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getUserNationality() {
        return userNationality;
    }

    public void setUserNationality(String userNationality) {
        this.userNationality = userNationality;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserBlood() {
        return userBlood;
    }

    public void setUserBlood(String userBlood) {
        this.userBlood = userBlood;
    }

    public String getUserMarried() {
        return userMarried;
    }

    public void setUserMarried(String userMarried) {
        this.userMarried = userMarried;
    }

    public String getUserRhType() {
        return userRhType;
    }

    public void setUserRhType(String userRhType) {
        this.userRhType = userRhType;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getPhysician() {
        return physician;
    }

    public void setPhysician(String physician) {
        this.physician = physician;
    }

    public String getNurse() {
        return nurse;
    }

    public void setNurse(String nurse) {
        this.nurse = nurse;
    }

    public String getDietitian() {
        return dietitian;
    }

    public void setDietitian(String dietitian) {
        this.dietitian = dietitian;
    }

    public String getBloodpressureenabled() {
        return bloodpressureenabled;
    }

    public void setBloodpressureenabled(String bloodpressureenabled) {
        this.bloodpressureenabled = bloodpressureenabled;
    }

    public String getBloodglucoseenabled() {
        return bloodglucoseenabled;
    }

    public void setBloodglucoseenabled(String bloodglucoseenabled) {
        this.bloodglucoseenabled = bloodglucoseenabled;
    }

    public String getBodyweightenabled() {
        return bodyweightenabled;
    }

    public void setBodyweightenabled(String bodyweightenabled) {
        this.bodyweightenabled = bodyweightenabled;
    }

    public String getDietenabled() {
        return dietenabled;
    }

    public void setDietenabled(String dietenabled) {
        this.dietenabled = dietenabled;
    }

    public String getExerciseenabled() {
        return exerciseenabled;
    }

    public void setExerciseenabled(String exerciseenabled) {
        this.exerciseenabled = exerciseenabled;
    }

    public String getSlumberenabled() {
        return slumberenabled;
    }

    public void setSlumberenabled(String slumberenabled) {
        this.slumberenabled = slumberenabled;
    }

    public String getLesionsenabled() {
        return lesionsenabled;
    }

    public void setLesionsenabled(String lesionsenabled) {
        this.lesionsenabled = lesionsenabled;
    }

    public String getDietsuggestcalories() {
        return dietsuggestcalories;
    }

    public void setDietsuggestcalories(String dietsuggestcalories) {
        this.dietsuggestcalories = dietsuggestcalories;
    }

    public String getDietidealbodyweight() {
        return dietidealbodyweight;
    }

    public void setDietidealbodyweight(String dietidealbodyweight) {
        this.dietidealbodyweight = dietidealbodyweight;
    }

    public String getDietintentbodyweight() {
        return dietintentbodyweight;
    }

    public void setDietintentbodyweight(String dietintentbodyweight) {
        this.dietintentbodyweight = dietintentbodyweight;
    }
}
