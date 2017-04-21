package com.omnihealthgroup.reshining.custom.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.omnihealthgroup.reshining.custom.object.GCMData;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Administrator on 2016/8/2.
 */
public class GCMDataDAO {
    private static final String TAG = "GCMDataDAO";

    /**
     * 表格名稱
     */
    public final static String TABLE_NAME = "GCMData";  // 總覽

    /**
     * 欄位名稱
     */
    public static final String KEY_ID = "_id";

    public final static String COL_UNIQUELD = "UNIQUELD"; //系統唯一識別碼
    public final static String COL_RFID = "RFID"; //RFID卡號
    public final static String COL_IDNO = "IDNO"; //身分證號碼
    public final static String COL_NAME = "NAME";//姓名
    public final static String COL_RECEIVETIME = "RECEIVETIME"; //接收時間
    public final static String COL_TITLE = "TITLE"; //標題
    public final static String COL_BODY = "BODY"; //內容
    public final static String COL_STATUS = "STATUS"; //狀態


    public static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                    KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_UNIQUELD + " TEXT, " +
                    COL_RFID + " TEXT, " +
                    COL_IDNO + " TEXT, " +
                    COL_NAME + " TEXT, " +
                    COL_RECEIVETIME + " TEXT, " +
                    COL_TITLE + " TEXT, " +
                    COL_BODY + " TEXT, " +
                    COL_STATUS + " TEXT)";

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
    public GCMDataDAO(Context context) {
        db = GCMDBHelper.getDatabase(context);
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
    public synchronized void insert(GCMData gcmData) {
        // 建立準備新增資料的ContentValues物件
        ContentValues cv = new ContentValues();

        // 加入ContentValues物件包裝的新增資料
        // 第一個參數是欄位名稱， 第二個參數是欄位的資料
        cv.put(COL_UNIQUELD, gcmData.getUserUniqueId());
        cv.put(COL_RFID, gcmData.getRfid());
        cv.put(COL_IDNO, gcmData.getUserIDNO());
        cv.put(COL_NAME, gcmData.getName());
        cv.put(COL_RECEIVETIME, gcmData.getReceivetime());
        cv.put(COL_TITLE, gcmData.getTitle());
        cv.put(COL_BODY, gcmData.getBody());
        cv.put(COL_STATUS, gcmData.getStatus());

        // 新增一筆資料並取得編號
        // 第一個參數是表格名稱
        // 第二個參數是沒有指定欄位值的預設值
        // 第三個參數是包裝新增資料的ContentValues物件
        long id = db.insert(TABLE_NAME, null, cv);
        gcmData.set_id(id);
    }

    /**
     * 修改參數指定的物件
     */
    public synchronized boolean update(GCMData gcmData) {
        // 建立準備新增資料的ContentValues物件
        ContentValues cv = new ContentValues();
        String where = null;
        // 加入ContentValues物件包裝的新增資料
        // 第一個參數是欄位名稱， 第二個參數是欄位的資料
        cv.put(COL_UNIQUELD, gcmData.getUserUniqueId());
        cv.put(COL_RFID, gcmData.getRfid());
        cv.put(COL_IDNO, gcmData.getUserIDNO());
        cv.put(COL_NAME, gcmData.getName());
        cv.put(COL_RECEIVETIME, gcmData.getReceivetime());
        cv.put(COL_TITLE, gcmData.getTitle());
        cv.put(COL_BODY, gcmData.getBody());
        cv.put(COL_STATUS, gcmData.getStatus());

        // 設定條件為Title，格式為「欄位名稱=開立科別＋床號＋姓名」
        where = KEY_ID + " = " + gcmData.get_id();

        // 執行修改資料並回傳修改的資料數量是否成功
        return db.update(TABLE_NAME, cv, where, null) > 0;
    }

    /**
     * 修改參數指定的物件
     */
    public synchronized boolean update_status(GCMData gcmData) {
        // 建立準備新增資料的ContentValues物件
        ContentValues cv = new ContentValues();
        String where = COL_STATUS + " = " + "0";
        // 加入ContentValues物件包裝的新增資料
        // 第一個參數是欄位名稱， 第二個參數是欄位的資料
        cv.put(COL_UNIQUELD, gcmData.getUserUniqueId());
        cv.put(COL_RFID, gcmData.getRfid());
        cv.put(COL_IDNO, gcmData.getUserIDNO());
        cv.put(COL_NAME, gcmData.getName());
        cv.put(COL_RECEIVETIME, gcmData.getReceivetime());
        cv.put(COL_TITLE, gcmData.getTitle());
        cv.put(COL_BODY, gcmData.getBody());
        cv.put(COL_STATUS, gcmData.getStatus());

        // 設定條件為Title，格式為「欄位名稱=開立科別＋床號＋姓名」
        where = KEY_ID + " = " + gcmData.get_id();

        // 執行修改資料並回傳修改的資料數量是否成功
        return db.update(TABLE_NAME, cv, where, null) > 0;
    }

    public synchronized boolean deleteAll() {
        return db.delete(TABLE_NAME, null, null) > 0;
    }

    /**
     * 讀取所有記事資料
     */
    public List<GCMData> getuserdata() {
        List<GCMData> resultList = new LinkedList<>();
        db.beginTransaction();
        String where = null;
        Cursor cursor = db.query(
                //                    TABLE_ARRAY[tableNumber], null, DATE_COLUMN + " LIKE?", new String[]{todayTime + "%"}, null, null, null, null);
                true, TABLE_NAME, null, where, null, null, null, KEY_ID + " DESC", null); //  DESC - 序列大至小；ASC - 序列小至大
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

    /**
     * 讀取所有記事資料
     */
    public List<GCMData> getuserdata_status() {
        List<GCMData> resultList = new LinkedList<>();
        db.beginTransaction();
        String where = COL_STATUS + " = " + "0";
        Cursor cursor = db.query(
                //                    TABLE_ARRAY[tableNumber], null, DATE_COLUMN + " LIKE?", new String[]{todayTime + "%"}, null, null, null, null);
                true, TABLE_NAME, null, where, null, null, null, KEY_ID + " DESC", null); //  DESC - 序列大至小；ASC - 序列小至大
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

    public GCMData getRecord(Cursor cursor) {
        GCMData gcmData = new GCMData();
        gcmData.set_id(cursor.getLong(cursor.getColumnIndex(KEY_ID)));
        gcmData.setUserUniqueId(cursor.getString(cursor.getColumnIndex(COL_UNIQUELD)));
        gcmData.setRfid(cursor.getString(cursor.getColumnIndex(COL_RFID)));
        gcmData.setUserIDNO(cursor.getString(cursor.getColumnIndex(COL_IDNO)));
        gcmData.setName(cursor.getString(cursor.getColumnIndex(COL_NAME)));
        gcmData.setReceivetime(cursor.getString(cursor.getColumnIndex(COL_RECEIVETIME)));
        gcmData.setTitle(cursor.getString(cursor.getColumnIndex(COL_TITLE)));
        gcmData.setBody(cursor.getString(cursor.getColumnIndex(COL_BODY)));
        gcmData.setStatus(cursor.getString(cursor.getColumnIndex(COL_STATUS)));

        return gcmData;
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