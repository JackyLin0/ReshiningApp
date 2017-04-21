package com.omnihealthgroup.reshining.custom.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.omnihealthgroup.reshining.custom.object.MeasureStandard;

import java.util.LinkedList;
import java.util.List;


/**
 * Created by Administrator on 2016/8/2.
 */
public class MeasureStandardDAO {
    //    private static final String TAG = "MeasureStandardDAO";
    private static String TAG = MeasureStandardDAO.class.getSimpleName();

    /**
     * 表格名稱
     */
    public final static String TABLE_NAME = "MeasureStandard";  // 總覽

    /**
     * 欄位名稱
     */
    public static final String KEY_ID = "_id";

    public final static String COL_ITEMKIND = "itemKind"; //收縮壓
    public final static String COL_WARNINGMAX = "warningMax"; //警告值上限
    public final static String COL_WARNINGMIN = "warningMin"; //警告值下限
    public final static String COL_DANGERMAX = "dangerMax";//危險值上限
    public final static String COL_DANGERMIN = "dangerMin";//危險值下限

    public static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                    KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_ITEMKIND + " TEXT, " +
                    COL_WARNINGMAX + " TEXT, " +
                    COL_WARNINGMIN + " TEXT, " +
                    COL_DANGERMAX + " TEXT, " +
                    COL_DANGERMIN + " TEXT)";

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
    public MeasureStandardDAO(Context context) {
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
    public synchronized void insert(MeasureStandard measureStandard) {
        // 建立準備新增資料的ContentValues物件
        ContentValues cv = new ContentValues();

        // 加入ContentValues物件包裝的新增資料
        // 第一個參數是欄位名稱， 第二個參數是欄位的資料
        cv.put(COL_ITEMKIND, measureStandard.getItemKind());
        cv.put(COL_WARNINGMAX, measureStandard.getWarningMax());
        cv.put(COL_WARNINGMIN, measureStandard.getWarningMin());
        cv.put(COL_DANGERMAX, measureStandard.getDangerMax());
        cv.put(COL_DANGERMIN, measureStandard.getDangerMin());

        // 新增一筆資料並取得編號
        // 第一個參數是表格名稱
        // 第二個參數是沒有指定欄位值的預設值
        // 第三個參數是包裝新增資料的ContentValues物件
        long id = db.insert(TABLE_NAME, null, cv);
        measureStandard.set_id(id);
    }

    /**
     * 修改參數指定的物件
     */
    public synchronized boolean update(MeasureStandard measureStandard) {
        // 建立準備新增資料的ContentValues物件
        ContentValues cv = new ContentValues();
        String where = null;
        // 加入ContentValues物件包裝的新增資料
        // 第一個參數是欄位名稱， 第二個參數是欄位的資料
        cv.put(COL_ITEMKIND, measureStandard.getItemKind());
        cv.put(COL_WARNINGMAX, measureStandard.getWarningMax());
        cv.put(COL_WARNINGMIN, measureStandard.getWarningMin());
        cv.put(COL_DANGERMAX, measureStandard.getDangerMax());
        cv.put(COL_DANGERMIN, measureStandard.getDangerMin());

        // 設定條件為Title，格式為「欄位名稱=開立科別＋床號＋姓名」
        where = KEY_ID + " = " + measureStandard.get_id();

        // 執行修改資料並回傳修改的資料數量是否成功
        return db.update(TABLE_NAME, cv, where, null) > 0;
    }

    public synchronized boolean deleteAll() {
        return db.delete(TABLE_NAME, null, null) > 0;
    }

    /**
     * 讀取所有記事資料
     */
    public List<MeasureStandard> getuserdata() {
        List<MeasureStandard> resultList = new LinkedList<>();
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

    /**
     * 讀取所有記事資料
     */
    public List<MeasureStandard> getuserdata_item(String item) {
        List<MeasureStandard> resultList = new LinkedList<>();
        db.beginTransaction();
        String where = null;
        where = COL_ITEMKIND + " = " + "'" + item + "'";
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

    public MeasureStandard getRecord(Cursor cursor) {
        MeasureStandard measureStandard = new MeasureStandard();
        measureStandard.set_id(cursor.getLong(cursor.getColumnIndex(KEY_ID)));
        measureStandard.setItemKind(cursor.getString(cursor.getColumnIndex(COL_ITEMKIND)));
        measureStandard.setWarningMax(cursor.getString(cursor.getColumnIndex(COL_WARNINGMAX)));
        measureStandard.setWarningMin(cursor.getString(cursor.getColumnIndex(COL_WARNINGMIN)));
        measureStandard.setDangerMax(cursor.getString(cursor.getColumnIndex(COL_DANGERMAX)));
        measureStandard.setDangerMin(cursor.getString(cursor.getColumnIndex(COL_DANGERMIN)));

        return measureStandard;
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