package com.omnihealthgroup.reshining.custom.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2016/8/2.
 */
public class HealthCareDBHelper extends SQLiteOpenHelper {
    //    private static final String TAG = "HealthCareDBHelper";
    private static String TAG = HealthCareDBHelper.class.getSimpleName();

    private Context context;

    public static final String DATABASE_NAME = "HealthCare.db"; // 資料庫名稱
    public static final int DATABASE_VERSION = 1;   // 資料庫版本，資料結構改變的時候要更改這個數字，通常是加一
    private static SQLiteDatabase healthcareDatabase; // 資料庫物件，固定的欄位變數

    public HealthCareDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);

        this.context = context;
    }

    // 需要資料庫的元件呼叫這個方法，這個方法在一般的應用都不需要修改
    public static SQLiteDatabase getDatabase(Context context) {
        if (healthcareDatabase == null || !healthcareDatabase.isOpen()) {
            System.out.println(TAG + "SQLiteDatabase is close");
            healthcareDatabase = new HealthCareDBHelper(
                    context,
                    DATABASE_NAME,
                    null,
                    DATABASE_VERSION
            ).getWritableDatabase();
        }
        return healthcareDatabase;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        // 建立需要的表格
        db.execSQL(UserDataDAO.CREATE_TABLE);
        //        db.execSQL(BioDataDAO.CREATE_TABLE);
        db.execSQL(MeasureStandardDAO.CREATE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 刪除原有的表格
        db.execSQL("DROP TABLE IF EXISTS " + UserDataDAO.CREATE_TABLE);
        //        db.execSQL("DROP TABLE IF EXISTS " + BioDataDAO.CREATE_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + MeasureStandardDAO.CREATE_TABLE);

        // 呼叫onCreate建立新版的表格
        onCreate(db);
    }
}
