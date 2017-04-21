package com.omnihealthgroup.reshining.custom.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.omnihealthgroup.reshining.custom.object.UserData;

import java.util.LinkedList;
import java.util.List;


/**
 * Created by Administrator on 2016/8/2.
 */
public class UserDataDAO {
    //    private static final String TAG = "UserDataDAO";
    private static String TAG = UserDataDAO.class.getSimpleName();

    /**
     * 表格名稱
     */
    public final static String TABLE_NAME = "UserData";  // 總覽

    /**
     * 欄位名稱
     */
    public static final String KEY_ID = "_id";

    public final static String COL_UNIQUELD = "UNIQUELD"; //系統唯一識別碼
    public final static String COL_RFID = "RFID"; //RFID卡號
    public final static String COL_IDNO = "IDNO"; //身分證號碼
    public final static String COL_NAME_SUR = "SUR_NAME";//姓氏
    public final static String COL_NAME_FIRST = "FIRST_NAME";//名字
    public final static String COL_NAME = "NAME";//姓名
    public final static String COL_NICK_NAME = "NICK_NAME";//英文姓名
    public final static String COL_GENDER = "GENDER";//性別，說明：[Unknow=未知(預設)]，[Male=男]，[Female=女]
    public final static String COL_BIRTHDAY = "BIRTHDAY"; //出生年月日，格式:yyyy-MM-dd HH:mm:ss.fff
    public final static String COL_NATIONALITY = "NATIONALITY"; //國籍，說明：[0=未知(預設)]，[Taiwan=台灣]
    public final static String COL_PHONE = "PHONE";//家裡電話號碼
    public final static String COL_MOBILE = "MOBILE";//手機號碼
    public final static String COL_EMAIL = "EMAIL"; //電子郵件信箱
    public final static String COL_BLOOD = "BLOOD"; //血型，說明：[0=未知(預設)]，[A]，[B]，[O]，[AB]
    public final static String COL_MARRIED = "MARRIED"; //婚姻狀況，說明：[0=未知(預設)]，[Unmarried=未婚]，[Married=已婚]
    public final static String COL_RHTYPE = "RHTYPE"; //RH血型，說明：[0=未知(預設)]，[Rh+]，[Rh-]
    public final static String COL_HEIGHT = "HEIGHT";//身高(cm)，說明：可為空，數值1~250
    public final static String COL_WEIGHT = "WEIGHT"; //體重(kg)，說明：可為空，數值1~250
    public final static String COL_PHYSICIAN = "PHYSICIAN"; //主治醫師
    public final static String COL_NURSE = "NURSE"; //護理師
    public final static String COL_DIETITIAN = "DIETITIAN"; //營養師

    //使用者專案照護服務設定
    public final static String COL_BLOODPRESSUREENABLED = "BLOODPRESSUREENABLED"; //啟用血壓照護
    public final static String COL_BLOODGLUCOSEENABLED = "BLOODGLUCOSEENABLED"; //啟用血糖照護
    public final static String COL_BODYWEIGHTENABLED = "BODYWEIGHTENABLED"; //啟用體重照護
    public final static String COL_DIETENABLED = "DIETENABLED"; //啟用飲食照護
    public final static String COL_EXERCISEENABLED = "EXERCISEENABLED"; //啟用運動照護
    public final static String COL_SLUMBERENABLED = "SLUMBERENABLED"; //啟用睡眠照護
    public final static String COL_LESIONSENABLED = "LESIONSENABLED"; //啟用病灶照護
    public final static String COL_DIETSUGGESTCALORIES = "DIETSUGGESTCALORIES"; //飲食照護建議卡路里(kcal)，說明：可為空
    public final static String COL_DIETIDEALBODYWEIGHT = "DIETIDEALBODYWEIGHT"; //飲食照護理想體重(kg)，說明：可為空
    public final static String COL_DIETINTENTBODYWEIGHT = "DIETINTENTBODYWEIGHT"; //飲食照護目標體重(kg)，說明：可為空

    // UID : 登入帳號
    public final static String COL_UID = "UID";
    // PASSWORD: 密碼
    public final static String COL_PASSWORD = "PASSWORD";
    // AREA :居住縣市
    public final static String COL_AREA = "AREA";
    // AC_HIGH: 飯前血糖
    public final static String COL_ACHIGH = "AC_HIGH";
    // AC_LOW: 飯後血糖
    public final static String COL_ACLOW = "AC_LOW";
    // BHP: 收縮壓
    public final static String COL_BHP = "BHP";
    // BLP: 舒張壓
    public final static String COL_BLP = "BLP";
    // REMEMBER_USER:記憶帳號密碼
    public final static String COL_REMEMBER_USER = "REMEMBER_USER";
    // TYPE:帳號型態
    public final static String COL_TYPE = "TYPE";
    // UNIT:單位名稱
    public final static String COL_UNIT = "UNIT";

    public static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                    KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_UNIQUELD + " TEXT, " +
                    COL_RFID + " TEXT, " +
                    COL_IDNO + " TEXT, " +
                    COL_UID + " TEXT, " +
                    COL_PASSWORD + " TEXT, " +
                    COL_NAME_SUR + " TEXT, " +
                    COL_NAME_FIRST + " TEXT, " +
                    COL_NAME + " TEXT, " +
                    COL_NICK_NAME + " TEXT, " +
                    COL_GENDER + " TEXT, " +
                    COL_BIRTHDAY + " TEXT, " +
                    COL_NATIONALITY + " TEXT, " +
                    COL_PHONE + " TEXT, " +
                    COL_MOBILE + " TEXT, " +
                    COL_EMAIL + " TEXT, " +
                    COL_BLOOD + " TEXT, " +
                    COL_MARRIED + " TEXT, " +
                    COL_RHTYPE + " TEXT, " +
                    COL_HEIGHT + " TEXT, " +
                    COL_WEIGHT + " TEXT, " +

                    COL_PHYSICIAN + " TEXT, " +
                    COL_NURSE + " TEXT, " +
                    COL_DIETITIAN + " TEXT, " +

                    COL_BLOODPRESSUREENABLED + " TEXT, " +
                    COL_BLOODGLUCOSEENABLED + " TEXT, " +
                    COL_BODYWEIGHTENABLED + " TEXT, " +
                    COL_DIETENABLED + " TEXT, " +
                    COL_EXERCISEENABLED + " TEXT, " +
                    COL_SLUMBERENABLED + " TEXT, " +
                    COL_LESIONSENABLED + " TEXT, " +
                    COL_DIETSUGGESTCALORIES + " TEXT, " +
                    COL_DIETIDEALBODYWEIGHT + " TEXT, " +
                    COL_DIETINTENTBODYWEIGHT + " TEXT)";

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * 資料庫物件
     */
    private SQLiteDatabase db;

    /**
     * 建構子，一般的應用都不需要修改
     *
     * @param context 被調用的畫面
     */
    public UserDataDAO(Context context) {
        db = HealthCareDBHelper.getDatabase(context);
    }

    /**
     * 確認DB是否在開啟狀態
     *
     * @return true：開啟<br/>false：關閉
     */
    public boolean dbIsOpen() {
        return db.isOpen();
    }

    /**
     * 關閉資料庫，一般的應用都不需要修改
     */
    public void closeDB() {
        db.close();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 新增參數指定的物件
     */
    public synchronized void insert(UserData userData) {
        // 建立準備新增資料的ContentValues物件
        ContentValues cv = new ContentValues();

        // 加入ContentValues物件包裝的新增資料
        // 第一個參數是欄位名稱， 第二個參數是欄位的資料
        cv.put(COL_UNIQUELD, userData.getUserUniqueId());
        cv.put(COL_RFID, userData.getRfid());
        cv.put(COL_IDNO, userData.getUserIDNO());
        cv.put(COL_UID, userData.getUID());
        cv.put(COL_PASSWORD, userData.getPassword());
        cv.put(COL_NAME_SUR, userData.getName_sur());
        cv.put(COL_NAME_FIRST, userData.getName_first());
        cv.put(COL_NAME, userData.getName());
        cv.put(COL_NICK_NAME, userData.getNickname());
        cv.put(COL_GENDER, userData.getGender());
        cv.put(COL_BIRTHDAY, userData.getBirthday());
        cv.put(COL_NATIONALITY, userData.getUserNationality());
        cv.put(COL_PHONE, userData.getPhone());
        cv.put(COL_MOBILE, userData.getMobile());
        cv.put(COL_EMAIL, userData.getEmail());
        cv.put(COL_BLOOD, userData.getUserBlood());
        cv.put(COL_MARRIED, userData.getUserMarried());
        cv.put(COL_RHTYPE, userData.getUserRhType());
        cv.put(COL_HEIGHT, userData.getHeight());
        cv.put(COL_WEIGHT, userData.getWeight());

        cv.put(COL_PHYSICIAN, userData.getPhysician());
        cv.put(COL_NURSE, userData.getNurse());
        cv.put(COL_DIETITIAN, userData.getDietitian());

        cv.put(COL_BLOODPRESSUREENABLED, userData.getBloodpressureenabled());
        cv.put(COL_BLOODGLUCOSEENABLED, userData.getBloodglucoseenabled());
        cv.put(COL_BODYWEIGHTENABLED, userData.getBodyweightenabled());
        cv.put(COL_DIETENABLED, userData.getDietenabled());
        cv.put(COL_EXERCISEENABLED, userData.getExerciseenabled());
        cv.put(COL_SLUMBERENABLED, userData.getSlumberenabled());
        cv.put(COL_LESIONSENABLED, userData.getLesionsenabled());
        cv.put(COL_DIETSUGGESTCALORIES, userData.getDietsuggestcalories());
        cv.put(COL_DIETIDEALBODYWEIGHT, userData.getDietidealbodyweight());
        cv.put(COL_DIETINTENTBODYWEIGHT, userData.getDietintentbodyweight());
        // 新增一筆資料並取得編號
        // 第一個參數是表格名稱
        // 第二個參數是沒有指定欄位值的預設值
        // 第三個參數是包裝新增資料的ContentValues物件
        long id = db.insert(TABLE_NAME, null, cv);
        userData.set_id(id);
    }

    /**
     * 修改參數指定的物件
     */
    public synchronized boolean update(UserData userData) {
        // 建立準備新增資料的ContentValues物件
        ContentValues cv = new ContentValues();
        String where = null;
        // 加入ContentValues物件包裝的新增資料
        // 第一個參數是欄位名稱， 第二個參數是欄位的資料
        cv.put(COL_UNIQUELD, userData.getUserUniqueId());
        cv.put(COL_RFID, userData.getRfid());
        cv.put(COL_IDNO, userData.getUserIDNO());
        cv.put(COL_UID, userData.getUID());
        cv.put(COL_PASSWORD, userData.getPassword());
        cv.put(COL_NAME_SUR, userData.getName_sur());
        cv.put(COL_NAME_FIRST, userData.getName_first());
        cv.put(COL_NAME, userData.getName());
        cv.put(COL_NICK_NAME, userData.getNickname());
        cv.put(COL_GENDER, userData.getGender());
        cv.put(COL_BIRTHDAY, userData.getBirthday());
        cv.put(COL_NATIONALITY, userData.getUserNationality());
        cv.put(COL_PHONE, userData.getPhone());
        cv.put(COL_MOBILE, userData.getMobile());
        cv.put(COL_EMAIL, userData.getEmail());
        cv.put(COL_BLOOD, userData.getUserBlood());
        cv.put(COL_MARRIED, userData.getUserMarried());
        cv.put(COL_RHTYPE, userData.getUserRhType());
        cv.put(COL_HEIGHT, userData.getHeight());
        cv.put(COL_WEIGHT, userData.getWeight());

        cv.put(COL_PHYSICIAN, userData.getPhysician());
        cv.put(COL_NURSE, userData.getNurse());
        cv.put(COL_DIETITIAN, userData.getDietitian());

        cv.put(COL_BLOODPRESSUREENABLED, userData.getBloodpressureenabled());
        cv.put(COL_BLOODGLUCOSEENABLED, userData.getBloodglucoseenabled());
        cv.put(COL_BODYWEIGHTENABLED, userData.getBodyweightenabled());
        cv.put(COL_DIETENABLED, userData.getDietenabled());
        cv.put(COL_EXERCISEENABLED, userData.getExerciseenabled());
        cv.put(COL_SLUMBERENABLED, userData.getSlumberenabled());
        cv.put(COL_LESIONSENABLED, userData.getLesionsenabled());
        cv.put(COL_DIETSUGGESTCALORIES, userData.getDietsuggestcalories());
        cv.put(COL_DIETIDEALBODYWEIGHT, userData.getDietidealbodyweight());
        cv.put(COL_DIETINTENTBODYWEIGHT, userData.getDietintentbodyweight());

        // 設定條件為Title，格式為「欄位名稱=開立科別＋床號＋姓名」
        where = KEY_ID + " = " + userData.get_id();

        // 執行修改資料並回傳修改的資料數量是否成功
        return db.update(TABLE_NAME, cv, where, null) > 0;
    }

    public synchronized boolean deleteAll() {
        return db.delete(TABLE_NAME, null, null) > 0;
    }

    /**
     * 讀取所有記事資料
     */
    public List<UserData> getuserdata() {
        List<UserData> resultList = new LinkedList<>();
        db.beginTransaction();
        String where = null;
        Cursor cursor = db.query(
                //                    TABLE_ARRAY[tableNumber], null, DATE_COLUMN + " LIKE?", new String[]{todayTime + "%"}, null, null, null, null);
                true, TABLE_NAME, null, where, null, null, null, KEY_ID + " ASC", null);

        // 如果有查詢結果
        while (cursor.moveToNext()) {
            // 讀取包裝一筆資料的物件
            resultList.add(getRecord(cursor));
        }
        //關閉Cursor物件
        cursor.close();
        db.endTransaction();

        return resultList;
    }

    // get user uid
    public synchronized String getUID() {
        //Log.i(TAG,"getUID()");
        //        db = openDatabase();
        db.beginTransaction();
        String u = null;
        SQLiteDatabase db = null;// openDatabase();
        try {
            Cursor uCursor = db.query(true,
                    TABLE_NAME,
                    new String[]{COL_NAME},
                    null,
                    null,
                    null,
                    null,
                    null, null);
            //Log.i(TAG,"Cursor="+uCursor );
            if (uCursor != null && uCursor.getCount() > 0) {
                uCursor.moveToFirst();
                u = uCursor.getString(0);
            }
            if (uCursor != null) {
                uCursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "getUID Fail() :" + e.getMessage());
        } finally {
            //            if (null != db) {
            //                db.close();
            //            }
            db.endTransaction();
        }
        return u;
    }

    public UserData getRecord(Cursor cursor) {
        UserData userData = new UserData();
        userData.set_id(cursor.getLong(cursor.getColumnIndex(KEY_ID)));
        userData.setUserUniqueId(cursor.getString(cursor.getColumnIndex(COL_UNIQUELD)));
        userData.setRfid(cursor.getString(cursor.getColumnIndex(COL_RFID)));
        userData.setUserIDNO(cursor.getString(cursor.getColumnIndex(COL_IDNO)));
        userData.setUID(cursor.getString(cursor.getColumnIndex(COL_UID)));
        userData.setPassword(cursor.getString(cursor.getColumnIndex(COL_PASSWORD)));
        userData.setName_sur(cursor.getString(cursor.getColumnIndex(COL_NAME_SUR)));
        userData.setName_first(cursor.getString(cursor.getColumnIndex(COL_NAME_FIRST)));
        userData.setName(cursor.getString(cursor.getColumnIndex(COL_NAME)));
        userData.setNickname(cursor.getString(cursor.getColumnIndex(COL_NICK_NAME)));
        userData.setGender(cursor.getString(cursor.getColumnIndex(COL_GENDER)));
        userData.setBirthday(cursor.getString(cursor.getColumnIndex(COL_BIRTHDAY)));
        userData.setUserNationality(cursor.getString(cursor.getColumnIndex(COL_NATIONALITY)));
        userData.setPhone(cursor.getString(cursor.getColumnIndex(COL_PHONE)));
        userData.setMobile(cursor.getString(cursor.getColumnIndex(COL_MOBILE)));
        userData.setEmail(cursor.getString(cursor.getColumnIndex(COL_EMAIL)));
        userData.setUserBlood(cursor.getString(cursor.getColumnIndex(COL_BLOOD)));
        userData.setUserMarried(cursor.getString(cursor.getColumnIndex(COL_MARRIED)));
        userData.setUserRhType(cursor.getString(cursor.getColumnIndex(COL_RHTYPE)));
        userData.setHeight(cursor.getString(cursor.getColumnIndex(COL_HEIGHT)));
        userData.setWeight(cursor.getString(cursor.getColumnIndex(COL_WEIGHT)));

        userData.setPhysician(cursor.getString(cursor.getColumnIndex(COL_PHYSICIAN)));
        userData.setNurse(cursor.getString(cursor.getColumnIndex(COL_NURSE)));
        userData.setDietitian(cursor.getString(cursor.getColumnIndex(COL_DIETITIAN)));

        userData.setBloodpressureenabled(cursor.getString(cursor.getColumnIndex(COL_BLOODPRESSUREENABLED)));
        userData.setBloodglucoseenabled(cursor.getString(cursor.getColumnIndex(COL_BLOODGLUCOSEENABLED)));
        userData.setBodyweightenabled(cursor.getString(cursor.getColumnIndex(COL_BODYWEIGHTENABLED)));
        userData.setDietenabled(cursor.getString(cursor.getColumnIndex(COL_DIETENABLED)));
        userData.setExerciseenabled(cursor.getString(cursor.getColumnIndex(COL_EXERCISEENABLED)));
        userData.setSlumberenabled(cursor.getString(cursor.getColumnIndex(COL_SLUMBERENABLED)));
        userData.setLesionsenabled(cursor.getString(cursor.getColumnIndex(COL_LESIONSENABLED)));
        userData.setDietsuggestcalories(cursor.getString(cursor.getColumnIndex(COL_DIETSUGGESTCALORIES)));
        userData.setDietidealbodyweight(cursor.getString(cursor.getColumnIndex(COL_DIETIDEALBODYWEIGHT)));
        userData.setDietintentbodyweight(cursor.getString(cursor.getColumnIndex(COL_DIETINTENTBODYWEIGHT)));
        return userData;
    }

    // 取得資料數量
    public int getCount() {
        int result = 0;
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_NAME, null);

        if (cursor.moveToNext()) {
            result = cursor.getInt(0);
        }
        cursor.close();
        return result;
    }


}